CREATE OR REPLACE PROCEDURE add_user (
    user_id patron.loginid%type,
    user_fname patron.firstname%type,
    user_lname patron.lastname%type,
    user_mname patron.middlename%type,
    user_pass patron.passsword%type,
    user_houseno patron.houseno%type,
    user_st patron.street%type,
    user_brgy patron.barangay%type,
    user_city patron.city%type,
    user_unpaid patron.unpaidfine%type ) IS
BEGIN
    INSERT INTO patron VALUES (
        user_id,
        user_fname,
        user_lname,
        user_mname,
        user_pass,
        user_houseno,
        user_st,
        user_brgy,
        user_city,
        user_unpaid);
END add_user;

CREATE OR REPLACE PROCEDURE edit_user IS
BEGIN
    
END edit_user;