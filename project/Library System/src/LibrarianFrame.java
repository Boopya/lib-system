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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

public class LibrarianFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Connection con;

	private JTabbedPane tabbedPane;

	private String[] panelTitles = { "Transaction", "Patron", "Book", "Librarian" };
	private JPanel[] panels = new JPanel[panelTitles.length];

	private JTable[] tables = new JTable[panels.length];
	private Object[][][] data = new Object[panels.length][][];
	private DefaultTableModel[] tableModels = new DefaultTableModel[tables.length];
	private TableRowSorter<DefaultTableModel>[] sorters = new TableRowSorter[tableModels.length];

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
	private JComboBox<?>[] searchBoxes = new JComboBox[searchPanels.length];

	private JButton[] addButtons = new JButton[panels.length];
	private JButton[] editButtons = new JButton[panels.length];
	private JButton[] deleteButtons = new JButton[panels.length];
	private JButton[] finishButtons = new JButton[panels.length];

	public LibrarianFrame(Connection con) throws SQLException {
		this.con = con;
		this.con.setAutoCommit(false);

		data = getData(con);

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
						JOptionPane.showMessageDialog(rootPane, e.getMessage(), "SQLException",
								JOptionPane.ERROR_MESSAGE);
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
				rs = statement.executeQuery("SELECT * FROM " + panelTitles[i]);
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
							JOptionPane.showMessageDialog(rootPane, e.getMessage(), "SQLException",
									JOptionPane.ERROR_MESSAGE);
						}
						createLoginFrame();

					} else if (optionPane.getValue().equals(options[1])) {
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
						try {
							con.rollback();
						} catch (SQLException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(rootPane, e.getMessage(), "SQLException",
									JOptionPane.ERROR_MESSAGE);
						}
						createLoginFrame();
					} else if (optionPane.getValue().equals(options[2])) {
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

	private class AddListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < addButtons.length; ++i) {
				if (addButtons[i].equals((JButton) e.getSource())) {
					createAddDialog(i);
				}
			}
		}
	}

	private void createAddDialog(int table) {
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

		if (table == 0) {
			field = new Object[] { new JTextField(), dateSpinner,
					new JComboBox<String>(new String[] { "LOAN", "RETURN", "RESERVE" }), new JTextField(),
					new JTextField(), new JTextField() };
		} else if (table == 1) {
			field = new Object[] { new JTextField(), new JTextField(), new JTextField(), new JTextField(),
					new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
					new JTextField() };
		} else if (table == 2) {
			field = new Object[] { new JTextField(), new JTextField(), new JTextField(), new JTextField(),
					new JComboBox<String>(new String[] { "ON-SHELF", "ON-HOLD", "ON-LOAN" }), dateSpinner,
					new JTextField() };
		} else if (table == 3) {
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

		JOptionPane optionPane = new JOptionPane(prompt, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
				options, options[0]);

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
							Object[] rowData = null;

							if (table == 0) {
								for (int i = 0, j = 0; i < data.length; ++i){
									if (i == 0 || i  < data.length - 3){
										data[i++] = ((JTextField) prompt[++j]).getText();
									}
									else if (i == 1){
										data[i++] = new SimpleDateFormat("dd/MM/yyyy")
										.format(((JSpinner)prompt[++j]).getValue());
									}
									else {	// Index 2
										data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
										.getSelectedItem());
									}
								}

								cs = con.prepareCall("{call add_transaction(?,?,?,?,?,?)}");

							} else if (table == 1) {
								for (int i = 0, j = 0; i < data.length; ++j) {
									data[i++] = ((JTextField) prompt[++j]).getText();
								}

								cs = con.prepareCall("{call add_user(?,?,?,?,?,?,?,?,?,?)}");

							} else if (table == 2) {
								for (int i = 0, j = 0; i < data.length; ++j){
									if (i != 4 || i != 5){
										data[i++] = ((JTextField) prompt[++j]).getText();
									}
									else if (i == 4){
										data[i++] = String.valueOf(((JComboBox<?>)prompt[++j])
										.getSelectedItem());
									}
									else {	// Index 5
										data[i++] = new SimpleDateFormat("dd/MM/yyyy")
										.format(((JSpinner)prompt[++j]).getValue());
									}
								}

								cs = con.prepareCall("{call add_book(?,?,?,?,?,?,?)}");

							} else if (table == 3) {
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

							rowData = new Object[data.length];
							for (int i = 0; i < data.length; ++i) {
								cs.setString(i + 1, data[i]);
								rowData[i] = data[i];
							}

							cs.executeUpdate();
							tableModels[table].addRow(rowData);
							dialog.dispose();

							JOptionPane.showMessageDialog(rootPane,panelTitles[table] + " Added!", 
							addButtons[table].getText(),JOptionPane.INFORMATION_MESSAGE);
						} 
						catch (SQLException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(rootPane, e.getMessage(), "SQLException",
									JOptionPane.ERROR_MESSAGE);
						}
					} else if (optionPane.getValue().equals(options[1])) {
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

		}
	}

	private class DeleteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

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