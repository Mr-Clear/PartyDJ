package data.derby;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import data.SettingListener;
import basics.Controller;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class DebugWindow extends javax.swing.JFrame
{
	private static final long serialVersionUID = 5132578068185235035L;
	private JScrollPane listsScrollPane;
	private JScrollPane listsContentScrollPane;
	private JScrollPane settingsScrollPane;
	private JList listsList;
	private JTable settingsTable;
	private JTable listsContentTable;
	
	private final DerbyDB data = (DerbyDB)Controller.getInstance().getData();
	private ListsContentTableModel listsContentTableModel;
	//private final Connection conn = data.conn; 
	

	/**
	* Auto-generated main method to display this JFrame
	 * @throws SQLException 
	*/
	/*public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DebugWindow inst = new DebugWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}*/
	
	public DebugWindow() 
	{
		super();
		initGUI();
		
		setVisible(true);
	}
	
	private void initGUI()
	{
		GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
		getContentPane().setLayout(thisLayout);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		{
			listsScrollPane = new JScrollPane();
			{
				listsList = new JList(new ListsListModel());
				listsScrollPane.setViewportView(listsList);
				listsList.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e)
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
				listsContentTable = new JTable(listsContentTableModel = new ListsContentTableModel(), new ListsContentTableColumnModel());
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
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(listsScrollPane, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(listsContentScrollPane, 0, 412, Short.MAX_VALUE))
			    .addComponent(settingsScrollPane, GroupLayout.Alignment.LEADING, 0, 541, Short.MAX_VALUE))
			.addContainerGap());
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addComponent(listsScrollPane, GroupLayout.Alignment.LEADING, 0, 553, Short.MAX_VALUE)
			    .addComponent(listsContentScrollPane, GroupLayout.Alignment.LEADING, 0, 553, Short.MAX_VALUE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(settingsScrollPane, 0, 316, Short.MAX_VALUE)
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
		
		public int getColumnCount()
		{
			return 2;
		}
		
	    public TableColumn getColumn(int columnIndex)
	    {
	    	return columns[columnIndex];
	    }
	}
	
	class ListsContentTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -5008336899891404273L;
		private final List<String[]> content = new ArrayList<String[]>();
		
		public ListsContentTableModel()
		{
			openList(0);
		}
		
		public void openList(int listIndex)
		{
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
		
		public int getColumnCount()
		{
			return 3;
		}
		
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
