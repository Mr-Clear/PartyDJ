package network;

import gui.settings.Closeable;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import static javax.swing.event.TableModelEvent.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import data.IData;
import basics.Controller;

public class WinLircReceiverSettings extends javax.swing.JPanel implements WinLircReceiver.WinLircListener, Closeable
{
	protected static final long serialVersionUID = 8285774262584373492L;
	protected final Controller controller = Controller.getInstance();
	protected final IData data = controller.getData();
	protected JTable tablePressedKeys;
	protected final TablePressedKeysModel tablePressedKeysModel = new TablePressedKeysModel();
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
	protected WinLircReceiver receiver = WinLircReceiver.instance;

	public WinLircReceiverSettings()
	{
		super();
		initGUI();
		statusChanged(receiver.isRunning());
		receiver.addWinLircListener(this);
	}
	
	private void initGUI()
	{
		GroupLayout thisLayout = new GroupLayout((JComponent)this);
		this.setLayout(thisLayout);
		setPreferredSize(new Dimension(400, 300));
		{
			spTablePressedKeys = new JScrollPane();
			{
				tablePressedKeys = new JTable();
				spTablePressedKeys.setViewportView(tablePressedKeys);
				tablePressedKeys.setModel(tablePressedKeysModel);
				tablePressedKeys.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
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
			spTableKeys = new JScrollPane();
			{
				tableKeys = new JTable();
				spTableKeys.setViewportView(tableKeys);
				tableKeys.setModel(tableKeysModel);
			}
		}
		{
			txtHost = new JTextField();
			txtHost.setText(data.readSetting("WinLIRC-IP", "127.0.0.1"));
			txtHost.addKeyListener(new KeyAdapter(){
				@Override
				public void keyTyped(KeyEvent e)
				{
					data.writeSetting("WinLIRC-IP", txtHost.getText());
				}});
		}
		{
			lblStatus = new JLabel();
			lblStatus.setText("Status:");
		}
		{
			lblShowStatus = new JLabel();
			lblShowStatus.setText("Offline");
			lblShowStatus.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
		}
		{
			btnConnect = new JButton();
			btnConnect.setText("Verbinden");
			btnConnect.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e)
				{
					receiver.start();
				}});
		}
		{
			btnDisconnect = new JButton();
			btnDisconnect.setText("Trennen");
			btnDisconnect.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e)
				{
					receiver.stop();
				}});
		}
		{
			btnReset = new JButton();
			btnReset.setText("Neu Verbinden");
			btnReset.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e)
				{
					receiver.stop();
					receiver.start();
				}});
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
				    .addComponent(spTablePressedKeys, GroupLayout.Alignment.LEADING, 0, 352, Short.MAX_VALUE)
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				            .addComponent(btnConnect, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addComponent(lblShowStatus, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
				            .addComponent(lblStatus, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addComponent(btnDisconnect, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addComponent(btnReset, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				        .addComponent(lblKeys, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(spTableKeys, 0, 291, Short.MAX_VALUE)))
				.addContainerGap());
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(thisLayout.createSequentialGroup()
				        .addGroup(thisLayout.createParallelGroup()
				            .addComponent(lblStatus, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
				            .addComponent(lblHost, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
				        .addGap(18)
				        .addGroup(thisLayout.createParallelGroup()
				            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				                .addComponent(lblShowStatus, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				                .addComponent(btnConnect, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				                .addComponent(btnDisconnect, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				                .addComponent(btnReset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				                .addGap(0, 132, Short.MAX_VALUE))
				            .addComponent(txtHost, GroupLayout.Alignment.LEADING, 0, 431, Short.MAX_VALUE))
				        .addGap(6))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(lblKeys, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 393, Short.MAX_VALUE))
				    .addComponent(spTableKeys, GroupLayout.Alignment.LEADING, 0, 492, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(thisLayout.createParallelGroup()
				    .addComponent(spTablePressedKeys, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
				    .addComponent(lblLastKeys, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(17, 17));
	}

	@Override
	public void keyPressed(final String remote, final String key, final int repeat, long keyCode)
	{
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
	public void statusChanged(final boolean running)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				lblShowStatus.setText(running ? "Online" : "Offline");
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
		protected final List<TableModelListener> listeners = new ArrayList<TableModelListener>();
		
		@Override
		public void removeTableModelListener(TableModelListener l)
		{
			listeners.remove(l);
		}
		
		@Override
		public void addTableModelListener(TableModelListener l)
		{
			listeners.add(l);
		}
		
		protected void fireEvent(TableModelEvent event)
		{
			synchronized(listeners)
			{
				for(TableModelListener listener : listeners)
					listener.tableChanged(event);
			}
		}
	}
	
	protected class TablePressedKeysModel extends ErasersAbstractTableModel
	{
		protected final List<String>remotes = new ArrayList<String>();
		protected final List<String>keys = new ArrayList<String>();
		protected final List<Integer>repeats = new ArrayList<Integer>();
		
		public void addKey(String remote, String key, int repeat)
		{
			int size = keys.size() - 1;
			if(size >= 0 &&
					remote.equals(remotes.get(size)) &&
					key.equals(keys.get(size)) &&
					repeat > repeats.get(size))
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
		public Object getValueAt(int rowIndex, int columnIndex)
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
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			throw new UnsupportedOperationException("Tabelle unterstützt setValueAt nicht.");
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex)
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
		public String getColumnName(int columnIndex)
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
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}
	}
	
	protected class TableKeysModel extends ErasersAbstractTableModel
	{
		protected final Map<String, String> keys = new TreeMap<String, String>();
		
		public TableKeysModel()
		{
			String[] k = data.readSetting("WinLIRC_known_Keys", "").split(" ");
			if(k.length % 2 != 0)
				controller.logError(Controller.NORMAL_ERROR, this, null, "Setting WinLIRC_known_Keys muss eine gerade Anzahl Teilstrings haben.");
			for(int i = 0; i < k.length; i += 2)
			{
				keys.put(k[i] + " " + k[i + 1], null);
			}
		}
		
		public void addKey(String remote, String key)
		{
			String mapKey = remote + " " + key;
			if(!keys.containsKey(mapKey))
			{
				keys.put(mapKey, null);				
				fireEvent(new TableModelEvent(this, keys.size(), keys.size(), ALL_COLUMNS, INSERT));
				saveMapKeys();
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return String.class;
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex)
		{
			switch(columnIndex)
			{
			case 0:
				return "Fernsteuerung";
			case 1:
				return "Taste";
			case 2:
				return "Aktion";
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
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(columnIndex < 2)
				return ((String)keys.keySet().toArray()[rowIndex]).split(" ")[columnIndex];
			else if(columnIndex == 2)
				return ((String)keys.values().toArray()[rowIndex]);
			else
				throw new IllegalArgumentException("Tabelle hat nur 3 Spalten.");
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == 3;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			throw new UnsupportedOperationException("Tabelle unterstützt setValueAt nicht.");
		}
		
		protected void saveMapKeys()
		{
			StringBuilder sb = new StringBuilder();
			for(String mapKey : keys.keySet())
				sb.append(mapKey).append(' ');
			sb.setLength(sb.length() - 1);
			data.writeSetting("WinLIRC_known_Keys", sb.toString());
		}
	}
}
