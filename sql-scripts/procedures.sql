-- USER GENERAL PROCEDURES --
CREATE OR REPLACE PROCEDURE add_user (
    user_id patron.loginid%type,
    user_fname patron.firstname%type,
    user_mname patron.middlename%type,
    user_lname patron.lastname%type,
    user_pass patron.password%type,
    user_houseno patron.houseno%type,
    user_st patron.street%type,
    user_brgy patron.barangay%type,
    user_city patron.city%type,
    user_unpaid patron.unpaidfine%type ) IS
BEGIN
    INSERT INTO patron VALUES (
        user_id,
        user_fname,
        user_mname,
        user_lname,
        user_pass,
        user_houseno,
        user_st,
        user_brgy,
        user_city,
        user_unpaid);
END add_user;
/

CREATE OR REPLACE PROCEDURE edit_user (
    old_user_id patron.loginid%type,
    user_id patron.loginid%type,
    user_fname patron.firstname%type,
    user_mname patron.middlename%type,
    user_lname patron.lastname%type,
    user_pass patron.password%type,
    user_houseno patron.houseno%type,
    user_st patron.street%type,
    user_brgy patron.barangay%type,
    user_city patron.city%type,
    user_unpaid patron.unpaidfine%type ) IS
BEGIN
    UPDATE patron SET
        loginid = user_id,
        firstname = user_fname,
        middlename = user_mname,
        lastname = user_lname,
        password = user_pass,
        houseno = user_houseno,
        street = user_st,
        barangay = user_brgy,
        city = user_city,
        unpaidfine = user_unpaid
    WHERE loginid = old_user_id;
END edit_user;
/

CREATE OR REPLACE PROCEDURE delete_user ( 
    user_id patron.loginid%type ) IS
BEGIN
    DELETE FROM patron WHERE loginid = user_id;
END delete_user;
/

-- LIBRARIAN GENERAL PROCEDURES --
CREATE OR REPLACE PROCEDURE add_librarian (
    user_id patron.loginid%type,
    user_fname patron.firstname%type,
    user_mname patron.middlename%type,
    user_lname patron.lastname%type,
    user_pass patron.password%type,
    user_houseno patron.houseno%type,
    user_st patron.street%type,
    user_brgy patron.barangay%type,
    user_city patron.city%type,
    user_unpaid patron.unpaidfine%type,
    patron_access librarian.patronaccess%type,
    lib_access librarian.libaccess%type,
    book_access librarian.bookaccess%type,
    trans_access librarian.transaccess%type ) IS
BEGIN
    add_user(
        user_id,
        user_fname,
        user_mname,
        user_lname,
        user_pass,
        user_houseno,
        user_st,
        user_brgy,
        user_city,
        user_unpaid);
        
    INSERT INTO librarian VALUES (
        user_id,
        patron_access,
        lib_access,
        book_access,
        trans_access);
END add_librarian;
/

CREATE OR REPLACE PROCEDURE edit_librarian (
    old_user_id patron.loginid%type,
    user_id patron.loginid%type,
    user_fname patron.firstname%type,
    user_mname patron.middlename%type,
    user_lname patron.lastname%type,
    user_pass patron.password%type,
    user_houseno patron.houseno%type,
    user_st patron.street%type,
    user_brgy patron.barangay%type,
    user_city patron.city%type,
    user_unpaid patron.unpaidfine%type,
    patron_access librarian.patronaccess%type,
    lib_access librarian.libaccess%type,
    book_access librarian.bookaccess%type,
    trans_access librarian.transaccess%type ) IS
BEGIN
    DELETE FROM librarian WHERE loginid = old_user_id;

    edit_user(
        old_user_id,
        user_id,
        user_fname,
        user_mname,
        user_lname,
        user_pass,
        user_houseno,
        user_st,
        user_brgy,
        user_city,
        user_unpaid);
        
    INSERT INTO librarian VALUES (
        user_id,
        patron_access,
        lib_access,
        book_access,
        trans_access);
END edit_librarian;
/

CREATE OR REPLACE PROCEDURE delete_librarian (
   user_id patron.loginid%type ) IS
BEGIN
    DELETE FROM librarian WHERE loginid = user_id;
    delete_user(user_id);
END delete_librarian;
/

-- BOOK GENERAL PROCEDURES --
CREATE OR REPLACE PROCEDURE add_book (
    p_isbn book.isbn%type,
    p_cpnum book.copynumber%type,
    p_title book.title%type,
    p_pubyear book.publicationyear%type,
    p_curstatus book.currentstatus%type,
    p_statusdate book.statusdate%type,
    p_shelfid book.shelf_shelfid%type ) IS
BEGIN
    INSERT INTO book VALUES (
        p_isbn,
        p_cpnum,
        p_title,
        p_pubyear,
        p_curstatus,
        p_statusdate,
        p_shelfid);
END add_book;
/

CREATE OR REPLACE PROCEDURE edit_book (
    old_isbn book.isbn%type,
    old_cpnum book.copynumber%type,
    p_isbn book.isbn%type,
    p_cpnum book.copynumber%type,
    p_title book.title%type,
    p_pubyear book.publicationyear%type,
    p_curstatus book.currentstatus%type,
    p_statusdate book.statusdate%type,
    p_shelfid book.shelf_shelfid%type ) IS
