public final class DatabaseContract {
    public final static String TRANSACTION_TABLE = "Transaction";
    public final static String PATRON_TABLE = "Patron";
    public final static String BOOK_TABLE = "Book";
    public final static String LIBRARIAN_TABLE = "Librarian";
    public final static int TOTAL_TABLE_NUMBER_SIZE = 4;

    private DatabaseContract() {}

    public static class TransactionTable {
        private TransactionTable() {}
        public static final String TRANSACTION_ID_COLUMN = "Transaction ID";
        public static final String TRANSACTION_DATE_COLUMN = "Transaction Date";
        public static final String TRANSACTION_MODE_COLUMN = "Transaction Mode";
        public static final String LOGIN_ID_COLUMN = "Login ID";
        public static final String ISBN_COLUMN = "ISBN";
        public static final String COPY_NUMBER_COLUMN = "Copy Number";
    }

    public static class PatronTable {
        private PatronTable() {}
        public static final String LOGIN_ID_COLUMN = "Login ID";
        public static final String FIRST_NAME_COLUMN = "First Name";
        public static final String MIDDLE_NAME_COLUMN = "Middle Name";
        public static final String LAST_NAME_COLUMN = "Last Name";
        public static final String PASSWORD_COLUMN = "Password";
        public static final String HOUSE_NO_COLUMN = "House No.";
        public static final String STREET_COLUMN = "Street";
        public static final String BARANGAY_COLUMN = "Barangay";
        public static final String CITY_COLUMN = "City";
        public static final String UNPAID_FINE_COLUMN = "Unpaid Fine";
    }

    public static class BookTable {
        private BookTable() {}
        public static final String ISBN_COLUMN = "ISBN";
        public static final String COPY_NUMBER_COLUMN = "Copy Number";
        public static final String TITLE_COLUMN = "Title";
        public static final String YEAR_OF_PUBLICATION_COLUMN = "Year of Publication";
        public static final String CURRENT_STATUS_COLUMN = "Current Status";
        public static final String STATUS_DATE_COLUMN = "Status Date";
        public static final String SHELF_ID_COLUMN = "Shelf ID";
    }

    public static class LibrarianTable {
        private LibrarianTable() {}
        public static final String LOGIN_ID_COLUMN = "Login ID";
        public static final String FIRST_NAME_COLUMN = "First Name";
        public static final String MIDDLE_NAME_COLUMN = "Middle Name";
        public static final String LAST_NAME_COLUMN = "Last Name";
        public static final String PASSWORD_COLUMN = "Password";
        public static final String HOUSE_NO_COLUMN = "House No.";
        public static final String STREET_COLUMN = "Street";
        public static final String BARANGAY_COLUMN = "Barangay";
        public static final String CITY_COLUMN = "City";
        public static final String UNPAID_FINE_COLUMN = "Unpaid Fine";
        public static final String PATRON_ACCESS_COLUMN = "Patron Access";
        public static final String LIBRARIAN_ACCESS_COLUMN = "Librarian Access";
        public static final String BOOK_ACCESS_COLUMN = "Book Access";
        public static final String TRANSACTION_ACCESS_COLUMN = "Transaction Access";
    }
}
