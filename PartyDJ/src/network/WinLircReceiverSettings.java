package network;

import static javax.swing.event.TableModelEvent.ALL_COLUMNS;
import static javax.swing.event.TableModelEvent.INSERT;
import static javax.swing.event.TableModelEvent.UPDATE;
import basics.Controller;
import basics.Scripter;
import data.IData;
import gui.settings.Closeable;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class WinLircReceiverSettings extends javax.swing.JPanel implements WinLircReceiver.WinLircListener, Closeable
{
	protected static final long serialVersionUID = 8285774262584373492L;
	protected final transient Controller controller = Controller.getInstance();
	protected final IData data = controller.getData();
	protected JTable tablePressedKeys;
	protected final TablePressedKeysModel tablePressedKeysModel = new TablePressedKeysModel();
	protected JCheckBox cbAutoConnect;
	protected JTable tableKeys;
	protected final TableKeysModel tableKeysModel = new TableKeysModel();
	protected JScrollPane spTableKeys;
	protected JLabel lblKeys;
	protected JScrollPane spTablePressedKeys;
	protected JTextField txtHost;
	protected JButton btnReset;
	protected JButton btnDisconnect;
	protected JButton btnConnect;
	protected JLabel lblShowStatus;
	protected JLabel lblStatus;
	protected JLabel lblHost;
	protected JLabel lblLastKeys;
	
	protected final WinLircReceiver receiver = WinLircReceiver.instance;
	protected final Map<String, WinLircReceiverKeyAction> keyActions = receiver.keyActions;

	public WinLircReceiverSettings()
	{
		super();
		initGUI();
		statusChanged(receiver.isRunning(), receiver.isRunning() ? "Unbekannt" : "Offline");
		receiver.addWinLircListener(this);
	}
	
	private void initGUI()
	{
		final GroupLayout thisLayout = new GroupLayout(this);
		this.setLayout(thisLayout);
		setPreferredSize(new Dimension(400, 300));
		{
			spTablePressedKeys = new JScrollPane();
			{
				tablePressedKeys = new JTable();
				spTablePressedKeys.setViewportView(tablePressedKeys);
				tablePressedKeys.setModel(tablePressedKeysModel);
				tablePressedKeys.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				tablePressedKeys.getColumnModel().getColumn(2).setMaxWidth(25);
			}
		}
		{
			lblLastKeys = new JLabel();
			lblLastKeys.setText("Zuletzt gedrückt:");
		}
		{
			lblHost = new JLabel();
			lblHost.setText("Host:");
		}
		{
			cbAutoConnect = new JCheckBox();
			cbAutoConnect.setText("Automatisch verbinden");
			cbAutoConnect.setSelected(Boolean.parseBoolean(data.readSetting("WinLIRC-Autorun", "false")));
			cbAutoConnect.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					data.writeSetting("WinLIRC-Autorun", Boolean.toString(cbAutoConnect.getModel().isSelected()));
				}
			});
		}
		{
			spTableKeys = new JScrollPane();
			{
				tableKeys = new JTable();
				spTableKeys.setViewportView(tableKeys);
				tableKeys.setModel(tableKeysModel);
				tableKeys.getColumnModel().getColumn(3).setMaxWidth(50);
				
				final JComboBox<String> comboBox = new JComboBox<>();
				comboBox.setEditable(true);
				comboBox.addItem("<Keine>");
				controller.getScripter();
				for(final String command : Scripter.getAvailableCommands())
					comboBox.addItem(command);
				tableKeys.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
			}
		}
		{
			txtHost = new JTextField();
			txtHost.setText(data.readSetting("WinLIRC-IP", "127.0.0.1"));
			txtHost.addKeyListener(new KeyAdapter()
			{
				//TODO Nachdem der Key eingetragen wurde.
				@Override public void keyTyped(final KeyEvent e)
				{
					data.writeSetting("WinLIRC-IP", txtHost.getText());
				}
			});
		}
		{
			lblStatus = new JLabel();
			lblStatus.setText("Status:");
		}
		{
			lblShowStatus = new JLabel();
			lblShowStatus.setText("Offline");
			lblShowStatus.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
		}
		{
			btnConnect = new JButton();
			btnConnect.setText("Verbinden");
			btnConnect.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					receiver.start();
				}
			});
		}
		{
			btnDisconnect = new JButton();
			btnDisconnect.setText("Trennen");
			btnDisconnect.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					receiver.stop();
				}
			});
		}
		{
			btnReset = new JButton();
			btnReset.setText("Neu Verbinden");
			btnReset.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					receiver.stop();
					final long start = System.currentTimeMillis(); 
					try
					{
						while(receiver.isRunning() && System.currentTimeMillis() - start < 1000)
							Thread.sleep(10);
					}
					catch (final InterruptedException ignored) { /* ignore */ }
					receiver.start();
				}
			});
		}
		{
			lblKeys = new JLabel();
			lblKeys.setText("Verfügbare Tasten:");
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(txtHost, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblLastKeys, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblHost, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGroup(thisLayout.createParallelGroup()
			    .addComponent(spTablePressedKeys, GroupLayout.Alignment.LEADING, 0, 254, Short.MAX_VALUE)
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            .addComponent(btnConnect, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			            .addComponent(lblShowStatus, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
			            .addComponent(lblStatus, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			            .addComponent(btnDisconnect, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			            .addComponent(btnReset, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            .addComponent(cbAutoConnect, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			            .addComponent(lblKeys, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(spTableKeys, 0, 199, Short.MAX_VALUE)))
			.addContainerGap());
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(lblKeys, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 492, Short.MAX_VALUE))
			    .addGroup(thisLayout.createSequentialGroup()
			        .addGroup(thisLayout.createParallelGroup()
			            .addComponent(lblStatus, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
			            .addComponent(lblHost, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
			        .addGap(18)
			        .addGroup(thisLayout.createParallelGroup()
			            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			                .addComponent(lblShowStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addComponent(btnConnect, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addGroup(thisLayout.createParallelGroup()
			                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			                        .addComponent(btnDisconnect, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                        .addComponent(btnReset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			                        .addGap(0, 6, Short.MAX_VALUE))
			                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			                        .addGap(20)
			                        .addComponent(cbAutoConnect, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			                        .addGap(0, 0, Short.MAX_VALUE)))
			                .addGap(223))
			            .addComponent(txtHost, GroupLayout.Alignment.LEADING, 0, 530, Short.MAX_VALUE))
			        .addGap(6))
			    .addComponent(spTableKeys, GroupLayout.Alignment.LEADING, 0, 591, Short.MAX_VALUE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup()
			    .addComponent(spTablePressedKeys, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblLastKeys, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)));
	}

	@Override
	public void keyPressed(final String remote, final String key, final int repeat, final long keyCode)
	{
		statusChanged(true, "Online");
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				tablePressedKeysModel.addKey(remote, key, repeat);
				tableKeysModel.addKey(remote, key);
			}
		});
	}

	@Override
	public void statusChanged(final boolean running, final String message)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				lblShowStatus.setText(message);
				btnConnect.setEnabled(!running);
				btnDisconnect.setEnabled(running);
				btnReset.setEnabled(running);
			}
		});
	}

	@Override
	public void close()
	{
		receiver.removeWinLircListener(this);
	}
	
	protected abstract class ErasersAbstractTableModel implements TableModel
	{
		protected final List<TableModelListener> listeners = new ArrayList<>();
		
		@Override
		public void removeTableModelListener(final TableModelListener l)
		{
			listeners.remove(l);
		}
		
		@Override
		public void addTableModelListener(final TableModelListener l)
		{
			listeners.add(l);
		}
		
		protected void fireEvent(final TableModelEvent event)
		{
			synchronized(listeners)
			{
				for(final TableModelListener listener : listeners)
					listener.tableChanged(event);
			}
		}
	}
	
	protected class TablePressedKeysModel extends ErasersAbstractTableModel
	{
		protected final List<String>remotes = new ArrayList<>();
		protected final List<String>keys = new ArrayList<>();
		protected final List<Integer>repeats = new ArrayList<>();
		
		public void addKey(final String remote, final String key, final int repeat)
		{
			final int size = keys.size() - 1;
			if(size >= 0
				&& remote.equals(remotes.get(size))
				&& key.equals(keys.get(size))
				&& repeat > repeats.get(size))
			{
				repeats.set(size, repeats.get(size) + 1);
				fireEvent(new TableModelEvent(this, size, size, ALL_COLUMNS, UPDATE));
			}
			else
			{
				remotes.add(remote);
				keys.add(key);
				repeats.add(repeat);
				fireEvent(new TableModelEvent(this, size + 1, size + 1, ALL_COLUMNS, INSERT));
				tablePressedKeys.scrollRectToVisible(tablePressedKeys.getCellRect(size + 1, 0, true));
			}
		}
		
		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			switch(columnIndex)
			{
			case 0:
				return remotes.get(rowIndex);
			case 1:
				return keys.get(rowIndex);
			case 2:
				return repeats.get(rowIndex);
			default:
				throw new IllegalArgumentException("Tabelle hat bur 3 Spalten.");
			}
		}
		
		@Override
		public void setValueAt(final Object value, final int rowIndex, final int columnIndex)
		{
			throw new UnsupportedOperationException("Tabelle unterstützt setValueAt nicht.");
		}
		
		@Override
		public Class<?> getColumnClass(final int columnIndex)
		{
			switch(columnIndex)
			{
			case 0:
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			default:
				throw new IllegalArgumentException("Tabelle hat nur 3 Spalten.");
			}
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public String getColumnName(final int columnIndex)
		{
			switch(columnIndex)
			{
			case 0:
				return "Fernsteuerung";
			case 1:
				return "Taste";
			case 2:
				return "#";
			default:
				throw new IllegalArgumentException("Tabelle hat nur 3 Spalten.");
			}
		}

		@Override
		public int getRowCount()
		{
			return keys.size();
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex)
		{
			return false;
		}
	}
	
	protected class TableKeysModel extends ErasersAbstractTableModel
	{
		protected final Map<String, String> keys = new TreeMap<>();
		
		public TableKeysModel()
		{
			final String[] k = data.readSetting("WinLIRC_known_Keys", "").split(" ");
			if(k.length % 2 == 0)
				for(int i = 0; i < k.length; i += 2)
					keys.put(k[i] + " " + k[i + 1], null);
		}
		
		public void addKey(final String remote, final String key)
		{
			final String mapKey = remote + " " + key;
			if(!keys.containsKey(mapKey))
			{
				keys.put(mapKey, null);				
				fireEvent(new TableModelEvent(this, keys.size(), keys.size(), ALL_COLUMNS, INSERT));
				saveMapKeys();
			}
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex)
		{
			if(columnIndex < 3)
				return String.class;
			else if(columnIndex == 3)
				return Boolean.class;
			else
				throw new IllegalArgumentException("Tabelle hat nur 3 Spalten.");
		}

		@Override
		public int getColumnCount()
		{
			return 4;
		}

		@Override
		public String getColumnName(final int columnIndex)
		{
			switch(columnIndex)
			{
			case 0:
				return "Fernsteuerung";
			case 1:
				return "Taste";
			case 2:
				return "Aktion";
			case 3:
				return "Einmal";
			default:
				throw new IllegalArgumentException("Tabelle hat nur 3 Spalten.");
			}
		}

		@Override
		public int getRowCount()
		{
			return keys.size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			if(columnIndex < 2)
				return ((String)keys.keySet().toArray()[rowIndex]).split(" ")[columnIndex];
			
			final String mapKey = getValueAt(rowIndex, 0) + " " + getValueAt(rowIndex, 1);
			if(columnIndex == 2)
			{
				return keyActions.containsKey(mapKey) ? keyActions.get(mapKey).command : null;
			}
			else if(columnIndex == 3)
				return keyActions.containsKey(mapKey) ? !keyActions.get(mapKey).repeat : false;
			else
				throw new IllegalArgumentException("Tabelle hat nur 4 Spalten.");
		}
		
		@Override
		public void setValueAt(final Object value, final int rowIndex, final int columnIndex)
		{
			if(columnIndex != 2 && columnIndex != 3)
				throw new IllegalArgumentException("Nur Spalten 3 und 4 sind editierbar.");

			final String mapKey = getValueAt(rowIndex, 0) + " " + getValueAt(rowIndex, 1);
			WinLircReceiverKeyAction action = keyActions.get(mapKey);
			if(action == null)
				action = new WinLircReceiverKeyAction();

			if(columnIndex == 2)
			{
				if(!(value instanceof String))
					throw new IllegalArgumentException("Spalte 2 nimmt nur Werte vom Typ String auf.");
				
				action.command = (String)value;
				if(action.command != null)
				{
					action.command = action.command.trim().replace(' ', '_');
					if(action.command.length() == 0 || action.command.equalsIgnoreCase("<Keine>")) action.command = null;
				}
				if(action.command == null)
					keyActions.remove(mapKey);
				else
					keyActions.put(mapKey, action);
				
				fireEvent(new TableModelEvent(this, rowIndex, rowIndex, 3, UPDATE));
			}
			else //(columnIndex == 3)
			{
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("Spalte 3 nimmt nur Werte vom Typ Boolean auf.");
				
				action.repeat = !(Boolean)value;
				if(action.command != null)
					keyActions.put(mapKey, action);
			}
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex)
		{
			return columnIndex >= 2;
		}
		
		protected void saveMapKeys()
		{
			final StringBuilder sb = new StringBuilder();
			for(final String mapKey : keys.keySet())
				sb.append(mapKey).append(' ');
			sb.setLength(sb.length() - 1);
			data.writeSetting("WinLIRC_known_Keys", sb.toString());
		}
	}
}
