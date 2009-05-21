package gui.settings;

import gui.GlobalHotKeys;
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
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
import players.IPlayer;
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
		JLabel expl = new JLabel("<html>Hier können Sie einstellen, mit welchen Tasten Sie zusätzlich zu den Media-Tasten den PartyDJ steuern wollen." +
								 "<br>Im linken Textfeld befinden sich die lokalen, im rechten die globalen HotKeys.</html>");
		
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
		
		//--Eingeschoben
		c.gridx = 1;
		p.add(makeLabel("Lokal:"), c);
		c.gridx = 2;
		p.add(makeLabel("Global:"), c);
		
		c.gridx = 4;
		p.add(makeLabel("Lokal:"), c);
		c.gridx = 5;
		p.add(makeLabel("Global:"), c);
		
		int startX = 0;
		int startY = 1;
		
		c.gridx = 0 + startX;
		c.gridy = 0 + startY;
		p.add(makeLabel("Play / Pause:"), c);

		c.gridx = 2 + startX;
		final JTextField play = new JTextField("NONE          ");
		play.setName("play");
		play.setToolTipText("Globaler HotKey für PLAY");
		play.setBackground(Color.WHITE);
		play.setEditable(false);
		p.add(play, c);

		c.gridx = 1 + startX;
		final JTextField localPlay = new JTextField("NONE          ");
		localPlay.setName("play");
		play.setToolTipText("Lokaler HotKey für PLAY");
		localPlay.setBackground(Color.WHITE);
		localPlay.setEditable(false);
		p.add(localPlay, c);

		c.gridx = 3 + startX;
		p.add(makeLabel("Stop:"), c);

		c.gridx = 5 + startX;
		final JTextField stop = new JTextField("NONE          ");
		stop.setName("stop");
		play.setToolTipText("Globaler HotKey für STOP");
		stop.setBackground(Color.WHITE);
		stop.setEditable(false);
		p.add(stop, c);

		c.gridx = 4 + startX;
		final JTextField localStop = new JTextField("NONE          ");
		localStop.setName("stop");
		play.setToolTipText("Lokaler HotKey für STOP");
		localStop.setBackground(Color.WHITE);
		localStop.setEditable(false);
		p.add(localStop, c);

		c.gridx = 0 + startX;
		c.gridy = 1 + startY;
		p.add(makeLabel("Nächstes Lied:"), c);

		c.gridx = 2 + startX;
		final JTextField next = new JTextField("NONE          ");
		next.setName("next");
		play.setToolTipText("Globaler HotKey für NÄCHSTES LIED");
		next.setBackground(Color.WHITE);
		next.setEditable(false);
		p.add(next, c);

		c.gridx = 1 + startX;
		final JTextField localNext = new JTextField("NONE          ");
		localNext.setName("next");
		play.setToolTipText("Lokaler HotKey für NÄCHSTES LIED");
		localNext.setBackground(Color.WHITE);
		localNext.setEditable(false);
		p.add(localNext, c);

		c.gridx = 3 + startX;
		c.gridy = 1 + startY;
		p.add(makeLabel("Vorheriges Lied:"), c);

		c.gridx = 5 + startX;
		final JTextField prev = new JTextField("NONE          ");
		prev.setName("prev");
		play.setToolTipText("Globaler HotKey für VORHERIGES LIED");
		prev.setBackground(Color.WHITE);
		prev.setEditable(false);
		p.add(prev, c);

		c.gridx = 4 + startX;
		final JTextField localPrev = new JTextField("NONE          ");
		localPrev.setName("prev");
		play.setToolTipText("Lokaler HotKey für VORHERIGES LIED");
		localPrev.setBackground(Color.WHITE);
		localPrev.setEditable(false);
		p.add(localPrev, c);

		c.gridx = 0 + startX;
		c.gridy = 2 + startY;
		p.add(makeLabel("Lauter:"), c);
		
		c.gridx = 2 + startX;
		final JTextField volUp = new JTextField("NONE          ");
		volUp.setName("volup");
		volUp.setToolTipText("Globaler HotKey für LAUTER");
		volUp.setBackground(Color.WHITE);
		volUp.setEditable(false);
		p.add(volUp, c);

		c.gridx = 1 + startX;
		final JTextField localVolUp = new JTextField("NONE          ");
		localVolUp.setName("volup");
		localVolUp.setToolTipText("Lokaler HotKey für LAUTER");
		localVolUp.setBackground(Color.WHITE);
		localVolUp.setEditable(false);
		p.add(localVolUp, c);

		c.gridx = 3 + startX;
		c.gridy = 2 + startY;
		p.add(makeLabel("Leiser:"), c);

		c.gridx = 5 + startX;
		final JTextField volDown = new JTextField("NONE          ");
		volDown.setName("voldown");
		volDown.setToolTipText("Globaler HotKey für LEISER");
		volDown.setBackground(Color.WHITE);
		volDown.setEditable(false);
		p.add(volDown, c);

		c.gridx = 4 + startX;
		final JTextField localVolDown = new JTextField("NONE          ");
		localVolDown.setName("voldown");
		localVolDown.setToolTipText("Lokaler HotKey für LEISER");
		localVolDown.setBackground(Color.WHITE);
		localVolDown.setEditable(false);
		p.add(localVolDown, c);
		
		c.gridy = 3 + startY;
		c.gridx = 2 + startX;
		c.gridwidth = 2;
		JButton clear = new JButton("<html><p style=\"text-align:center\"><b>Alle globalen</font></b><br>HotKeys löschen!</p></html>");
		p.add(clear, c);
		
		c.gridx = 0 + startX;
		JButton clearLocal = new JButton("<html><p style=\"text-align:center\"><b>Alle lokalen</font></b><br>HotKeys löschen!</p></html>");
		p.add(clearLocal, c);

		KeyListener listener = new GlobalHotKeySetter(play, stop, volDown, volUp, next, prev);
		play.addKeyListener(listener);
		stop.addKeyListener(listener);
		volDown.addKeyListener(listener);
		volUp.addKeyListener(listener);
		next.addKeyListener(listener);
		prev.addKeyListener(listener);
		
		listener = new LocalHotKeySetter(localPlay, localStop, localVolDown, localVolUp, localNext, localPrev);
		localPlay.addKeyListener(listener);
		localStop.addKeyListener(listener);
		localVolDown.addKeyListener(listener);
		localVolUp.addKeyListener(listener);
		localNext.addKeyListener(listener);
		localPrev.addKeyListener(listener);
		
		
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
		
		clearLocal.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e)
								{
									KeyStrokeManager.getInstance().getInputMap().clear();
									localPlay.setText("");
									localStop.setText("");
									localVolDown.setText("");
									localVolUp.setText("");
									localNext.setText("");
									localPrev.setText("");
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
			if(!(e.getComponent() instanceof JTextField))
				return;
			
			JTextField actual = (JTextField) e.getComponent();
			{
				manager.enableHotKey(e.getModifiers(), e.getKeyCode());
				int id = (String.valueOf(e.getKeyCode()) + (char)0 + e.getModifiers()).hashCode();
				GlobalHotKeys.getInstance().setKeyAction(id, actual.getName());
				actual.setText(KeyEvent.getKeyText(e.getKeyCode()));
				actual.repaint();
			}
		}

		@Override
		public void keyReleased(KeyEvent e){}

		@Override
		public void keyTyped(KeyEvent e){}
	}
	
	protected class LocalHotKeySetter implements KeyListener
	{
		JTextField[] fields;
		KeyStrokeManager manager = KeyStrokeManager.getInstance();
		public LocalHotKeySetter(JTextField...txts)
		{
			fields = txts;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			InputMap iMap = manager.getInputMap();
			ActionMap aMap = manager.getActionMap();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
			if(!(e.getComponent() instanceof JTextField))
				return;
			
			final JTextField txtField = (JTextField) e.getComponent();
			
			if(iMap.get(keyStroke) != null)
			{
				for(JTextField field : fields)
				{
					if(iMap.get(keyStroke).toString().equalsIgnoreCase(field.getName()))
					{						
						field.setText("");
						field.repaint();
					}
				}
			}
			
			iMap.put(keyStroke, txtField.getName());
			aMap.put(txtField.getName(), new AbstractAction(){
				
									private static final long serialVersionUID = 8899350838466037814L;
									@Override
									public void actionPerformed(ActionEvent e)
									{
										IPlayer p = Controller.getInstance().getPlayer();
										String action = txtField.getName();
										if(action.equalsIgnoreCase("play"))
											p.fadeInOut();
										else if(action.equalsIgnoreCase("stop"))
											p.stop();
										else if(action.equalsIgnoreCase("volup"))
											p.setVolume(p.getVolume() + 10);
										else if(action.equalsIgnoreCase("voldown"))
											p.setVolume(p.getVolume() - 10);
										else if(action.equalsIgnoreCase("next"))
											p.playNext();
										else if(action.equalsIgnoreCase("prev"))
											p.playPrevious();
											
									}});
			txtField.setText(KeyEvent.getKeyText(e.getKeyCode()));
			txtField.repaint();
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
