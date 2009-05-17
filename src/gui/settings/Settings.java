package gui.settings;

import gui.KeyStrokeManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import data.IData;
import data.ListAdapter;
import data.SettingListener;
import lists.ListException;
import basics.Controller;

//TODO JavaDoc
//TODO Sinnvollen Namen für die Klasse finden.

/**
 * Einstellungen für die Listen.
 * 
 * @author Sam
 * 
 * @see SettingWindow
 */
public class Settings extends JPanel
{
	private static final long serialVersionUID = -6987892133947674516L;
	
	public Settings()
	{
		initGUI();
	}
	
	protected void initGUI()
	{
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();

		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 10, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		con.weightx = 0.0;
		con.weighty = 0.0;
		con.gridx = 0;
		con.ipady = 22;
		add(listExplanation(), con);
		
		con.gridy = 1;
		con.ipady = 100;
		add(listTable(), con);
		
		con.insets = new Insets(30, 10, 0, 0);
		con.gridy = 2;
		con.ipady = 0;
		con.ipadx = 35;
		add(shortCutExpl(), con);

		con.gridy = 3;
		con.ipadx = 35;
		con.anchor = GridBagConstraints.WEST;
		add(shortCuts(), con);
		
		this.setVisible(true);
	}
	
	protected JPanel listExplanation()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel listExpl = new JPanel(new GridBagLayout());
		
		JLabel titel = new JLabel("Shuffle");
		JLabel expl = new JLabel("Hier können Sie einstellen, wie groß die Wahrscheinlichkeit ist, dass ein Lied von einer bestimmten Liste gespielt wird.");
		
		titel.setFont(new Font("SansSerifs", Font.BOLD, 18));
		expl.setFont(new Font("SansSerifs", Font.ITALIC, 12));
		
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		listExpl.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		listExpl.add(titel, c);
		
		c.insets = new Insets(5, 5, 10, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 1;
		listExpl.add(expl, c);
		
		listExpl.setBackground(null);
				
		return listExpl;
	}
	
	protected JPanel shortCutExpl()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel shortCutExpl = new JPanel(new GridBagLayout());
		
		JLabel titel = new JLabel("ShortCuts");
		JLabel expl = new JLabel("Hier können Sie einstellen, mit welchen Tasten Sie zusätzlich zu den Media-Tasten den PartyDJ steuern wollen.");
		
		titel.setFont(new Font("SansSerifs", Font.BOLD, 18));
		expl.setFont(new Font("SansSerifs", Font.ITALIC, 12));
		
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		shortCutExpl.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		shortCutExpl.add(titel, c);
		
