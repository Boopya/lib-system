-- PATRON --
CREATE TABLE patron (
    loginid      NUMBER(25) NOT NULL,
    firstname    VARCHAR2(20) NOT NULL,
    middlename   VARCHAR2(20),
    lastname     VARCHAR2(20) NOT NULL,
    password     VARCHAR2(50) NOT NULL,
    houseno      VARCHAR2(10),
    street       VARCHAR2(20) NOT NULL,
    barangay     VARCHAR2(20) NOT NULL,
    city         VARCHAR2(20) NOT NULL,
    unpaidfine   NUMBER(11, 2) NOT NULL
);

ALTER TABLE patron ADD CONSTRAINT patron_pk PRIMARY KEY ( loginid );


-- LIBRARIAN --
CREATE TABLE librarian (
    loginid      NUMBER(25) NOT NULL,
    bookaccess   VARCHAR2(3) NOT NULL,
    useraccess   VARCHAR2(3) NOT NULL
);

ALTER TABLE librarian ADD CONSTRAINT librarian_pk PRIMARY KEY ( loginid );

ALTER TABLE librarian
    ADD CONSTRAINT librarian_patron_fk FOREIGN KEY ( loginid )
        REFERENCES patron ( loginid );
        
ALTER TABLE librarian 
    ADD CONSTRAINT bookaccess_check CHECK ( bookaccess IN
        ( '111','110','100','000','001','011','010','101' ));
        
ALTER TABLE librarian 
    ADD CONSTRAINT useraccess_check CHECK ( useraccess IN
        ( '111','110','100','000','001','011','010','101' ));
        
        
-- BOOK --
CREATE TABLE book (
    isbn              NUMBER(13) NOT NULL,
    copynumber        NUMBER(3) NOT NULL,
    title             VARCHAR2(50) NOT NULL,
    publicationyear   NUMBER(4) NOT NULL,
    currentstatus     VARCHAR2(20) NOT NULL,
    statusdate        DATE NOT NULL,
    shelf_shelfid     NUMBER(20) NOT NULL
);

ALTER TABLE book ADD CONSTRAINT book_pk PRIMARY KEY ( isbn,
                                                      copynumber );

ALTER TABLE book
    ADD CONSTRAINT book_shelf_fk FOREIGN KEY ( shelf_shelfid )
        REFERENCES shelf ( shelfid );
        
ALTER TABLE book ADD CONSTRAINT currentstatus_check CHECK ( 
    currentstatus IN ( 'ON-SHELF','ON-HOLD','ON-LOAN' ));
        
        
-- AUTHOR --
CREATE TABLE author (
    authorid     NUMBER(20) NOT NULL,
    firstname    VARCHAR2(20) NOT NULL,
    middlename   VARCHAR2(20),
    lastname     VARCHAR2(20) NOT NULL
);

ALTER TABLE author ADD CONSTRAINT author_pk PRIMARY KEY ( authorid );


-- SHELF --
CREATE TABLE shelf (
    shelfid    NUMBER(20) NOT NULL,
    capacity   NUMBER(3) NOT NULL
);

ALTER TABLE shelf ADD CONSTRAINT shelf_pk PRIMARY KEY ( shelfid );


-- TRANSACTION --
CREATE TABLE transaction (
    transactionid     NUMBER(50) NOT NULL,
    transactiondate   DATE NOT NULL,
    transactionmode   VARCHAR2(20) NOT NULL,
    patron_loginid    NUMBER(25) NOT NULL,
    book_isbn         NUMBER(13) NOT NULL,
    book_copynumber   NUMBER(3) NOT NULL
);

ALTER TABLE transaction
    ADD CONSTRAINT transaction_pk PRIMARY KEY ( transactionid );

ALTER TABLE transaction
    ADD CONSTRAINT transaction_book_fk FOREIGN KEY ( book_isbn,
                                                     book_copynumber )
        REFERENCES book ( isbn,
                          copynumber );

ALTER TABLE transaction
    ADD CONSTRAINT transaction_patron_fk FOREIGN KEY ( patron_loginid )
        REFERENCES patron ( loginid );
        
ALTER TABLE transaction ADD CONSTRAINT transactionmode_check CHECK (
    transactionmode IN ( 'LOAN','RETURN','RESERVE' ));
        
        
-- BOOK AUTHOR --
CREATE TABLE bookauthor (
    book_isbn         NUMBER(13) NOT NULL,
    book_copynumber   NUMBER(3) NOT NULL,
    author_authorid   NUMBER(20) NOT NULL
);

ALTER TABLE bookauthor
    ADD CONSTRAINT bookauthor_pk PRIMARY KEY ( book_isbn,
                                               book_copynumber,
                                               author_authorid );

ALTER TABLE bookauthor
    ADD CONSTRAINT bookauthor_author_fk FOREIGN KEY ( author_authorid )
        REFERENCES author ( authorid );

ALTER TABLE bookauthor
    ADD CONSTRAINT bookauthor_book_fk FOREIGN KEY ( book_isbn,
                                                    book_copynumber )
        REFERENCES book ( isbn,
                          copynumber );