package gui.settings;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import lists.ListException;
import basics.Controller;
import com.jgoodies.forms.layout.*;
import data.IData;
import data.ListAdapter;
import data.SettingListener;

public class Shuffle extends JPanel 
{
	private static final long serialVersionUID = 2304624209647591659L;
	
	public Shuffle() 
	{
		initComponents();
	}
	
	protected JScrollPane listTable()
	{
		try
		{
			List<String> listNames = Controller.getInstance().getData().getLists();
			IData data = Controller.getInstance().getData();
			String[] labels = new String[]{"Liste", "Priorität", "Spielwahrscheinlichkeit"};
			Object[][] lists = new Object[listNames.size()][3];
			
			for(int i = 0; i < listNames.size(); i++)
			{
				lists[i][0] = listNames.get(i);
			}
			
			JTable listTable = new JTable(new ShuffleTableModel(lists, labels));
			int skipped = 0;
			
			Map<Integer, JSpinner> spinners = new HashMap<Integer, JSpinner>(4);
			Map<String, JSpinner> namedSp = new HashMap<String, JSpinner>(4);
			PriorityListener prListener = new PriorityListener(spinners, namedSp, listTable);
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
			
			spinner.setValue(Integer.parseInt(data.readSetting("MasterListPriority", "1")));
			spinner.addChangeListener(new SpinnerListener("Hauptliste"));
			spinners.put(0, spinner);
			namedSp.put("MasterListPriority", spinner);
			
			listTable.getColumnModel().addColumnModelListener(new ColumnListener());
			String[] width = data.readSetting("ColumnSize", "@407@91@138").split("@");
			for(int i = 0; i < 3; i++)
				listTable.getColumnModel().getColumn(i).setPreferredWidth(Integer.parseInt(width[i + 1]));
			
			listTable.setValueAt("Hauptliste", 0, 0);
			data.addListListener(prListener);
			data.addSettingListener(prListener);
			for(int i = 1; i <= listNames.size(); i++)
			{
				String list = listNames.get(i - 1);
				
				if(list.equals(basics.Controller.getInstance().getLastPlayedName()))
				{
					skipped++;
				}
				else
				{
					JSpinner sp = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
					sp.setValue(data.getListPriority(list));
					sp.setName(list);
					sp.addChangeListener(new SpinnerListener(list));
					spinners.put(i - skipped, sp);
					namedSp.put(list, sp);
					listTable.setValueAt(list , i - skipped, 0);
				}
			}
			listTable.setFillsViewportHeight(true);
			listTable.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(spinners));
			listTable.getColumnModel().getColumn(1).setCellRenderer(new SpinnerRenderer(spinners));
			JScrollPane scroll = new JScrollPane(listTable);
			scroll.setVisible(true);
			prListener.calcListPercent();
			
			return scroll;
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JScrollPane();
	}
	
	protected class SpinnerListener implements ChangeListener
	{
		private final String name;
		public SpinnerListener(String name)
		{
			this.name = name;
		}
		@Override
		public void stateChanged(ChangeEvent ce)
		{
			IData data = Controller.getInstance().getData();
			if(ce.getSource() instanceof JSpinner)
			{
				try
				{
					if(name.equalsIgnoreCase("hauptliste"))
					{
						data.writeSetting("MasterListPriority", ((JSpinner)ce.getSource()).getValue().toString());
						return;
					}
					data.setListPriority(name, (Integer)((JSpinner)ce.getSource()).getValue());
				}
				catch (ListException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	protected class ColumnListener implements TableColumnModelListener
	{
		IData data = Controller.getInstance().getData();
		
		@Override
		public void columnAdded(TableColumnModelEvent e){}

		@Override
		public void columnMarginChanged(ChangeEvent e)
		{
			if(e.getSource() instanceof DefaultTableColumnModel)
			{
				String val = "";
				for(int i = 0; i < 3; i++)
				{
					int size = ((DefaultTableColumnModel)e.getSource()).getColumn(i).getPreferredWidth();
					val += (char) 64 + String.valueOf(size);
				}
				data.writeSetting("ColumnSize", val);
			}
		}

		@Override
		public void columnMoved(TableColumnModelEvent e){}

		@Override
		public void columnRemoved(TableColumnModelEvent e){}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e){}
	}
	
	protected class PriorityListener extends ListAdapter implements SettingListener
	{
		private Map<String, JSpinner> namedSp;
		private JTable table;
		private Map<Integer, JSpinner> spinners;

		public PriorityListener(Map<Integer, JSpinner> spinners, Map<String, JSpinner> namedSp, JTable table)
		{
			this.namedSp = namedSp;
			this.spinners = spinners;
			this.table = table;
		}

		@Override
		public void listPriorityChanged(final String listName, final int newPriority)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					namedSp.get(listName).setValue(newPriority);
					calcListPercent();
					table.repaint();
				}});
			
		}

