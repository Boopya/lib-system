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


CREATE OR REPLACE TRIGGER transaction_trigger
BEFORE INSERT OR UPDATE ON transaction
FOR EACH ROW
DECLARE
v_status book.currentstatus%TYPE;
v_status_date book.statusdate%TYPE;
v_loginid transaction.patron_loginid%TYPE;
v_loan_count number;
v_return_count number;
v_pending_count number;
v_reserve_count number;
v_loan_interval number;
v_penalty_fee patron.unpaidfine%TYPE;
BEGIN
    IF (:NEW.transactionmode = 'LOAN') THEN
        SELECT currentstatus, statusdate INTO v_status, v_status_date FROM book
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
        SELECT patron_loginid INTO v_loginid FROM transaction
        WHERE transactionmode = 'RESERVE' AND transactiondate = v_status_date;
        
        SELECT COUNT(transactionid) INTO v_loan_count FROM transaction
        WHERE patron_loginid = :NEW.patron_loginid AND (:NEW.transactiondate - transactiondate) <= 7 AND transactionmode = 'LOAN';
        
        SELECT COUNT(transactionid) INTO v_return_count FROM transaction
        WHERE patron_loginid = :NEW.patron_loginid AND (:NEW.transactiondate - transactiondate) <= 7 AND transactionmode = 'RETURN';
        
        v_pending_count := v_loan_count - v_return_count;
        
        IF (v_status = 'ON-LOAN') THEN
            RAISE_APPLICATION_ERROR(-20100,'The book is already loaned.');
        ELSIF (v_status = 'ON-HOLD' AND (:NEW.transactiondate - v_status_date) <= 7 AND v_loginid != :NEW.patron_loginid) THEN
            RAISE_APPLICATION_ERROR(-20200,'The book is reserved to another patron.');
        ELSIF (v_pending_count >= 2) THEN
            RAISE_APPLICATION_ERROR(-20300,'You have reached maximum reserved/loaned books.');
        END IF;
        
        UPDATE book SET
            currentstatus = 'ON-LOAN',
            statusdate = :NEW.transactiondate
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
    ELSIF (:NEW.transactionmode = 'RETURN') THEN
        SELECT currentstatus, statusdate INTO v_status, v_status_date FROM book
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
        SELECT patron_loginid INTO v_loginid FROM transaction
        WHERE transactionmode = 'LOAN' AND transactiondate = v_status_date 
        AND book_isbn = :NEW.book_isbn AND book_copynumber = :NEW.book_copynumber;
        
        IF (v_status != 'ON-LOAN') THEN
            RAISE_APPLICATION_ERROR(-20400,'The book is already returned.');
        ELSIF (:NEW.patron_loginid != v_loginid) THEN
            RAISE_APPLICATION_ERROR(-20500,'You cannot return a book that has been loaned by another patron.');
        END IF;
        
        v_loan_interval := :NEW.transactiondate - v_status_date;
        
        IF (v_loan_interval > 7) THEN
            v_loan_interval := v_loan_interval - 7;
            v_penalty_fee := v_loan_interval * 20;
            
            UPDATE patron SET
                unpaidfine = unpaidfine + v_penalty_fee
            WHERE loginid = :NEW.patron_loginid;
            
        END IF;
        
        UPDATE book SET
            currentstatus = 'ON-SHELF',
            statusdate = :NEW.transactiondate
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
    ELSIF (:NEW.transactionmode = 'RESERVE') THEN
        SELECT currentstatus INTO v_status FROM book
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
         SELECT COUNT(transactionid) INTO v_loan_count FROM transaction
        WHERE patron_loginid = :NEW.patron_loginid AND (:NEW.transactiondate - transactiondate) <= 7 AND transactionmode = 'LOAN';
        
        SELECT COUNT(transactionid) INTO v_return_count FROM transaction
        WHERE patron_loginid = :NEW.patron_loginid AND (:NEW.transactiondate - transactiondate) <= 7 AND transactionmode = 'RETURN';
        
        SELECT COUNT(transactionid) INTO v_reserve_count FROM transaction
        WHERE patron_loginid = :NEW.patron_loginid AND (:NEW.transactiondate - transactiondate) <= 7 AND transactionmode = 'RESERVE';
        
        IF ((v_reserve_count - v_loan_count) < 0) THEN
            v_reserve_count := 0;
        ELSE 
            v_reserve_count := v_reserve_count - v_loan_count;
        END IF;
        
        v_pending_count := (v_loan_count - v_return_count) + v_reserve_count;
        
        IF (v_status != 'ON-SHELF') THEN
            RAISE_APPLICATION_ERROR(-20600,'The book is not available for reserve.');
        ELSIF (v_pending_count >= 2) THEN
            RAISE_APPLICATION_ERROR(-20700,'You have reached maximum reserved/loaned books.');
        END IF;
        
        UPDATE book SET
            currentstatus = 'ON-HOLD',
            statusdate = :NEW.transactiondate
        WHERE isbn = :NEW.book_isbn AND copynumber = :NEW.book_copynumber;
        
    END IF;
END loan_trigger;
/

COMMIT;