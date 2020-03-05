import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class LibrarianFrame extends JFrame implements SQLStatements {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private JTabbedPane tablesTabbedPane;
    private JPanel[] panels;
    private JTable[] tables;
    private String[] tableNames;
    private String[][] columnNames;
    private Object[][][] data;
    private JPanel[] searchPanels;
    private JLabel[] searchLabels;
    private JTextField[] searchFields;
    private JComboBox<?>[] searchBoxes;
    private JButton[] addButtons;
    private JButton[] editButtons;
    private JButton[] deleteButtons;
    private JButton[] finishButtons;
    private DefaultTableModel[] tableModels;
    private TableRowSorter<DefaultTableModel>[] sorters;

    private String loginId;

    public LibrarianFrame(Connection con, String loginId) {
            this.con = con;
            this.loginId = loginId;
            setTitle("Librarian");

            tableNames = 
                    new String[] {DatabaseContract.TRANSACTION_TABLE,
                                  DatabaseContract.PATRON_TABLE,
                                  DatabaseContract.BOOK_TABLE,
                                  DatabaseContract.LIBRARIAN_TABLE};

            columnNames = new String[][]{
                    new String[] {DatabaseContract.TransactionTable.TRANSACTION_ID_COLUMN,
                                  DatabaseContract.TransactionTable.TRANSACTION_DATE_COLUMN,
                                  DatabaseContract.TransactionTable.TRANSACTION_MODE_COLUMN,
                                  DatabaseContract.TransactionTable.LOGIN_ID_COLUMN,
                                  DatabaseContract.TransactionTable.ISBN_COLUMN,
                                  DatabaseContract.TransactionTable.COPY_NUMBER_COLUMN},

                    new String[] {DatabaseContract.PatronTable.LOGIN_ID_COLUMN,
                                  DatabaseContract.PatronTable.FIRST_NAME_COLUMN,
                                  DatabaseContract.PatronTable.MIDDLE_NAME_COLUMN,
                                  DatabaseContract.PatronTable.LAST_NAME_COLUMN,
                                  DatabaseContract.PatronTable.PASSWORD_COLUMN,
                                  DatabaseContract.PatronTable.HOUSE_NO_COLUMN,
                                  DatabaseContract.PatronTable.STREET_COLUMN,
                                  DatabaseContract.PatronTable.BARANGAY_COLUMN,
                                  DatabaseContract.PatronTable.CITY_COLUMN,
                                  DatabaseContract.PatronTable.UNPAID_FINE_COLUMN},

                    new String[] {DatabaseContract.BookTable.ISBN_COLUMN,
                                  DatabaseContract.BookTable.COPY_NUMBER_COLUMN,
                                  DatabaseContract.BookTable.TITLE_COLUMN,
                                  DatabaseContract.BookTable.YEAR_OF_PUBLICATION_COLUMN,
                                  DatabaseContract.BookTable.CURRENT_STATUS_COLUMN,
                                  DatabaseContract.BookTable.STATUS_DATE_COLUMN,
                                  DatabaseContract.BookTable.SHELF_ID_COLUMN},

                    new String[] {DatabaseContract.LibrarianTable.LOGIN_ID_COLUMN,
                                  DatabaseContract.LibrarianTable.FIRST_NAME_COLUMN,
                                  DatabaseContract.LibrarianTable.MIDDLE_NAME_COLUMN,
                                  DatabaseContract.LibrarianTable.LAST_NAME_COLUMN,
                                  DatabaseContract.LibrarianTable.PASSWORD_COLUMN,
                                  DatabaseContract.LibrarianTable.HOUSE_NO_COLUMN,
                                  DatabaseContract.LibrarianTable.STREET_COLUMN,
                                  DatabaseContract.LibrarianTable.BARANGAY_COLUMN,
                                  DatabaseContract.LibrarianTable.CITY_COLUMN,
                                  DatabaseContract.LibrarianTable.UNPAID_FINE_COLUMN,
                                  DatabaseContract.LibrarianTable.PATRON_ACCESS_COLUMN,
                                  DatabaseContract.LibrarianTable.LIBRARIAN_ACCESS_COLUMN,
                                  DatabaseContract.LibrarianTable.BOOK_ACCESS_COLUMN,
                                  DatabaseContract.LibrarianTable.TRANSACTION_ACCESS_COLUMN}
            };

            // tables
            panels = new JPanel[tableNames.length];
            tables = new JTable[tableNames.length];
            data = new Object[tables.length][][];
            tableModels = new DefaultTableModel[tables.length];
            sorters = new TableRowSorter[tableModels.length];

            // for search tool
            searchPanels = new JPanel[tables.length];
            searchLabels = new JLabel[tables.length];
            searchFields = new JTextField[tables.length];
            searchBoxes = new JComboBox[tables.length];

            // buttons
            addButtons = new JButton[tables.length];
            editButtons = new JButton[tables.length];
            deleteButtons = new JButton[tables.length];
            finishButtons = new JButton[tables.length];

            initComponents();
    }
	
    public void initComponents() {
        try {
            con.setAutoCommit(false);
            data = getData(con);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        tablesTabbedPane = new JTabbedPane();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < panels.length; ++i) {
            panels[i] = new JPanel(new GridBagLayout());
            tablesTabbedPane.add(tableNames[i], panels[i]);

            tables[i] = new JTable() {
                private static final long serialVersionUID = 1L;

                public boolean getScrollableTracksViewportWidth() {
                    return getPreferredSize().width < getParent().getWidth();
                }
            };

            tableModels[i] = new DefaultTableModel(data[i], columnNames[i]) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return String.class;
                }
            };

            sorters[i] = new TableRowSorter<DefaultTableModel>(tableModels[i]);
            tableModels[i].setColumnIdentifiers(columnNames[i]);
            tables[i].setModel(tableModels[i]);
            tables[i].setPreferredScrollableViewportSize(new Dimension(800, 350));
            tables[i].setFillsViewportHeight(true);
            tables[i].getTableHeader().setReorderingAllowed(false);
            tables[i].getTableHeader().setResizingAllowed(false);
            tables[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tables[i].setRowSorter(sorters[i]);

            searchLabels[i] = new JLabel("Search:");
            searchFields[i] = new JTextField();
            searchFields[i].setPreferredSize(new Dimension(300, 20));
            searchBoxes[i] = new JComboBox<String>(columnNames[i]);
            searchBoxes[i].setSelectedIndex(0);

            searchFields[i].getDocument().addDocumentListener(new SearchListener());
            searchFields[i].addKeyListener(new SearchListener());
            searchBoxes[i].addItemListener(new SearchListener());

            searchPanels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
            searchPanels[i].add(searchLabels[i]);
            searchPanels[i].add(searchFields[i]);
            searchPanels[i].add(searchBoxes[i]);

            addButtons[i] = new JButton("Add " + tableNames[i]);
            editButtons[i] = new JButton("Edit " + tableNames[i]);
            deleteButtons[i] = new JButton("Delete " + tableNames[i]);
            finishButtons[i] = new JButton("Finish");

            addButtons[i].addActionListener(new AddListener());
            editButtons[i].addActionListener(new EditListener());
            deleteButtons[i].addActionListener(new DeleteListener());
            finishButtons[i].addActionListener(new FinishListener());

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridwidth = 4;
            panels[i].add(searchPanels[i], constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridwidth = 4;
            panels[i].add(new JScrollPane(tables[i], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridwidth = 1;
            panels[i].add(addButtons[i], constraints);

            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridwidth = 1;
            panels[i].add(editButtons[i], constraints);

            constraints.gridx = 2;
            constraints.gridy = 2;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridwidth = 1;
            panels[i].add(deleteButtons[i], constraints);

            constraints.gridx = 3;
            constraints.gridy = 2;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.gridwidth = 1;
            panels[i].add(finishButtons[i], constraints);
        }

        String[] access = new String[tables.length];
        try {
            PreparedStatement preparedStatement = con.prepareStatement(ACCESS_QUERY);
            preparedStatement.setString(1,loginId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                for (int i = 0; i < access.length; ++i){
                    access[i] = resultSet.getString(i+1);
                    if (access[i].charAt(0) == '0') addButtons[i].setEnabled(false);
                    if (access[i].charAt(1) == '0') editButtons[i].setEnabled(false);
                    if (access[i].charAt(2) == '0') deleteButtons[i].setEnabled(false);
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tablesTabbedPane.setFocusable(false);

        add(tablesTabbedPane);

        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(rootPane, e.getMessage(), "SQLException", JOptionPane.ERROR_MESSAGE);
                    }
                    createLoginFrame();
                }
            }
        });
    }

    private Object[][][] getData(Connection con) throws SQLException {
        Object[][][] data = new Object[panels.length][][];

        for (int i = 0; i < data.length; ++i){
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            if (i == 1){
                rs = statement.executeQuery("SELECT * FROM PATRON WHERE LOGINID NOT IN (SELECT LOGINID FROM LIBRARIAN)");
            }
            else if (i == 3){
                rs = statement.executeQuery("SELECT P.LOGINID, FIRSTNAME, MIDDLENAME, LASTNAME, PASSWORD, "
                + "HOUSENO, STREET, BARANGAY, CITY, UNPAIDFINE, "
                + "PATRONACCESS, LIBACCESS, BOOKACCESS, TRANSACCESS "
                + "FROM PATRON P, LIBRARIAN L WHERE P.LOGINID = L.LOGINID");
            }
            else {
                rs = statement.executeQuery("SELECT * FROM " + tableNames[i]);
            }

            rs.last();
            data[i] = new Object[rs.getRow()][];
            rs.beforeFirst();

            for (int j = 0; rs.next(); ++j){
                data[i][j] = new Object[columnNames[i].length];
                for (int k = 0; k < data[i][j].length; ++k){
                    data[i][j][k] = rs.getString(k+1);
                }
            }
        }

        return data;
    }

    private void createLoginFrame() {
        LoginFrame login = new LoginFrame(con);
        login.setSize(300, 200);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        dispose();
    }

    private class FinishListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            finishDialog();
        }
    }

    private void finishDialog() {
        Object[] options = {"Save", "Don't Save", "Cancel"};
        JOptionPane optionPane = new JOptionPane("Do you want to save your changes?", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options);
        JDialog dialog = new JDialog(this, "Finish", true);
        dialog.setContentPane(optionPane);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())) {
                    try {
                        if (optionPane.getValue().equals(options[0])) {
                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            con.commit();
                            createLoginFrame();

                        } else if (optionPane.getValue().equals(options[1])) {
                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            con.rollback();
                            createLoginFrame();

                        } else if (optionPane.getValue().equals(options[2])) {
                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            dialog.dispose();
                        }
                    }
                    catch (SQLException e) {
                        JOptionPane.showMessageDialog(rootPane, e.getMessage(), 
                        "SQLException", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                for (int i = 0; i < addButtons.length; ++i) {
                    if (addButtons[i].equals((JButton) e.getSource())) {
                        createAddDialog(i);
                    }
                }
            }
            catch (SQLException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage(),
                "SQLException",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createAddDialog(int table) throws SQLException {
        Object[] prompt = new Object[columnNames[table].length * 2];

        Object[] field = null;

        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date endDate = calendar.getTime();
        SpinnerModel dateModel = new SpinnerDateModel(initDate, startDate, endDate, Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        Statement statement;
        statement = con.createStatement();
        ResultSet rs;

        if (table == 0) {

            rs = statement.executeQuery("SELECT LOGINID FROM PATRON");

            Vector<String> loginID = new Vector<String>();

            while (rs.next()){
                    loginID.add(rs.getString(1));
            }

            rs = statement.executeQuery("SELECT ISBN, COPYNUMBER FROM BOOK");

            Vector<String> isbn = new Vector<String>();
            Vector<String> copynumber = new Vector<String>();

            while (rs.next()){
                    boolean isISBNDuplicate = false;
                    boolean isCopyNumDuplicate = false;

                    for (String value : isbn){
                            if (value.equals(rs.getString(1))){
                                    isISBNDuplicate = true;
                                    break;
                            }
                    }

                    for (String value : copynumber){
                            if (value.equals(rs.getString(2))){
                                    isCopyNumDuplicate = true;
                                    break;
                            }
                    }

                    if (!isISBNDuplicate){
                            isbn.add(rs.getString(1));
                    }

                    if (!isCopyNumDuplicate){
                            copynumber.add(rs.getString(2));
                    }
            }

            field = new Object[] { new JTextField(), dateSpinner,
                                   new JComboBox<String>(new String[] { "LOAN", "RETURN", "RESERVE" }),
                                   new JComboBox<String>(loginID), new JComboBox<String>(isbn), 
                                   new JComboBox<String>(copynumber) };

        }
        
        else if (table == 1) {
            field = new Object[] { new JTextField(), new JTextField(), new JTextField(), new JTextField(),
                    new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
                    new JTextField() };
        }
        
        else if (table == 2) {
            rs = statement.executeQuery("SELECT SHELFID FROM SHELF");

            Vector<String> shelfID = new Vector<String>();

            while (rs.next()){
                shelfID.add(rs.getString(1));
            }

            field = new Object[] { new JTextField(), new JTextField(), new JTextField(), new JTextField(),
                    new JComboBox<String>(new String[] { "ON-SHELF", "ON-HOLD", "ON-LOAN" }), dateSpinner,
                    new JComboBox<String>(shelfID) };
        } 
        
        else if (table == 3) {
            String[] access = { "111", "110", "100", "000", "001", "011", "010", "101" };

            field = new Object[] { new JTextField(), new JTextField(), new JTextField(), new JTextField(),
                                   new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
                                   new JTextField(), new JComboBox<String>(access), new JComboBox<String>(access),
                                   new JComboBox<String>(access), new JComboBox<String>(access) };
        }

        for (int i = 0, j = 0; i < prompt.length; ++j) {
            prompt[i++] = columnNames[table][j];
            prompt[i++] = field[j];
        }

        Object[] options = { "Save", "Cancel" };

        JOptionPane optionPane = new JOptionPane(prompt, JOptionPane.PLAIN_MESSAGE, 
        JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        JDialog dialog = new JDialog(this, addButtons[table].getText(), true);
        dialog.setContentPane(optionPane);

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())) {
                    if (optionPane.getValue().equals(options[0])) {
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                        try {
                            String[] data = new String[columnNames[table].length];
                            CallableStatement cs = null;

                            if (table == 0) {
                                for (int i = 0, j = 0; i < data.length; ++j){
                                    if (i == 0){
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else if (i == 1){
                                        data[i++] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        .format(((JSpinner)prompt[++j]).getValue());
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call add_transaction(?,to_date(?,'yyyy-MM-dd hh:mi:ss'),?,?,?,?)}");
                            }
                            
                            else if (table == 1) {
                                for (int i = 0, j = 0; i < data.length; ++j) {
                                    data[i++] = ((JTextField) prompt[++j]).getText();
                                }

                                cs = con.prepareCall("{call add_user(?,?,?,?,?,?,?,?,?,?)}");
                            }
                            
                            else if (table == 2) {
                                for (int i = 0, j = 0; i < data.length; ++j){
                                    if (i < data.length - 3){
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else if (i == 5){
                                        data[i++] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        .format(((JSpinner)prompt[++j]).getValue());
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call add_book(?,?,?,?,?,to_date(?,'yyyy-MM-dd hh:mi:ss'),?)}");

                            }
                            
                            else if (table == 3) {
                                for (int i = 0, j = 0; i < data.length; ++j) {
                                    if (i < data.length - 4) {
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call add_librarian(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                            }

                            for (int i = 0; i < data.length; ++i) {
                                cs.setString(i + 1, data[i]);
                            }

                            cs.executeUpdate();
                            tableModels[table].addRow(data);
                            dialog.dispose();

                            JOptionPane.showMessageDialog(rootPane,tableNames[table] + " Added!", 
                            addButtons[table].getText(),JOptionPane.INFORMATION_MESSAGE);
                        } 
                        catch (SQLException e) {
                                JOptionPane.showMessageDialog(rootPane, e.getMessage(),
                                "SQLException",JOptionPane.ERROR_MESSAGE);
                        }
                    } 
                    
                    else if (optionPane.getValue().equals(options[1])) {
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        dialog.dispose();
                    }
                }
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private class EditListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                for (int i = 0; i < editButtons.length; ++i){
                    if (editButtons[i].equals((JButton)e.getSource())){
                        int[] rows = tables[i].getSelectedRows();
                        if (rows.length == 0) return;
                        for (int j = 0; j < rows.length; ++j){
                            createEditDialog(i,rows[j]);
                        }
                    }
                }
            }
            catch (SQLException | ParseException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage(),
                "SQLException",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createEditDialog(int table, int row) 
        throws SQLException, ParseException {
        Object[] prompt = new Object[columnNames[table].length * 2];

        String[] values = new String[columnNames[table].length];

        for (int i = 0; i < values.length; ++i){
                values[i] = String.valueOf(tables[table].getValueAt(row,i));
        }

        Object[] field = null;

        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date endDate = calendar.getTime();
        SpinnerModel dateModel = new SpinnerDateModel(initDate, startDate, endDate, Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        Statement statement;
        statement = con.createStatement();
        ResultSet rs;

        if(table == 0) {
            dateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd")
            .parse(values[1].substring(0,10)));

            JComboBox<String> modes = new JComboBox<String>(new String[]{ "LOAN", "RETURN", "RESERVE" });
            modes.setSelectedItem(values[2]);

            Vector<String>[] vectors = new Vector[3];
            JComboBox<?>[] comboBoxes = new JComboBox[vectors.length];

            for (int i = 0; i < vectors.length; ++i){
                    vectors[i] = new Vector<String>();
            }

            rs = statement.executeQuery("SELECT LOGINID FROM PATRON");
            while (rs.next()){
                    vectors[0].add(rs.getString(1));
            }

            rs = statement.executeQuery("SELECT ISBN, COPYNUMBER FROM BOOK");
            while (rs.next()){
                    boolean isISBNDuplicate = false;
                    boolean isCopyNumDuplicate = false;

                    for (String isbn : vectors[1]){
                        if (isbn.equals(rs.getString(1))){
                            isISBNDuplicate = true;
                            break;
                        }
                    }

                    for (String cpnum : vectors[2]){
                        if (cpnum.equals(rs.getString(2))){
                            isCopyNumDuplicate = true;
                            break;
                        }
                    }

                    if (!isISBNDuplicate){
                        vectors[1].add(rs.getString(1));
                    }

                    if (!isCopyNumDuplicate){
                        vectors[2].add(rs.getString(2));
                    }
            }

            for (int i = 0; i < comboBoxes.length; ++i){
                comboBoxes[i] = new JComboBox<String>(vectors[i]);
                comboBoxes[i].setSelectedItem(values[3+i]);
            }

            JTextField transIdField = new JTextField(values[0]);
            transIdField.setEditable(false);

            field = new Object[] { 
                transIdField, dateSpinner, modes,
                comboBoxes[0], comboBoxes[1], comboBoxes[2] };

        }
        
        else if(table == 1) {
            JTextField loginIdField = new JTextField(values[0]);
            loginIdField.setEditable(false);

            field = new Object[] { 
                    loginIdField, new JTextField(values[1]),
                    new JTextField(values[2]), new JTextField(values[3]),
                    new JTextField(values[4]), new JTextField(values[5]),
                    new JTextField(values[6]), new JTextField(values[7]),
                    new JTextField(values[8]), new JTextField(values[9]) };
        }
        
        else if(table == 2) {
            dateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd")
            .parse(values[5].substring(0,10)));

            rs = statement.executeQuery("SELECT SHELFID FROM SHELF");

            Vector<String> shelfID = new Vector<String>();

            while (rs.next()){
                shelfID.add(rs.getString(1));
            }

            JComboBox<String> shelfBox = new JComboBox<String>(shelfID);
            shelfBox.setSelectedItem(values[6]);

            JComboBox<String> status = new JComboBox<String>(new String[]{ "ON-SHELF", "ON-HOLD", "ON-LOAN" });
            status.setSelectedItem(values[4]);

            JTextField isbnField = new JTextField(values[0]);
            JTextField cpNumField = new JTextField(values[1]);
            isbnField.setEditable(false);
            cpNumField.setEditable(false);

            field = new Object[] { 
                    isbnField, cpNumField, 
                    new JTextField(values[2]), new JTextField(values[3]),
                    status, dateSpinner, shelfBox };
        }
        
        else if(table == 3) {
            String[] access = { "111", "110", "100", "000", "001", "011", "010", "101" };

            JComboBox<?>[] comboBoxes = new JComboBox[4];

            for (int i = 0; i < comboBoxes.length; ++i){
                comboBoxes[i] = new JComboBox<String>(access);
                comboBoxes[i].setSelectedItem(values[10+i]);
            }

            JTextField loginIdField = new JTextField(values[0]);
            loginIdField.setEditable(false);

            field = new Object[] { 
                    loginIdField, new JTextField(values[1]),
                    new JTextField(values[2]), new JTextField(values[3]),
                    new JTextField(values[4]), new JTextField(values[5]),
                    new JTextField(values[6]), new JTextField(values[7]),
                    new JTextField(values[8]), new JTextField(values[9]),
                    comboBoxes[0], comboBoxes[1], comboBoxes[2], comboBoxes[3]};
        }

        for(int i = 0, j = 0; i < prompt.length; ++j) {
            prompt[i++] = columnNames[table][j];
            prompt[i++] = field[j];
        }

        Object[] options = {"Save","Cancel"};

        JOptionPane optionPane = new JOptionPane(prompt, JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION,null,options,options[0]);

        JDialog dialog = new JDialog(this, editButtons[table].getText(), true);
        dialog.setContentPane(optionPane);

        optionPane.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())) {
                    if (optionPane.getValue().equals(options[0])){
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                        try {
                            String[] data = new String[columnNames[table].length];
                            CallableStatement cs = null;

                            if (table == 0){
                                for (int i = 0, j = 0; i < data.length; ++j){
                                    if (i == 0){
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else if (i == 1){
                                        data[i++] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        .format(((JSpinner)prompt[++j]).getValue());
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call edit_transaction(?,?,to_date(?,'yyyy-MM-dd hh:mi:ss'),?,?,?,?)}");
                            }
                            
                            else if (table == 1){
                                for (int i = 0, j = 0; i < data.length; ++j) {
                                    data[i++] = ((JTextField) prompt[++j]).getText();
                                }

                                cs = con.prepareCall("{call edit_user(?,?,?,?,?,?,?,?,?,?,?)}");
                            }
                            
                            else if (table == 2){
                                for (int i = 0, j = 0; i < data.length; ++j){
                                    if (i < data.length - 3){
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else if (i == 5){
                                        data[i++] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        .format(((JSpinner)prompt[++j]).getValue());
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call edit_book(?,?,?,?,?,?,?,to_date(?,'yyyy-MM-dd hh:mi:ss'),?)}");
                            }
                            else if (table == 3){
                                for (int i = 0, j = 0; i < data.length; ++j) {
                                    if (i < data.length - 4) {
                                        data[i++] = ((JTextField) prompt[++j]).getText();
                                    }
                                    else {
                                        data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
                                        .getSelectedItem());
                                    }
                                }

                                cs = con.prepareCall("{call edit_librarian(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                            }

                            if (table != 2){
                                cs.setString(1, values[0]);
                                for (int i = 0; i < data.length; ++i){
                                    cs.setString(i + 2, data[i]);
                                }
                            }
                            
                            else {
                                cs.setString(1, values[0]);
                                cs.setString(2, values[1]);
                                for (int i =0; i < data.length; ++i){
                                    cs.setString(i + 3, data[i]);
                                }
                            }

                            int response = JOptionPane.showConfirmDialog(rootPane, "Do you want save your changes to:\n" 
                            + values[0], editButtons[table].getText(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (response == JOptionPane.YES_OPTION){

                                cs.executeUpdate();

                                for (int i = 0; i < columnNames[table].length; ++i){
                                    tables[table].setValueAt(data[i], row, i);
                                }

                                dialog.dispose();

                                JOptionPane.showMessageDialog(rootPane, tableNames[table] + " Updated!", 
                                editButtons[table].getText(), JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        catch (SQLException e) {
                                JOptionPane.showMessageDialog(rootPane, e.getMessage(),
                                "SQLException",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                    else if (optionPane.getValue().equals(options[1])){
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        dialog.dispose();
                    }
                }
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                for (int i = 0; i < deleteButtons.length; ++i){
                    if (deleteButtons[i].equals((JButton)e.getSource())){
                        int[] rows = tables[i].getSelectedRows();
                        if (rows.length == 0) return;
                        createDeleteDialog(i,rows);
                    }
                }
            }
            catch (SQLException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage(),
                "SQLException",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createDeleteDialog(int table, int[] rows) throws SQLException {

        Vector<String[]> keys = new Vector<String[]>();

        keys.add(new String[rows.length]);
        for (int i = 0; i < rows.length; ++i){
            keys.get(0)[i] = String.valueOf(tables[table].getValueAt(rows[i],0));
        }

        if (table == 2){
            keys.add(new String[rows.length]);
            for (int i = 0; i < rows.length; ++i){
                keys.get(1)[i] = String.valueOf(tables[table].getValueAt(rows[i],1));
            }
        }

        int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to delete selected " 
        + tableNames[table] + "/s", deleteButtons[table].getText(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION){
            for (int i = 0; i < keys.get(0).length; ++i){
                for (int j = 0; j < tableModels[table].getRowCount(); ++j){
                    String modelKey = String.valueOf(tableModels[table].getValueAt(j,0));
                    if (keys.get(0)[i].equals(modelKey)){

                        CallableStatement cs = null;
                        if (table == 0){
                            cs = con.prepareCall("{call delete_transaction(?)}");
                            cs.setString(1,keys.get(0)[i]);
                        }
                        else if (table == 1){
                            cs = con.prepareCall("{call delete_user(?)}");
                            cs.setString(1,keys.get(0)[i]);
                        }
                        else if (table == 2){
                            cs = con.prepareCall("{call delete_book(?,?)}");
                            cs.setString(1,keys.get(0)[i]);
                            cs.setString(2,keys.get(1)[i]);
                        }
                        else if (table == 3){
                            cs = con.prepareCall("{call delete_librarian(?)}");
                            cs.setString(1,keys.get(0)[i]);
                        }

                        cs.executeUpdate();
                        tableModels[table].removeRow(j);
                        break;
                    }
                }
            }

            JOptionPane.showMessageDialog(rootPane, tableNames[table] + " Deleted!",
            deleteButtons[table].getText(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class SearchListener extends KeyAdapter implements DocumentListener, ItemListener {

        @Override
        public void keyTyped(KeyEvent e) {
            for (int i = 0; i < searchFields.length; ++i){
                if (searchFields[i].equals((JTextField)e.getSource()) 
                && (int)e.getKeyChar() == 10){
                    search(i);
                }
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            for (int i = 0; i < searchBoxes.length; ++i){
                if (searchBoxes[i].equals((JComboBox<?>)e.getSource())){
                    search(i);
                }
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            for (int i = 0; i < searchFields.length; ++i){
                if (searchFields[i].getDocument().equals((Document)e.getDocument())){
                    search(i);
                }
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            for (int i = 0; i < searchFields.length; ++i){
                if (searchFields[i].getDocument().equals((Document)e.getDocument())){
                    search(i);
                }
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            for (int i = 0; i < searchFields.length; ++i){
                if (searchFields[i].getDocument().equals((Document)e.getDocument())){
                    search(i);
                }
            }
        }

        private void search(int i){
            try {
                RowFilter<DefaultTableModel,Object> rowFilter = null;
                rowFilter = RowFilter.regexFilter("(?i)" + searchFields[i].getText(), 
                searchBoxes[i].getSelectedIndex());
                sorters[i].setRowFilter(rowFilter);
            }
            catch (NullPointerException e) {
                // Expected Exception: user search without any contents on the table
            }
            catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(rootPane,e.getMessage(),
                "PatternSyntaxException",JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(rootPane,e.getMessage(),
                "Exception",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}