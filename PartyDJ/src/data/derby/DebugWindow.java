package data.derby;

import basics.Controller;
import data.SettingException;
import data.SettingListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * Ermöglicht es vom PartyDJ aus direkt auf die Derby Datenbank zuzugreifen.
 * 
 * @author Eraser
 */
public class DebugWindow extends javax.swing.JFrame
{
	private static final long serialVersionUID = 5132578068185235035L;
	private JScrollPane listsScrollPane;
	private JScrollPane listsContentScrollPane;
	private JScrollPane settingsScrollPane;
	private JList<String> listsList;
	private JButton executeUpdate;
	private JButton executeQuery;
	private JTextField sqlText;
	private JTable settingsTable;

	private final DerbyDB data = (DerbyDB) Controller.getInstance().getData();
	private ListsContentTableModel listsContentTableModel;
	private JButton fixDB;

	public DebugWindow()
	{
		super("Database Dubug Window - Don't do shit!");
		initGUI();

		setVisible(true);
	}

	private void initGUI()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		{
			listsScrollPane = new JScrollPane();
			{
				listsList = new JList<>(new ListsListModel());
				listsScrollPane.setColumnHeaderView(listsList);
				listsList.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(final MouseEvent e)
					{
						final String valueString = listsList.getSelectedValue();
						final int valueInt = Integer.parseInt(valueString.substring(0, valueString.indexOf(':')));
						listsContentTableModel.openList(valueInt);
						listsContentTableModel.fireTableChanged(null);
					}
				});
			}
		}
		{
			listsContentScrollPane = new JScrollPane();
			{
				listsContentTableModel = new ListsContentTableModel();
				final JTable listsContentTable = new JTable(listsContentTableModel, new ListsContentTableColumnModel());
				listsContentScrollPane.setViewportView(listsContentTable);
			}
		}
		{
			settingsScrollPane = new JScrollPane();
			{
				settingsTable = new JTable(new SettingsTableModel(), new SettingsTableColumnModel());
				settingsScrollPane.setViewportView(settingsTable);
			}
		}
		{
			sqlText = new JTextField();
			sqlText.setText("SELECT * FROM SETTINGS");
		}
		{
			executeQuery = new JButton();
			executeQuery.setText("Execute Query");
			executeQuery.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					final StringBuilder content = new StringBuilder();

					try (Statement statement = data.conn.createStatement())
					{
						try (ResultSet rs = statement.executeQuery(sqlText.getText()))
						{
							final ResultSetMetaData metaData = rs.getMetaData();
							final int cCount = metaData.getColumnCount();
							for (int i = 1; i <= cCount; i++)
							{
								content.append(metaData.getColumnLabel(i).replace("\"", "\"\""));
								if (i <= cCount - 1)
									content.append(';');
							}
							content.append('\n');
							while (rs.next())
							{
								for (int i = 1; i <= cCount; i++)
								{
									final Object o = rs.getObject(i);
									if (o != null)
										content.append(o.toString().replace("\"", "\"\""));
									if (i <= cCount - 1)
										content.append(';');
								}
								content.append('\n');
							}
							new DataOutputDialog(DebugWindow.this, content.toString());
						}
					}
					catch (final SQLException e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					catch (final Throwable e1)
					{
						new DataOutputDialog(DebugWindow.this, e1);
					}
				}
			});
		}
		{
			executeUpdate = new JButton();
			executeUpdate.setText("Execute Update");
			executeUpdate.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					try (Statement statement = data.conn.createStatement())
					{
						final int rows = statement.executeUpdate(sqlText.getText());
						JOptionPane.showMessageDialog(null, rows, "PartyDJ", JOptionPane.INFORMATION_MESSAGE);
					}
					catch (final Throwable e1)
					{
						new DataOutputDialog(DebugWindow.this, e1);
					}
				}
			});
		}
		fixDB = new JButton("Fix Lists");
		fixDB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					data.fixDb();
					JOptionPane.showMessageDialog(null, "Fertig", "Datenbank reparieren.", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (final SQLException e1)
				{
					new DataOutputDialog(DebugWindow.this, e1);
				}
			}
		});
		final GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(10).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(listsScrollPane, GroupLayout.PREFERRED_SIZE, 590, GroupLayout.PREFERRED_SIZE).addComponent(listsContentScrollPane, GroupLayout.PREFERRED_SIZE, 590, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(fixDB).addComponent(settingsScrollPane, GroupLayout.PREFERRED_SIZE, 343, GroupLayout.PREFERRED_SIZE))).addGroup(groupLayout.createSequentialGroup().addComponent(sqlText, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE).addGap(6).addComponent(executeQuery).addGap(10).addComponent(executeUpdate))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(11).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addComponent(listsScrollPane, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE).addGap(11).addComponent(listsContentScrollPane, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addComponent(settingsScrollPane, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(fixDB))).addGap(11).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(1).addComponent(sqlText, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)).addComponent(executeQuery).addComponent(executeUpdate))));
		getContentPane().setLayout(groupLayout);
		pack();
		this.setSize(950, 600);
	}

	class SettingsTableModel extends AbstractTableModel implements SettingListener
	{
		private static final long serialVersionUID = -5008336899891404273L;
		private final List<String[]> settings = new ArrayList<>();

		public SettingsTableModel()
		{
			try (ResultSet rs = data.queryRS("SELECT * FROM SETTINGS"))
			{
				while (rs.next())
					settings.add(new String[] { rs.getString(1), rs.getString(2) });

				data.addSettingListener(this);
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public int getRowCount()
		{
			return settings.size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			return settings.get(rowIndex)[columnIndex];
		}

		@Override
		public void settingChanged(final String name, final String value)
		{
			boolean found = false;
			for (int i = 0; i < settings.size(); i++)
			{
				if (settings.get(i)[0].equals(name))
				{
					final String[] tupel = settings.get(i);
					tupel[1] = value;
					found = true;
					break;
				}
			}
			if (!found)
				settings.add(new String[] { name, value });

			fireTableChanged(null);
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex)
		{
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
		{
			if (columnIndex == 1)
			{
				try
				{
					data.writeSetting(settings.get(rowIndex)[0], aValue.toString());
				}
				catch (final SettingException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	static class SettingsTableColumnModel extends DefaultTableColumnModel
	{
		private static final long serialVersionUID = -3855341471424485718L;
		private final TableColumn[] columns;

		public SettingsTableColumnModel()
		{
			final TableColumn column1 = new TableColumn();
			column1.setModelIndex(0);
			column1.setHeaderValue("Setting");
			final TableColumn column2 = new TableColumn();
			column2.setModelIndex(1);
			column2.setHeaderValue("Wert");
			columns = new TableColumn[] { column1, column2 };
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public TableColumn getColumn(final int columnIndex)
		{
			return columns[columnIndex];
		}
	}

	class ListsContentTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -5008336899891404273L;
		private final List<String[]> content = new ArrayList<>();
		private int listIndex = 0;

		public ListsContentTableModel()
		{
			openList();
		}

		public void openList()
		{
			openList(listIndex);
		}

		@SuppressWarnings("resource")
		public void openList(@SuppressWarnings("hiding") final int listIndex)
		{
			this.listIndex = listIndex;
			content.clear();
			try
			{
				ResultSet rs;
				if (listIndex == 0)
					rs = data.queryRS("SELECT LIST, POSITION, NAME FROM FILES, LISTS_CONTENT WHERE FILES.INDEX = LISTS_CONTENT.INDEX ORDER BY LIST, POSITION");
				else
					rs = data.queryRS("SELECT LIST, POSITION, NAME FROM FILES, LISTS_CONTENT WHERE FILES.INDEX = LISTS_CONTENT.INDEX AND LIST = ? ORDER BY LIST, POSITION", Integer.toString(listIndex));
				while (rs.next())
					content.add(new String[] { rs.getString(1), rs.getString(2), rs.getString(3) });
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public int getRowCount()
		{
			return content.size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			return content.get(rowIndex)[columnIndex];
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex)
		{
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
		{
			if (columnIndex == 1)
			{
				try
				{
					data.executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = ?", aValue.toString(), content.get(rowIndex)[0], content.get(rowIndex)[1]);
					openList();
				}
				catch (final SQLException e)
				{
					e.printStackTrace();
				}
			}

		}
	}

	static class ListsContentTableColumnModel extends DefaultTableColumnModel
	{
		private static final long serialVersionUID = -3855341471424485718L;
		private final TableColumn[] columns;

		public ListsContentTableColumnModel()
		{
			final TableColumn column1 = new TableColumn();
			column1.setModelIndex(0);
			column1.setHeaderValue("Liste");
			column1.setMaxWidth(80);
			final TableColumn column2 = new TableColumn();
			column2.setModelIndex(1);
			column2.setHeaderValue("Position");
			column2.setMaxWidth(80);
			final TableColumn column3 = new TableColumn();
			column3.setModelIndex(2);
			column3.setHeaderValue("Track");
			columns = new TableColumn[] { column1, column2, column3 };
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public TableColumn getColumn(final int columnIndex)
		{
			return columns[columnIndex];
		}
	}

	class ListsListModel extends AbstractListModel<String>
	{
		private static final long serialVersionUID = 1L;
		private final List<String> lists = new ArrayList<>();

		public ListsListModel()
		{
			lists.add("0: Alle");
			try (ResultSet rs = data.queryRS("SELECT INDEX, NAME FROM LISTS ORDER BY INDEX"))
			{
				while (rs.next())
					lists.add(rs.getString(1) + ": " + rs.getString(2));
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public String getElementAt(final int index)
		{
			return lists.get(index);
		}

		@Override
		public int getSize()
		{
			return lists.size();
		}
	}
}
