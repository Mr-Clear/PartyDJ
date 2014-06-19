package gui.settings;

import basics.Controller;
import data.IData;
import data.ListAdapter;
import data.SettingListener;
import lists.ListException;
import java.awt.Component;
import java.awt.Font;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Shuffle extends JPanel implements Closeable
{
	private static final long serialVersionUID = 2304624209647591659L;
	final static IData data = Controller.getInstance().getData();
	PriorityListener prListener;
	
	public Shuffle() 
	{
		initComponents();
	}
	
	protected JScrollPane listTable()
	{
		try
		{
			final List<String> listNames = Controller.getInstance().getData().getLists();
			final String[] labels = new String[]{"Liste", "Priorität", "Spielwahrscheinlichkeit"};
			final Object[][] lists = new Object[listNames.size()][3];
			
			for(int i = 0; i < listNames.size(); i++)
			{
				lists[i][0] = listNames.get(i);
			}
			
			final JTable listTable = new JTable(new ShuffleTableModel(lists, labels));
			int skipped = 0;
			
			final Map<Integer, JSpinner> spinners = new HashMap<>(4);
			final Map<String, JSpinner> namedSp = new HashMap<>(4);
			prListener = new PriorityListener(spinners, namedSp, listTable);
			final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
			
			spinner.setValue(Integer.parseInt(data.readSetting("MasterListPriority", "1")));
			spinner.addChangeListener(new SpinnerListener("Hauptliste"));
			spinners.put(0, spinner);
			namedSp.put("MasterListPriority", spinner);
			
			listTable.getColumnModel().addColumnModelListener(new ColumnListener());
			final String[] width = data.readSetting("ColumnSize", "@407@91@138").split("@");
			for(int i = 0; i < 3; i++)
				listTable.getColumnModel().getColumn(i).setPreferredWidth(Integer.parseInt(width[i + 1]));
			
			listTable.setValueAt("Hauptliste", 0, 0);
			data.addListListener(prListener);
			data.addSettingListener(prListener);
			for(int i = 1; i <= listNames.size(); i++)
			{
				final String list = listNames.get(i - 1);
				
				if(list.equals(Controller.getLastPlayedName()))
				{
					skipped++;
				}
				else
				{
					final JSpinner sp = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
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
			final JScrollPane scroll = new JScrollPane(listTable);
			scroll.setVisible(true);
			prListener.calcListPercent();
			
			return scroll;
		}
		catch (final ListException e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e, "Fehler bei Zugriff auf Datenbank.");
		}
		
		return new JScrollPane();
	}
	
	protected static class SpinnerListener implements ChangeListener
	{
		private final String name;
		public SpinnerListener(final String name)
		{
			this.name = name;
		}
		@Override
		public void stateChanged(final ChangeEvent ce)
		{
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
				catch (final ListException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Zugriff auf Datenbank.");
				}
			}
		}
	}
	
	protected static class ColumnListener implements TableColumnModelListener
	{
		@Override
		public void columnAdded(final TableColumnModelEvent e) { /* not to implement */ }

		@Override
		public void columnMarginChanged(final ChangeEvent e)
		{
			if(e.getSource() instanceof DefaultTableColumnModel)
			{
				final StringBuilder sb = new StringBuilder();
				for(int i = 0; i < 3; i++)
				{
					final int size = ((DefaultTableColumnModel)e.getSource()).getColumn(i).getPreferredWidth();
					sb.append((char)64 + String.valueOf(size));
				}
				data.writeSetting("ColumnSize", sb.toString());
			}
		}

		@Override
		public void columnMoved(final TableColumnModelEvent e) { /* not to implement */ }

		@Override
		public void columnRemoved(final TableColumnModelEvent e) { /* not to implement */ }

		@Override
		public void columnSelectionChanged(final ListSelectionEvent e) { /* not to implement */ }
	}
	
	protected static class PriorityListener extends ListAdapter implements SettingListener
	{
		private final Map<String, JSpinner> namedSp;
		private final JTable table;
		private final Map<Integer, JSpinner> spinners;

		public PriorityListener(final Map<Integer, JSpinner> spinners, final Map<String, JSpinner> namedSp, final JTable table)
		{
			this.namedSp = namedSp;
			this.spinners = spinners;
			this.table = table;
		}

		@Override
		public void listPriorityChanged(final String listName, final int newPriority)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					namedSp.get(listName).setValue(newPriority);
					calcListPercent();
					table.repaint();
				}
			});
			
		}

		@Override
		public void settingChanged(final String name, final String value)
		{
			if(namedSp.get(name) != null)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override public void run()
					{
						namedSp.get(name).setValue(Integer.parseInt(value));
						calcListPercent();
						table.repaint();
					}
				});
			}
		}
		
		public void calcListPercent()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					try
					{
						int sum = Integer.parseInt(data.readSetting("MasterListPriority", "1"));
						int val = 0;
						for(int i = 0; i < namedSp.size(); i++)
						{
							final String n = spinners.get(i).getName();
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
							final String n = spinners.get(i).getName();
							if(n != null)
							{
								val = data.getListPriority(spinners.get(i).getName());
								final double percent = ((double)val / sum) * 100;
								table.setValueAt(String.format("%2.2f", (Double)percent) + "%", i, 2);
							}
						}
						final double per = (Double.parseDouble(data.readSetting("MasterListPriority", "1")) / sum) * 100;
						table.setValueAt(String.format("%2.2f", per) + "%", 0, 2);
					}
					catch (final ListException e)
					{
						Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Zugriff auf Datenbank.");
					}
				}
			});
		}
	}
	
	protected static class ShuffleTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 4845502871704418973L;
		Object[][] rowData;
		String[] columnNames;
		
		public ShuffleTableModel(final Object[][] rowData, final String[] columnNames)
		{
			this.rowData = rowData;
			this.columnNames = columnNames;
		}
		
		@Override
		public boolean isCellEditable(final int row, final int column)
		{
			switch(column)
			{
			case 0:
				return false;
				//TODO Wunschliste ohne Priorität mit check Box o.ä.
			case 1:
				if("Wunschliste".equals(rowData[row][0]))
					return false;
				return true;
			case 2:
				return false;
			default:
				return false;
			}
		}
		
		@Override
		public String getColumnName(final int column)
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
		public Object getValueAt(final int row, final int column)
		{
			return rowData[row][column];
		}
		
		@Override
		public void setValueAt(final Object value, final int row, final int col)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					rowData[row][col] = value;
					fireTableCellUpdated(row, col);
				}
			});
		}
	}
	
	protected static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
	{
		private static final long serialVersionUID = -7664852358641606118L;
		protected Map<Integer, JSpinner> spinners;
		
		public SpinnerEditor(final Map<Integer, JSpinner> spinners)
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
		public JSpinner getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) 
		{
            return spinners.get(row);
        }
	}
	
	protected static class SpinnerRenderer implements TableCellRenderer
	{
		Map<Integer, JSpinner> spinners;
		
		public SpinnerRenderer(final Map<Integer, JSpinner> spinners)
		{
			super();
			this.spinners = spinners;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
		{
			return spinners.get(row);
		}
	}

	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sam Meier
		label1 = new JLabel();
		label2 = new JLabel();
		final CellConstraints cc = new CellConstraints();

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

	@Override
	public void close()
	{
		data.removeListListener(prListener);
		data.removeSettingListener(prListener);
	}
}