BEGIN
    UPDATE book SET
        isbn = p_isbn,
        copynumber = p_cpnum,
        title = p_title,
        publicationyear = p_pubyear,
        currentstatus = p_curstatus,
        statusdate = p_statusdate,
        shelf_shelfid = p_shelfid
    WHERE isbn = old_isbn AND 
          copynumber = old_cpnum;
END edit_book;
/

CREATE OR REPLACE PROCEDURE delete_book (
    p_isbn book.isbn%type,
    p_cpnum book.copynumber%type ) IS
BEGIN
    DELETE FROM book 
        WHERE isbn = p_isbn AND copynumber = p_cpnum;
END delete_book;
/


-- TRANSACTION GENERAL PROCEDURE --
CREATE OR REPLACE PROCEDURE add_transaction (
    p_transid transaction.transactionid%type,
    p_transdate transaction.transactiondate%type,
    p_transmode transaction.transactionmode%type,
    p_loginid transaction.patron_loginid%type,
    p_isbn transaction.book_isbn%type,
    p_cpnum transaction.book_copynumber%type ) IS
BEGIN
    INSERT INTO transaction VALUES (
        p_transid,
        p_transdate,
        p_transmode,
        p_loginid,
        p_isbn,
        p_cpnum);
END add_transaction;
/

CREATE OR REPLACE PROCEDURE edit_transaction (
    old_transid transaction.transactionid%type,
    p_transid transaction.transactionid%type,
    p_transdate transaction.transactiondate%type,
    p_transmode transaction.transactionmode%type,
    p_loginid transaction.patron_loginid%type,
    p_isbn transaction.book_isbn%type,
    p_cpnum transaction.book_copynumber%type ) IS
BEGIN
    UPDATE transaction SET
        transactionid = p_transid,
        transactiondate = p_transdate,
        transactionmode = p_transmode,
        patron_loginid = p_loginid,
        book_isbn = p_isbn,
        book_copynumber = p_cpnum
    WHERE transactionid = old_transid;
END edit_transaction;
/

CREATE OR REPLACE PROCEDURE delete_transaction (
    p_transid transaction.transactionid%type ) IS
BEGIN
    DELETE from transaction 
        WHERE transactionid = p_transid;
END delete_transaction;
/


CREATE OR REPLACE PROCEDURE update_book_status (
    p_isbn book.isbn%type,
    p_cpnum book.copynumber%type,
    p_curstatus book.currentstatus%type,
    p_statusdate book.statusdate%type ) IS
BEGIN
    UPDATE book SET
        currentstatus = p_curstatus,
        statusdate = p_statusdate
    WHERE isbn = p_isbn AND 
          copynumber = p_cpnum;
END update_book_status;
/


CREATE OR REPLACE PROCEDURE update_user_fine (
    p_loginid patron.loginid%type,
    p_amount patron.unpaidfine%type ) IS
BEGIN
    UPDATE patron SET
        unpaidfine = unpaidfine + p_amount
    WHERE loginid = p_loginid;
END update_user_fine;
/


CREATE OR REPLACE TRIGGER shelf_capacity_trigger
BEFORE INSERT ON book FOR EACH ROW
DECLARE
v_books shelf.capacity%TYPE := 0;
v_capacity shelf.capacity%TYPE := 0;
BEGIN
    SELECT COUNT(isbn) INTO v_books FROM book 
    WHERE shelf_shelfid = :NEW.shelf_shelfid;

    SELECT capacity INTO v_capacity FROM shelf
    WHERE shelfid = :NEW.shelf_shelfid;

    IF (v_books >= v_capacity) THEN
        RAISE_APPLICATION_ERROR(-20100,'The shelf is at the maximum capacity.');
    END IF;

END shelf_capacity_trigger;
/


CREATE OR REPLACE TRIGGER transaction_numeric_trigger
BEFORE INSERT OR UPDATE ON transaction FOR EACH ROW
BEGIN
    IF (:NEW.transactionid <= 0) THEN
        RAISE_APPLICATION_ERROR(-20200,'Invalid transaction ID.');
    END IF;
END transaction_numeric_trigger;
/


CREATE OR REPLACE TRIGGER user_numeric_trigger
BEFORE INSERT OR UPDATE ON patron FOR EACH ROW
BEGIN
    IF (:NEW.loginid <= 0) THEN
        RAISE_APPLICATION_ERROR(-20300,'Invalid login ID.');
    ELSIF (:NEW.unpaidfine < 0) THEN
        RAISE_APPLICATION_ERROR(-20400,'Invalid unpaid fine.');
    END IF;
END user_numeric_trigger;
/


CREATE OR REPLACE TRIGGER book_numeric_trigger
BEFORE INSERT OR UPDATE ON book FOR EACH ROW
BEGIN
    IF (:NEW.isbn < 1000000000) THEN
        RAISE_APPLICATION_ERROR(-20500,'Invalid ISBN.');
    ELSIF (:NEW.copynumber <= 0) THEN
        RAISE_APPLICATION_ERROR(-20600,'Invalid copy number.');
    ELSIF (:NEW.publicationyear <= 0) THEN
        RAISE_APPLICATION_ERROR(-20700,'Invalid publication year.');
    END IF;
END book_numeric_trigger;
/

COMMIT;