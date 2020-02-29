import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.sun.rowset.CachedRowSetImpl;
import javax.sql.rowset.CachedRowSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import java.sql.*;
import java.util.regex.PatternSyntaxException;

public class LibrarianFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Connection con;

	private JTabbedPane tabbedPane;

	private String[] panelTitles = { "Transaction", "Patron", "Book", "Librarian" };
	private JPanel[] panels = new JPanel[panelTitles.length];

	private JTable[] tables = new JTable[panels.length];
	private CachedRowSet[] rowSets = new CachedRowSet[tables.length];
	private CustomTableModel[] tableModels = new CustomTableModel[tables.length];
	private TableRowSorter<CustomTableModel>[] sorters = new TableRowSorter[tableModels.length];

	private String[][] columnNames = {
			new String[] { "Transaction ID", "Transaction Date", "Transaction Mode", "Login ID", "ISBN",
					"Copy Number" },
			new String[] { "Login ID", "First Name", "Middle Name", "Last Name", "Password", "House No.", "Street",
					"Barangay", "City", "Unpaid Fine" },
			new String[] { "ISBN", "Copy Number", "Title", "Year of Publication", "Current Status", "Status Date",
					"Shelf ID" },
			new String[] { "Login ID", "First Name", "Middle Name", "Last Name", "Password", "House No.", "Street",
					"Barangay", "City", "Unpaid Fine", "Patron Access", "Librarian Access", "Book Access",
					"Transaction Access" } };

	private JPanel[] searchPanels = new JPanel[panels.length];
	private JLabel[] searchLabels = new JLabel[searchPanels.length];
	private JTextField[] searchFields = new JTextField[searchPanels.length];
	private JComboBox<String>[] searchBoxes = new JComboBox[searchPanels.length];

	private JButton[] addButtons = new JButton[panels.length];
	private JButton[] editButtons = new JButton[panels.length];
	private JButton[] deleteButtons = new JButton[panels.length];
	private JButton[] finishButtons = new JButton[panels.length];

	public LibrarianFrame(Connection con) throws SQLException {
		this.con = con;
		this.con.setAutoCommit(false);

		setTitle("Librarian");

		tabbedPane = new JTabbedPane();

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);

		for (int i = 0; i < panels.length; ++i) {
			panels[i] = new JPanel(new GridBagLayout());
			tabbedPane.add(panelTitles[i], panels[i]);

			tables[i] = new JTable() {
				private static final long serialVersionUID = 1L;

				public boolean getScrollableTracksViewportWidth() {
					return getPreferredSize().width < getParent().getWidth();
				}
			};

			rowSets[i] = new CachedRowSetImpl();
			rowSets[i].setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
			rowSets[i].setConcurrency(ResultSet.CONCUR_UPDATABLE);
			rowSets[i].setUsername(AccessCredentials.DATABASE_USERNAME);
			rowSets[i].setPassword(AccessCredentials.DATABASE_PASSWORD);
			rowSets[i].setUrl(AccessCredentials.DATABASE_HOST);
			if (i == 1) {
				rowSets[i].setCommand("SELECT * FROM PATRON WHERE LOGINID NOT IN (SELECT LOGINID FROM LIBRARIAN)");
			} else if (i == 3) {
				rowSets[i].setCommand("SELECT P.LOGINID, P.FIRSTNAME, P.MIDDLENAME, P.LASTNAME, P.PASSWORD, "
						+ "P.HOUSENO, P.STREET, P.BARANGAY, P.CITY, P.UNPAIDFINE, "
						+ "L.PATRONACCESS, L.LIBACCESS, L.BOOKACCESS, L.TRANSACCESS "
						+ "FROM PATRON P, LIBRARIAN L WHERE P.LOGINID = L.LOGINID");
			} else {
				rowSets[i].setCommand("SELECT * FROM " + panelTitles[i].toUpperCase());
			}
			rowSets[i].execute();

			tableModels[i] = new CustomTableModel(rowSets[i]);
			sorters[i] = new TableRowSorter<CustomTableModel>(tableModels[i]);
			tables[i].setModel(tableModels[i]);
			tables[i].setPreferredScrollableViewportSize(new Dimension(800, 350));
			tables[i].setFillsViewportHeight(true);
			tables[i].getTableHeader().setReorderingAllowed(false);
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

			addButtons[i] = new JButton("Add " + panelTitles[i]);
			editButtons[i] = new JButton("Edit " + panelTitles[i]);
			deleteButtons[i] = new JButton("Delete " + panelTitles[i]);
			finishButtons[i] = new JButton("Finish");

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

		tabbedPane.setFocusable(false);

		add(tabbedPane);

		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", "Exit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					try {
						con.rollback();
					} catch (SQLException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(rootPane, e.getMessage(),
						"SQLException", JOptionPane.ERROR_MESSAGE);
					}
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

	private void finishDialog() {
		Object[] options = { "Save", "Don't Save", "Cancel" };
		JOptionPane optionPane = new JOptionPane("Do you want to save your changes?", JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null, options);
		JDialog dialog = new JDialog(this, "Finish", true);
		dialog.setContentPane(optionPane);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())) {
					if (optionPane.getValue().equals(options[0])) {
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
						try {
							con.commit();
						} catch (SQLException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(rootPane, e.getMessage(), 
							"SQLException",JOptionPane.ERROR_MESSAGE);
						}
						createLoginFrame();

					} else if (optionPane.getValue().equals(options[1])) {
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
						try {
							con.rollback();
						} catch (SQLException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(rootPane, e.getMessage(),
							"SQLException", JOptionPane.ERROR_MESSAGE);
						}
						createLoginFrame();
					}
					else if (optionPane.getValue().equals(options[2])){
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

	private class FinishListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			finishDialog();
		}
	}

	private class CustomTableModel implements TableModel {
		private CachedRowSet crs;
		private ResultSetMetaData metadata;
		private int rows, cols;

		public CustomTableModel(CachedRowSet crs) throws SQLException {
			this.crs = crs;
			this.metadata = this.crs.getMetaData();
			cols = metadata.getColumnCount();
			this.crs.beforeFirst();
			this.rows = 0;
			while (this.crs.next()) {
				++this.rows;
			}
			this.crs.beforeFirst();
		}

		public void close() {
			try {
				crs.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(rootPane, e.getMessage(),
				"SQLException", JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			close();
		}

		@Override
		public int getRowCount() {
			return rows;
		}

		@Override
		public int getColumnCount() {
			return cols;
		}

		@Override
		public String getColumnName(int columnIndex) {
			try {
				return this.metadata.getColumnLabel(columnIndex + 1);
			} catch (SQLException e) {
				e.printStackTrace();
				return e.toString();
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			try {
				this.crs.absolute(rowIndex + 1);
				Object obj = this.crs.getObject(columnIndex + 1);
				if (obj == null) {
					return null;
				} else {
					return obj.toString();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return e.toString();
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) { }

		@Override
		public void addTableModelListener(TableModelListener l) { }

		@Override
		public void removeTableModelListener(TableModelListener l) { }
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
				if (searchBoxes[i].equals((JComboBox<String>)e.getSource())){
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
				RowFilter<CustomTableModel,Object> rowFilter = null;
				rowFilter = RowFilter.regexFilter("(?i)" + searchFields[i].getText(), 
				searchBoxes[i].getSelectedIndex());
				sorters[i].setRowFilter(rowFilter);
			}
			catch (NullPointerException e) {

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