		c.insets = new Insets(5, 5, 10, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 1;
		shortCutExpl.add(expl, c);
		
		shortCutExpl.setBackground(null);
				
		return shortCutExpl;
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
	
	protected JPanel shortCuts()
	{
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		p.setLayout(new GridBagLayout());
		
		c.insets = new Insets(5, 5, 10, 10);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		p.add(makeLabel("Play / Pause:"), c);
		
		final JTextField play = new JTextField("Num 5");
		play.setBackground(Color.WHITE);
		play.setEditable(false);
		c.gridx = 1;
		p.add(play, c);
		
		c.gridy = 1;
		c.gridx = 0;
		p.add(makeLabel("Stop:"), c);
		
		final JTextField stop = new JTextField("Num 0");
		stop.setBackground(Color.WHITE);
		stop.setEditable(false);
		c.gridx = 1;
		p.add(stop, c);
		
		c.gridy = 0;
		c.gridx = 2;
		p.add(makeLabel("Nächstes Lied:"), c);
		
		final JTextField next = new JTextField("Num 6");
		next.setBackground(Color.WHITE);
		next.setEditable(false);
		c.gridx = 3;
		p.add(next, c);
		
		c.gridy = 1;
		c.gridx = 2;
		p.add(makeLabel("Vorheriges Lied:"), c);
		
		final JTextField prev = new JTextField("Num 4");
		prev.setBackground(Color.WHITE);
		prev.setEditable(false);
		c.gridx = 3;
		p.add(prev, c);
		
		c.gridy = 0;
		c.gridx = 4;
		p.add(makeLabel("Lauter:"), c);
		
		final JTextField volUp = new JTextField("Num 8");
		volUp.setBackground(Color.WHITE);
		volUp.setEditable(false);
		c.gridx = 5;
		p.add(volUp, c);
		
		c.gridy = 1;
		c.gridx = 4;
		p.add(makeLabel("Leiser:"), c);
		
		final JTextField volDown = new JTextField("Num 2");
		volDown.setBackground(Color.WHITE);
		volDown.setEditable(false);
		c.gridx = 5;
		p.add(volDown, c);
		
		JButton clear = new JButton("<html><p style=\"text-align:center\"><b>Alle globalen</font></b><br>HotKeys löschen!</p></html>");
		c.gridx = 0;
		c.gridy = 2;
		p.add(clear, c);

		GlobalHotKeySetter listener = new GlobalHotKeySetter(play, stop, volDown, volUp, next, prev);
		play.addKeyListener(listener);
		stop.addKeyListener(listener);
		volDown.addKeyListener(listener);
		volUp.addKeyListener(listener);
		next.addKeyListener(listener);
		prev.addKeyListener(listener);
		
		clear.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e)
								{
									for(Integer id : KeyStrokeManager.getInstance().getHotKeys())
										KeyStrokeManager.getInstance().disableHotKey(id);
									play.setText("");
									stop.setText("");
									volDown.setText("");
									volUp.setText("");
									next.setText("");
									prev.setText("");
								}});
		
		return p;
	}
	
	protected JLabel makeLabel(String text)
	{
		return new JLabel(text);
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

		public void listPriorityChanged(String listName, int newPriority)
		{
			namedSp.get(listName).setValue(newPriority);
			calcListPercent();
			table.repaint();
		}

		@Override
		public void settingChanged(String name, String value)
		{
			if(namedSp.get(name) != null)
			{
				namedSp.get(name).setValue(Integer.parseInt(value));
				calcListPercent();
				table.repaint();
			}
		}
		
		public void calcListPercent()
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
		public void setValueAt(Object value, int row, int col)
		{
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
		}
	}
	
	protected class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
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
	
	protected class GlobalHotKeySetter implements KeyListener
	{
		JTextField[] fields;
		KeyStrokeManager manager = KeyStrokeManager.getInstance();
		
		public GlobalHotKeySetter(JTextField...txts)
		{
			fields = txts;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			boolean register = false;
			System.out.println("KeyTyped:  " + e.getKeyCode());
			
			for(Integer id : manager.getHotKeys())
			{
				System.out.println("Registered:  " + id);
				
				if(id == (String.valueOf(e.getKeyCode()) + (char)0 + e.getModifiers()).hashCode())
				{
					System.out.println("Char:  " + e.getKeyChar() + "  Code:  " + e.getKeyCode() + "  Mod:  " + e.getModifiers());
					for(JTextField txtField : fields)
						if(String.valueOf(e.getKeyChar()).equals(txtField.getText()))
							txtField.setText("");
					if(e.getComponent() instanceof JTextField)
					{
						((JTextField)e.getComponent()).setText(id.toString());
					}
				}
				else
					register = true;
			}
			if(manager.getHotKeys().size() == 0 || register)
			{
				System.out.println("EnableHotKey");
				manager.enableHotKey(e.getModifiers(), e.getKeyCode());
				if(e.getComponent() instanceof JTextField)
				{
					((JTextField)e.getComponent()).setText("" + e.getKeyCode());
					((JTextField)e.getComponent()).repaint();
				}
				register = false;
			}
			System.out.println();
		}

		@Override
		public void keyReleased(KeyEvent e){}

		@Override
		public void keyTyped(KeyEvent e){}
	}
	
	protected class LokalHotKeySetter implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent e){}
		@Override
		public void keyTyped(KeyEvent e){}
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
}
