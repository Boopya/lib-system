<<<<<<< HEAD
import java.text.SimpleDateFormat;

public interface SQLStatements {
	String[] TABLES = { "TRANSACTION", "PATRON", "BOOK", "LIBRARIAN", "SHELF", "AUTHOR", "BOOKAUTHOR" };

	String[] TRANSACTION_COLUMNS = { "TRANSACTIONID", "TRANSACTIONDATE", "TRANSACTIONMODE", "PATRON_LOGINID", "BOOK_ISBN", "BOOK_COPYNUMBER" };
	String[] PATRON_COLUMNS = { "LOGINID", "FIRSTNAME", "MIDDLENAME", "LASTNAME", "PASSWORD", "HOUSENO", "STREET", "BARANGAY", "CITY", "UNPAIDFINE" };
	String[] BOOK_COLUMNS = { "ISBN", "COPYNUMBER", "TITLE", "PUBLICATIONYEAR", "CURRENTSTATUS", "STATUSDATE", "SHELF_SHELFID" };
	String[] LIBRARIAN_COLUMNS = { "LOGINID", "PATRONACCESS", "LIBACCESS", "BOOKACCESS", "TRANSACCESS" };
	String[] SHELF_COLUMNS = { "SHELFID", "CAPACITY" };
	String[] AUTHOR_COLUMNS = { "AUTHORID", "FIRSTNAME", "MIDDLENAME", "LASTNAME" };
	String[] BOOKAUTHOR_COLUMNS = { "BOOK_ISBN", "BOOK_COPYNUMBER", "AUTHOR_AUTHORID" };

	String PATRON_QUERY = "SELECT LOGINID FROM PATRON WHERE LOGINID = ? AND PASSWORD = ?";
	String LIBRARIAN_QUERY = "SELECT LOGINID FROM LIBRARIAN WHERE LOGINID = ?";
	String ACCESS_QUERY = "SELECT TRANSACCESS, PATRONACCESS, BOOKACCESS, LIBACCESS FROM LIBRARIAN WHERE LOGINID = ?";
	String BOOK_STATUS_QUERY = "SELECT CURRENTSTATUS, STATUSDATE FROM BOOK WHERE ISBN = ? AND COPYNUMBER = ?";
	String PATRON_MODE_QUERY = "SELECT PATRON_LOGINID FROM TRANSACTION WHERE TRANSACTIONMODE = ? AND TRANSACTIONDATE = to_date(?,'yyyy-mm-dd hh24:mi:ss') AND BOOK_ISBN = ? AND BOOK_COPYNUMBER = ?";
	String COUNT_MODE_QUERY = "SELECT COUNT(TRANSACTIONID) FROM TRANSACTION WHERE PATRON_LOGINID = ? AND TRANSACTIONMODE = ? AND (to_date(?,'yyyy-mm-dd hh24:mi:ss') - TRANSACTIONDATE) <= 7";

	String[] ACCESS_PERMISSIONS = { "111", "110", "100", "000", "001", "011", "010", "101" };
	String[] CURRENT_STATUS = { "ON-SHELF", "ON-HOLD", "ON-LOAN" };
	String[] TRANSACTION_MODE = { "LOAN", "RETURN", "RESERVE" };

	SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SPINNER_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
}
=======
import java.text.SimpleDateFormat;

public interface SQLStatements {
	String[] TABLES = { "TRANSACTION", "PATRON", "BOOK", "LIBRARIAN", "SHELF", "AUTHOR", "BOOKAUTHOR" };

	String[] TRANSACTION_COLUMNS = { "TRANSACTIONID", "TRANSACTIONDATE", "TRANSACTIONMODE", "PATRON_LOGINID", "BOOK_ISBN", "BOOK_COPYNUMBER" };
	String[] PATRON_COLUMNS = { "LOGINID", "FIRSTNAME", "MIDDLENAME", "LASTNAME", "PASSWORD", "HOUSENO", "STREET", "BARANGAY", "CITY", "UNPAIDFINE" };
	String[] BOOK_COLUMNS = { "ISBN", "COPYNUMBER", "TITLE", "PUBLICATIONYEAR", "CURRENTSTATUS", "STATUSDATE", "SHELF_SHELFID" };
	String[] LIBRARIAN_COLUMNS = { "LOGINID", "PATRONACCESS", "LIBACCESS", "BOOKACCESS", "TRANSACCESS" };
	String[] SHELF_COLUMNS = { "SHELFID", "CAPACITY" };
	String[] AUTHOR_COLUMNS = { "AUTHORID", "FIRSTNAME", "MIDDLENAME", "LASTNAME" };
	String[] BOOKAUTHOR_COLUMNS = { "BOOK_ISBN", "BOOK_COPYNUMBER", "AUTHOR_AUTHORID" };

	String PATRON_QUERY = "SELECT LOGINID FROM PATRON WHERE LOGINID = ? AND PASSWORD = ?";
	String LIBRARIAN_QUERY = "SELECT LOGINID FROM LIBRARIAN WHERE LOGINID = ?";
	String ACCESS_QUERY = "SELECT TRANSACCESS, PATRONACCESS, BOOKACCESS, LIBACCESS FROM LIBRARIAN WHERE LOGINID = ?";
	String BOOK_STATUS_QUERY = "SELECT CURRENTSTATUS, STATUSDATE FROM BOOK WHERE ISBN = ? AND COPYNUMBER = ?";
	String PATRON_MODE_QUERY = "SELECT PATRON_LOGINID FROM TRANSACTION WHERE TRANSACTIONMODE = ? AND TRANSACTIONDATE = to_date(?,'yyyy-mm-dd hh24:mi:ss') AND BOOK_ISBN = ? AND BOOK_COPYNUMBER = ?";
	String COUNT_MODE_QUERY = "SELECT COUNT(TRANSACTIONID) FROM TRANSACTION WHERE PATRON_LOGINID = ? AND TRANSACTIONMODE = ? AND (to_date(?,'yyyy-mm-dd hh24:mi:ss') - TRANSACTIONDATE) <= 7";

	String[] ACCESS_PERMISSIONS = { "111", "110", "100", "000", "001", "011", "010", "101" };
	String[] CURRENT_STATUS = { "ON-SHELF", "ON-HOLD", "ON-LOAN" };
	String[] TRANSACTION_MODE = { "LOAN", "RETURN", "RESERVE" };

	SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SPINNER_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
}
>>>>>>> 1e98e5f7756668a21eb5c82977f5ab2af6243101
