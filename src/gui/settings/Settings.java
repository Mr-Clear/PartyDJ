package gui.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import data.IData;
import data.ListAdapter;
import data.SettingListener;
import lists.ListException;
import basics.Controller;

public class Settings  extends JPanel
{
	private static final long serialVersionUID = -6987892133947674516L;
	
	public Settings()
	{
		initGUI();
	}
	
	private void initGUI()
	{
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		
		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		con.gridx = 0;
		con.gridy = 0;
		con.weightx = 0.0;
		con.weighty = 0.0;
		con.ipadx = GridBagConstraints.REMAINDER;
		con.ipady = 22;
		add(shuffleSetting(), con);
		
		con.gridy = 1;
		con.ipadx = GridBagConstraints.REMAINDER;
		add(listShuffler(), con);
		
		this.setVisible(true);
	}
	
	private JPanel shuffleSetting()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel shuffleSet = new JPanel(new GridBagLayout());
		
		JLabel titel = new JLabel("Shuffle");
		JLabel expl = new JLabel("Hier können Sie einstellen, wie groß die Wahrscheinlichkeit ist, dass ein Lied von einer bestimmten Liste gespielt wird.");
		
		titel.setFont(new Font("SansSerifs", Font.BOLD, 18));
		expl.setFont(new Font("SansSerifs", Font.ITALIC, 12));
		
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		shuffleSet.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		shuffleSet.add(titel, c);
		
		c.insets = new Insets(5, 5, 10, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 1;
		shuffleSet.add(expl, c);
		
		shuffleSet.setBackground(null);
				
		return shuffleSet;
	}
	
	private JScrollPane listShuffler()
	{
		try
		{
			List<String> listNames = Controller.getInstance().getData().getLists();
			IData data = Controller.getInstance().getData();
			String[] labels = new String[]{"Liste", "Priorität", "Spielwahrscheinlichkeit"};
			Object[][] lists = new Object[listNames.size()][3];
			
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
			prListener.calcPercent();
			
			return scroll;
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JScrollPane();
	}
	
	class SpinnerListener implements ChangeListener
	{
		private final String name;
		public SpinnerListener(String name)
		{
			this.name = name;
		}
		@Override
		public void stateChanged(ChangeEvent ce)
		{
			if(ce.getSource() instanceof JSpinner)
			{
				try
				{
					if(name.equalsIgnoreCase("hauptliste"))
					{
						Controller.getInstance().getData().writeSetting("MasterListPriority", ((JSpinner)ce.getSource()).getValue().toString());
						return;
					}
					System.out.println(name + "   " + (Integer)((JSpinner)ce.getSource()).getValue());
					Controller.getInstance().getData().setListPriority(name, (Integer)((JSpinner)ce.getSource()).getValue());
				}
				catch (ListException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*class TableListener implements ComponentListener
	{
		@Override
		public void componentHidden(ComponentEvent e){}

		@Override
		public void componentMoved(ComponentEvent e){}

		@Override
		public void componentResized(ComponentEvent e)
		{
			if(e.getSource() instanceof JTable)
			{
			}
		}

		@Override
		public void componentShown(ComponentEvent e){}
		
	}*/
	
	class PriorityListener extends ListAdapter implements SettingListener
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

		public void listPriorityChanged(String listName, int newPriority)
		{
			namedSp.get(listName).setValue(newPriority);
			calcPercent();
			table.repaint();
		}

		@Override
		public void settingChanged(String name, String value)
		{
			if(namedSp.get(name) != null)
			{
				namedSp.get(name).setValue(Integer.parseInt(value));
				calcPercent();
				table.repaint();
			}
		}
		
		public void calcPercent()
		{
			try
			{
				IData data = Controller.getInstance().getData();
				int sum = Integer.parseInt(data.readSetting("MasterListPriority"));
				int val = 0;
				for(int i = 0; i < namedSp.size(); i++)
				{
					String n = spinners.get(i).getName();
					if(n != null)
					{
						sum += data.getListPriority(n);
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
				double per = (Double.parseDouble(data.readSetting(("MasterListPriority"))) /sum) * 100;
				table.setValueAt(String.format("%2.2f", per) + "%", 0, 2);
			}
			catch (ListException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class ShuffleTableModel extends AbstractTableModel
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
				case 1:				return true;
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
		public void setValueAt(Object value, int row, int col)
		{
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
		}
	}
	
	class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
	{
		private static final long serialVersionUID = -7664852358641606118L;
		Map<Integer, JSpinner> spinners;
		
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
	
	class SpinnerRenderer implements TableCellRenderer
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
}
