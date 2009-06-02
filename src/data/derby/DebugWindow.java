package data.derby;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import data.SettingException;
import data.SettingListener;
import basics.Controller;

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
	private JList listsList;
	private JButton executeUpdate;
	private JButton executeQuery;
	private JTextField sqlText;
	private JTable settingsTable;
	
	private final DerbyDB data = (DerbyDB)Controller.getInstance().getData();
	private ListsContentTableModel listsContentTableModel;
	private final DebugWindow Me = this;
	
	public DebugWindow() 
	{
		super("Database Dubug Window - Don't do shit!");
		initGUI();
		
		setVisible(true);
	}
	
	private void initGUI()
	{
		GroupLayout thisLayout = new GroupLayout(getContentPane());
		getContentPane().setLayout(thisLayout);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		{
			listsScrollPane = new JScrollPane();
			{
				listsList = new JList(new ListsListModel());
				listsScrollPane.setViewportView(listsList);
				listsList.addMouseListener(new MouseAdapter(){
				@Override public void mouseClicked(MouseEvent e)
							{
								String valueString = (String)listsList.getSelectedValue();
								int valueInt = Integer.parseInt(valueString.substring(0, valueString.indexOf(':')));
								listsContentTableModel.openList(valueInt);
								listsContentTableModel.fireTableChanged(null);
							}});
			}
		}
		{
			listsContentScrollPane = new JScrollPane();
			{
				final JTable listsContentTable = new JTable(listsContentTableModel = new ListsContentTableModel(), new ListsContentTableColumnModel());
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
			executeQuery.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							StringBuilder content = new StringBuilder();
							try
							{
								ResultSet rs = data.conn.createStatement().executeQuery(sqlText.getText());
								ResultSetMetaData metaData = rs.getMetaData();
								int cCount = metaData.getColumnCount();
								for(int i = 1; i <= cCount; i++)
								{
									content.append(metaData.getColumnLabel(i).replace("\"", "\"\""));
									if(i <= cCount - 1)
										content.append(';');
								}
								content.append('\n');
								while(rs.next())
								{
									for(int i = 1; i <= cCount; i++)
									{
										Object o = rs.getObject(i);
										if(o != null)
											content.append(o.toString().replace("\"", "\"\""));
										if(i <= cCount - 1)
											content.append(';');
									}
									content.append('\n');
								}
								new DataOutputDialog(Me, content.toString());
							}
							catch (Throwable e1)
							{
								new DataOutputDialog(Me, e1);
							}
						}});
		}
		{
			executeUpdate = new JButton();
			executeUpdate.setText("Execute Update");
			executeUpdate.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						int rows = data.conn.createStatement().executeUpdate(sqlText.getText());
						JOptionPane.showMessageDialog(null, rows, "PartyDJ", JOptionPane.INFORMATION_MESSAGE);
					}
					catch (Throwable e1)
					{
						new DataOutputDialog(Me, e1);
					}
				}});
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(listsScrollPane, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(listsContentScrollPane, 0, 377, Short.MAX_VALUE))
			    .addComponent(settingsScrollPane, GroupLayout.Alignment.LEADING, 0, 507, Short.MAX_VALUE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(sqlText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
			    .addComponent(executeQuery, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(executeUpdate, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap());
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addGroup(thisLayout.createParallelGroup()
			            .addComponent(listsScrollPane, GroupLayout.Alignment.LEADING, 0, 576, Short.MAX_VALUE)
			            .addComponent(listsContentScrollPane, GroupLayout.Alignment.LEADING, 0, 576, Short.MAX_VALUE))
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(settingsScrollPane, 0, 328, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(sqlText, 0, 709, Short.MAX_VALUE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(executeQuery, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(executeUpdate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
			.addContainerGap());
		pack();
		this.setSize(950, 600);
	}
	
	class SettingsTableModel extends AbstractTableModel implements SettingListener
	{
		private static final long serialVersionUID = -5008336899891404273L;
		private final List<String[]> settings = new ArrayList<String[]>();
		
		public SettingsTableModel()
		{
			try
			{
				ResultSet rs = data.queryRS("SELECT * FROM SETTINGS");
				while(rs.next())
					settings.add(new String[]{rs.getString(1), rs.getString(2)});
				
				data.addSettingListener(this);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		public int getColumnCount()
		{
			return 2;
		}

		public int getRowCount()
		{
			return settings.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return settings.get(rowIndex)[columnIndex];
		}

		public void settingChanged(String name, String value)
		{
			boolean found = false;
			for(int i = 0; i < settings.size(); i++)
			{
				if(settings.get(i)[0].equals(name))
				{
					settings.get(i)[1] = value;
					found = true;
					break;
				}
			}
			if(!found)
				settings.add(new String[]{name, value});
			
			fireTableChanged(null);			
		}
		
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
	    {
	    	return columnIndex == 1;
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	    {
	    	if(columnIndex == 1)
	    	{
	    		try
				{
					data.writeSetting(settings.get(rowIndex)[0], aValue.toString());
				}
				catch (SettingException e)
				{
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	class SettingsTableColumnModel extends DefaultTableColumnModel
	{
		private static final long serialVersionUID = -3855341471424485718L;
		private final TableColumn[] columns;
		
		public SettingsTableColumnModel()
		{
			TableColumn column1 = new TableColumn();
			column1.setModelIndex(0);
			column1.setHeaderValue("Setting");
			TableColumn column2 = new TableColumn();
			column2.setModelIndex(1);
			column2.setHeaderValue("Wert");
			columns = new TableColumn[]{column1, column2};
		}
		
		@Override
		public int getColumnCount()
		{
			return 2;
		}
		
	    @Override
		public TableColumn getColumn(int columnIndex)
	    {
	    	return columns[columnIndex];
	    }
	}
	
	class ListsContentTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -5008336899891404273L;
		private final List<String[]> content = new ArrayList<String[]>();
		private int listIndex = 0;
		
		public ListsContentTableModel()
		{
			openList();
		}
		
		public void openList()
		{
			openList(listIndex);
		}
		
		@SuppressWarnings("hiding")
		public void openList(int listIndex)
		{
			this.listIndex = listIndex;
			content.clear();
			try
			{
				ResultSet rs;
				if(listIndex == 0)
				 rs = data.queryRS("SELECT LIST, POSITION, NAME FROM FILES, LISTS_CONTENT WHERE FILES.INDEX = LISTS_CONTENT.INDEX ORDER BY LIST, POSITION");
				else
					rs = data.queryRS("SELECT LIST, POSITION, NAME FROM FILES, LISTS_CONTENT WHERE FILES.INDEX = LISTS_CONTENT.INDEX AND LIST = ? ORDER BY LIST, POSITION", Integer.toString(listIndex));
				while(rs.next())
					content.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			fireTableDataChanged();
		}
		
		public int getColumnCount()
		{
			return 3;
		}

		public int getRowCount()
		{
			return content.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return content.get(rowIndex)[columnIndex];
		}
		
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
	    {
	    	return columnIndex == 1;
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	    {
	    	if(columnIndex == 1)
	    	{
	    		try
				{
					data.executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = ?", aValue.toString(), content.get(rowIndex)[0], content.get(rowIndex)[1]);
					openList();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
	    	}
	    	
	    }
	}
	
	class ListsContentTableColumnModel extends DefaultTableColumnModel
	{
		private static final long serialVersionUID = -3855341471424485718L;
		private final TableColumn[] columns;
		
		public ListsContentTableColumnModel()
		{
			TableColumn column1 = new TableColumn();
			column1.setModelIndex(0);
			column1.setHeaderValue("Liste");
			column1.setMaxWidth(80);
			TableColumn column2 = new TableColumn();
			column2.setModelIndex(1);
			column2.setHeaderValue("Position");
			column2.setMaxWidth(80);
			TableColumn column3 = new TableColumn();
			column3.setModelIndex(2);
			column3.setHeaderValue("Track");
			columns = new TableColumn[]{column1, column2, column3};
		}
		
		@Override
		public int getColumnCount()
		{
			return 3;
		}
		
	    @Override
		public TableColumn getColumn(int columnIndex)
	    {
	    	return columns[columnIndex];
	    }
	}
	
	class ListsListModel extends AbstractListModel
	{
		private static final long serialVersionUID = 1L;
		private final List<String> lists = new ArrayList<String>();
		
		public ListsListModel()
		{
			lists.add("0: Alle");
			try
			{
				ResultSet rs = data.queryRS("SELECT INDEX, NAME FROM LISTS ORDER BY INDEX");
				while(rs.next())
					lists.add(rs.getString(1) + ": " + rs.getString(2));
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		public Object getElementAt(int index)
		{
			return lists.get(index);
		}

		public int getSize()
		{
			return lists.size();
		}
	}
}
