import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;

public class PatronFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private JTabbedPane tablesTabbedPane;
    private JPanel[] panels;
    private JTable[] tables;
    private String[] tableNames;
    private String[][] columnNames;
    private Object[][][] data;
    private JButton reserveButton;
    private JPanel[] searchPanels;
    private JLabel[] searchLabels;
    private JTextField[] searchFields;
    private JComboBox<?>[] searchBoxes;
    private DefaultTableModel[] tableModels;
    private TableRowSorter<DefaultTableModel>[] sorters;
    private String loginId;
    
    public PatronFrame(Connection con, String loginId){
        this.con = con;
        this.loginId = loginId;
        setTitle("Patron");
        
        tableNames = new String[] {DatabaseContract.TRANSACTION_TABLE,
                                   DatabaseContract.BOOK_TABLE,};
        
        columnNames = new String[][]{
                    new String[] {DatabaseContract.TransactionTable.TRANSACTION_ID_COLUMN,
                                  DatabaseContract.TransactionTable.TRANSACTION_DATE_COLUMN,
                                  DatabaseContract.TransactionTable.TRANSACTION_MODE_COLUMN,
                                  DatabaseContract.TransactionTable.LOGIN_ID_COLUMN,
                                  DatabaseContract.TransactionTable.ISBN_COLUMN,
                                  DatabaseContract.TransactionTable.COPY_NUMBER_COLUMN},
             
                    new String[] {DatabaseContract.BookTable.ISBN_COLUMN,
                                  DatabaseContract.BookTable.COPY_NUMBER_COLUMN,
                                  DatabaseContract.BookTable.TITLE_COLUMN,
                                  DatabaseContract.BookTable.YEAR_OF_PUBLICATION_COLUMN,
                                  DatabaseContract.BookTable.CURRENT_STATUS_COLUMN,
                                  DatabaseContract.BookTable.STATUS_DATE_COLUMN,
                                  DatabaseContract.BookTable.SHELF_ID_COLUMN},
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

        initComponents();
    }
    
    public void initComponents() {
        tablesTabbedPane = new JTabbedPane();
        
        try {
            data = getData(con);
        }
        catch(SQLException e) {
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
            
            if(i == 1) { // if books panel
                reserveButton = new JButton("Reserve Book");
                reserveButton.addActionListener(new ReserveButtonListener());
                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.anchor = GridBagConstraints.CENTER;
                panels[i].add(reserveButton, constraints);
            }
        }
        
        tablesTabbedPane.setFocusable(false);
        add(tablesTabbedPane);
        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        
        addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent event){
                        int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", 
                        "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (response == JOptionPane.YES_OPTION){
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

        for (int i = 0; i < data.length; ++i){
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            if(i == 0) {
                rs = statement.executeQuery("SELECT * FROM " + tableNames[i] +
                                            " WHERE PATRON_LOGINID = " + loginId);
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
    
    private class ReserveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            // TODO
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