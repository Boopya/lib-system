import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;

public class PatronFrame extends JFrame implements SQLStatements {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private JTabbedPane tablesTabbedPane;
    private JPanel[] panels;
    private JTable[] tables;
    private String[] tableNames;
    private String[][] columnNames;
    private Object[][][] data;
    private JButton patronInfoButton;
    private JButton reserveButton;
    private JButton[] finishButtons;
    private JPanel[] searchPanels;
    private JLabel[] searchLabels;
    private JTextField[] searchFields;
    private JComboBox<?>[] searchBoxes;
    private DefaultTableModel[] tableModels;
    private TableRowSorter<DefaultTableModel>[] sorters;
    private String loginId;

    public PatronFrame(Connection con, String loginId) {
        this.con = con;
        this.loginId = loginId;
        setTitle("Patron");

        tableNames = new String[] { DatabaseContract.TRANSACTION_TABLE, DatabaseContract.BOOK_TABLE };

        columnNames = new String[][] { new String[] { DatabaseContract.TransactionTable.TRANSACTION_ID_COLUMN,
                DatabaseContract.TransactionTable.TRANSACTION_DATE_COLUMN,
                DatabaseContract.TransactionTable.TRANSACTION_MODE_COLUMN,
                DatabaseContract.TransactionTable.LOGIN_ID_COLUMN, DatabaseContract.TransactionTable.ISBN_COLUMN,
                DatabaseContract.TransactionTable.COPY_NUMBER_COLUMN },

                new String[] { DatabaseContract.BookTable.ISBN_COLUMN, DatabaseContract.BookTable.COPY_NUMBER_COLUMN,
                        DatabaseContract.BookTable.TITLE_COLUMN, DatabaseContract.BookTable.YEAR_OF_PUBLICATION_COLUMN,
                        DatabaseContract.BookTable.CURRENT_STATUS_COLUMN, DatabaseContract.BookTable.STATUS_DATE_COLUMN,
                        DatabaseContract.BookTable.SHELF_ID_COLUMN }, };

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

        finishButtons = new JButton[tables.length];

        initComponents();
    }

    public void initComponents() {
        tablesTabbedPane = new JTabbedPane();

        try {
            con.setAutoCommit(false);
            data = getData(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

            searchFields[i].getDocument().addDocumentListener(new PatronFrame.SearchListener());
            searchFields[i].addKeyListener(new PatronFrame.SearchListener());
            searchBoxes[i].addItemListener(new PatronFrame.SearchListener());

            searchPanels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
            searchPanels[i].add(searchLabels[i]);
            searchPanels[i].add(searchFields[i]);
            searchPanels[i].add(searchBoxes[i]);

            finishButtons[i] = new JButton("Finish");
            finishButtons[i].addActionListener(new FinishListener());

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridwidth = 5;
            panels[i].add(searchPanels[i], constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridwidth = 5;
            panels[i].add(new JScrollPane(tables[i], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), constraints);

            constraints.gridx = 4;
            constraints.gridy = 2;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.gridwidth = 1;
            panels[i].add(finishButtons[i], constraints);

            switch (i) {
                case 0:

                    patronInfoButton = new JButton("Patron Information");
                    patronInfoButton.addActionListener(new PatronInfoListener());
                    constraints.gridx = 2;
                    constraints.gridy = 2;
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.gridwidth = 1;
                    panels[i].add(patronInfoButton, constraints);
                    tables[i].setEnabled(false);

                    break;

                case 1:

                    reserveButton = new JButton("Reserve Book");
                    reserveButton.addActionListener(new ReserveButtonListener());
                    constraints.gridx = 2;
                    constraints.gridy = 2;
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.gridwidth = 1;
                    panels[i].add(reserveButton, constraints);

                    break;

                default:
                    JOptionPane.showMessageDialog(rootPane, "Unexpected Behavior", "Error!", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }

        tablesTabbedPane.setFocusable(false);
        add(tablesTabbedPane);
        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    createLoginFrame();
                }
            }
        });
    }

    private void createLoginFrame() {
        LoginFrame login = new LoginFrame(con);
        login.setSize(300, 200);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        dispose();
    }