		@Override
		public void settingChanged(final String name, final String value)
		{
			if(namedSp.get(name) != null)
			{
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run()
					{
						namedSp.get(name).setValue(Integer.parseInt(value));
						calcListPercent();
						table.repaint();
					}});
				
			}
		}
		
		public void calcListPercent()
		{
			SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run()
					{
						try
						{
							IData data = Controller.getInstance().getData();
							int sum = Integer.parseInt(data.readSetting("MasterListPriority", "1"));
							int val = 0;
							for(int i = 0; i < namedSp.size(); i++)
							{
								String n = spinners.get(i).getName();
								if(n != null)
								{
									sum += data.getListPriority(n);
								}
								if(sum == 0)
								{
									table.setValueAt(String.format("%2.2f", 1d / data.getLists().size()) + "%", 0, 2);
									return;
								}
							}
							
							for(int i = 0; i < namedSp.size(); i++)
							{
								String n = spinners.get(i).getName();
								if(n != null)
								{
									val = data.getListPriority(spinners.get(i).getName());
									double percent = ((double)val / sum) * 100;
									table.setValueAt(String.format("%2.2f", (Double)percent) + "%", i, 2);
								}
							}
							double per = (Double.parseDouble(data.readSetting("MasterListPriority", "1")) /sum) * 100;
							table.setValueAt(String.format("%2.2f", per) + "%", 0, 2);
						}
						catch (ListException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}});
		}
	}
	
	protected class ShuffleTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 4845502871704418973L;
		Object[][] rowData;
		String[] columnNames;
		
		public ShuffleTableModel(Object[][] rowData, String[] columnNames)
		{
			this.rowData = rowData;
			this.columnNames = columnNames;
		}
		
		@Override
		public boolean isCellEditable(int row, int column)
		{
			switch(column)
			{
				case 0:				return false;
				//TODO Wunschliste ohne Priorität mit check Box o.ä.
				case 1:				if(rowData[row][0].equals("Wunschliste"))
										return false;
									return true;
				case 2:				return false;
				default:			return false;
			}
		}
		
		@Override
		public String getColumnName(int column)
		{
			return columnNames[column];
		}
		
		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public int getRowCount()
		{
			return rowData.length;
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			return rowData[row][column];
		}
		
		@Override
		public void setValueAt(final Object value, final int row, final int col)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					 rowData[row][col] = value;
					 fireTableCellUpdated(row, col);
				}});
           
		}
	}
	
	protected class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
	{
		private static final long serialVersionUID = -7664852358641606118L;
		protected Map<Integer, JSpinner> spinners;
		
		public SpinnerEditor(Map<Integer, JSpinner> spinners)
		{
			super();
			this.spinners = spinners;
		}
		
		@Override
		public Object getCellEditorValue()
		{
			return spinners.toString();
		}

		@Override
		public JSpinner getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) 
		{
            return spinners.get(row);
        }
	}
	
	protected class SpinnerRenderer implements TableCellRenderer
	{
		private static final long serialVersionUID = 5606482980770475335L;
		Map<Integer, JSpinner> spinners;
		
		public SpinnerRenderer(Map<Integer, JSpinner> spinners)
		{
			super();
			this.spinners = spinners;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			return spinners.get(row);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sam Meier
		label1 = new JLabel();
		label2 = new JLabel();
		CellConstraints cc = new CellConstraints();

		//======== this ========

		setLayout(new FormLayout(
			"$rgap, $lcgap, 323dlu:grow, $lcgap, 2dlu",
			"fill:23dlu, $lgap, fill:18dlu, 16dlu, fill:64dlu:grow, $lgap, $nlgap"));

		//---- label1 ----
		label1.setText("Shuffle");
		label1.setFont(new Font("Tahoma", Font.BOLD, 24));
		add(label1, cc.xy(3, 1));

		//---- label2 ----
		label2.setText("Hier k\u00f6nnen Sie einstellen, mit welcher Wahrscheinlichkeit die Listen gespielt werden.");
		label2.setFont(new Font("Tahoma", Font.ITALIC, 12));
		add(label2, cc.xy(3, 3));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		add(listTable(), cc.xy(3, 5));
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sam Meier
	private JLabel label1;
	private JLabel label2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