    private Object[][][] getData(Connection con) throws SQLException {
        Object[][][] data = new Object[panels.length][][];

        for (int i = 0; i < data.length; ++i) {
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            if (i == 0) {
                rs = statement.executeQuery("SELECT * FROM " + tableNames[i] + " WHERE PATRON_LOGINID = " + loginId);
            } else {
                rs = statement.executeQuery("SELECT * FROM " + tableNames[i]);
            }

            rs.last();
            data[i] = new Object[rs.getRow()][];
            rs.beforeFirst();

            for (int j = 0; rs.next(); ++j) {
                data[i][j] = new Object[columnNames[i].length];
                for (int k = 0; k < data[i][j].length; ++k) {
                    data[i][j][k] = rs.getString(k + 1);
                }
            }
        }

        return data;
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

    private class PatronInfoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            createPatronInfoDialog();
        }
    }

    private void createPatronInfoDialog(){
        Object[] prompt = new Object[PATRON_COLUMNS.length * 2];

        String[] patronColumns = new String[]{ 
            DatabaseContract.PatronTable.LOGIN_ID_COLUMN,
            DatabaseContract.PatronTable.FIRST_NAME_COLUMN,
            DatabaseContract.PatronTable.MIDDLE_NAME_COLUMN,
            DatabaseContract.PatronTable.LAST_NAME_COLUMN,
            DatabaseContract.PatronTable.PASSWORD_COLUMN,
            DatabaseContract.PatronTable.HOUSE_NO_COLUMN,
            DatabaseContract.PatronTable.STREET_COLUMN,
            DatabaseContract.PatronTable.BARANGAY_COLUMN,
            DatabaseContract.PatronTable.CITY_COLUMN,
            DatabaseContract.PatronTable.UNPAID_FINE_COLUMN };

        String[] values = queryPatron();
        JTextField[] fields = new JTextField[values.length];
        for (int i = 0; i < fields.length; ++i){
            fields[i] = new JTextField(values[i]);
        }
        fields[0].setEditable(false);
        fields[9].setEditable(false);

        for (int i = 0, j = 0; i < prompt.length; ++j){
            prompt[i++] = patronColumns[j];
            prompt[i++] = fields[j];
        }

        Object[] options = { "Save", "Cancel" };

        JOptionPane optionPane = new JOptionPane(prompt, JOptionPane.PLAIN_MESSAGE, 
        JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        JDialog dialog = new JDialog(this, patronInfoButton.getText(), true);
        dialog.setContentPane(optionPane);

        optionPane.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())){
                    if (optionPane.getValue().equals(options[0])){
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        try {
                            String[] data = new String[values.length];

                            for (int i = 0, j = 0; i < data.length; ++j){
                                data[i++] = ((JTextField)prompt[++j]).getText();
                            }

                            CallableStatement cs = con.prepareCall("{call edit_user(?,?,?,?,?,?,?,?,?,?,?)}");

                            cs.setString(1,values[0]);
                            for (int i = 0; i < data.length; ++i){
                                cs.setString(i + 2, data[i]);
                            }

                            boolean isDataUnchanged = true;
                            for (int i = 0; i < data.length; ++i){
                                if (!values[i].equals(data[i])){
                                    isDataUnchanged = false;
                                    break;
                                }
                            }
                            if (isDataUnchanged){
                                dialog.dispose();
                                return;
                            }

                            int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to save your changes?", 
                            patronInfoButton.getText(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (response == JOptionPane.YES_OPTION){
                                cs.executeUpdate();

                                Object[][][] dataModels = getData(con);
                                for (int i = 0; i < tableModels.length; ++i){
                                    tableModels[i].setDataVector(dataModels[i], columnNames[i]);
                                    tableModels[i].fireTableDataChanged();
                                }

                                dialog.dispose();

                                JOptionPane.showMessageDialog(rootPane, "Profile Updated!", 
                                patronInfoButton.getText(), JOptionPane.INFORMATION_MESSAGE);
                            }

                        } catch (SQLException e) {
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

    private String[] queryPatron() {
        String[] result = new String[PATRON_COLUMNS.length];
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM PATRON WHERE LOGINID = " + loginId);
            if (resultSet.next()){
                for (int i = 0; i < result.length; ++i){
                    result[i] = resultSet.getString(i+1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private class ReserveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] rows = tables[1].getSelectedRows();
            if (rows.length == 0) 
                return;
            else if (rows.length > 2){
                JOptionPane.showMessageDialog(rootPane, "You can only reserve at most 2 books.", 
                reserveButton.getText(), JOptionPane.WARNING_MESSAGE);
                return;
            }

            createReserveDialog(rows);
        }
    }

    private void createReserveDialog(int[] rows){
        Vector<String[]> keys = new Vector<String[]>();

        keys.add(new String[rows.length]);
        keys.add(new String[rows.length]);
        for (int i = 0; i < rows.length; ++i){
            keys.get(0)[i] = String.valueOf(tables[1].getValueAt(rows[i],0));
            keys.get(1)[i] = String.valueOf(tables[1].getValueAt(rows[i],1));
        }

        int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to reserve selected book/s?", 
        reserveButton.getText(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION){
            try {
                for (int i = 0; i < rows.length; ++i){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    String dateStr = DATEFORMAT.format(calendar.getTime());

                    Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT TRANSACTION_ID_SEQ.CURRVAL FROM DUAL");

                    String transactionSeq = null;
                    if (resultSet.next()){
                        transactionSeq = resultSet.getString(1);
                    }

                    String[] data = new String[]{ 
                        transactionSeq, 
                        dateStr, 
                        "RESERVE", 
                        loginId, 
                        keys.get(0)[i], 
                        keys.get(1)[i] };

                    LibrarianFrame.reserveValidation(con, data);

                    CallableStatement cs = con.prepareCall("{call add_transaction(?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?)}");
                   
                    for (int j = 0; j < data.length; ++j) {
                        cs.setString(j + 1, data[j]);
                    }

                    cs.executeUpdate();

                    statement.executeQuery("SELECT TRANSACTION_ID_SEQ.NEXTVAL FROM DUAL");
                }

                Object[][][] dataModels = getData(con);
                for (int i = 0; i < tableModels.length; ++i){
                    tableModels[i].setDataVector(dataModels[i], columnNames[i]);
                    tableModels[i].fireTableDataChanged();
                }

                JOptionPane.showMessageDialog(rootPane, "Reservation Successful!", 
                reserveButton.getText(), JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(rootPane, e.getMessage(),
                "SQLException",JOptionPane.ERROR_MESSAGE);
            }
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