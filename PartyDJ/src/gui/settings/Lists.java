package gui.settings;

import static common.Functions.formatTime;
import basics.Controller;
import common.Track;
import data.IData;
import data.ListAdapter;
import data.SettingListener;
import gui.PDJScrollList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lists.ListException;
import lists.TrackListModel;
import lists.data.DbTrack;

public class Lists extends JSplitPane implements Closeable
{
	private static final long serialVersionUID = 1L;
	private static final Controller controller = Controller.getInstance();
	private static final IData data = controller.getData();
	private JList<String> listLists;
	private JLabel lblTracksPercent;
	private JLabel lblDurationPercent;
	private JLabel lblPriorityPercent;
	private JSpinner spinnerPriority;
	private final ListsListModel listsListModel = new ListsListModel();
	private final Listener listener;
	private TrackListModel currentList;
	private double durationTotal;
	private double durationCurrent;

	public Lists()
	{
		setResizeWeight(0.9);
		
		final JPanel right = new JPanel();
		this.setRightComponent(right);
		
		final JButton btnAdd = new JButton("Hinzufügen");
		
		final JButton btnDelete = new JButton("Löschen");
		
		final JButton btnRename = new JButton("Umbenennen");
		
		final JLabel lblPriority = new JLabel("Priorität:");
		
		spinnerPriority = new JSpinner();
		
		final JLabel lblDuration = new JLabel("Dauer:");
		
		final JLabel lblTracks = new JLabel("Tracks:");
		
		lblTracksPercent = new JLabel("0 / 0 = 0%");
		
		lblDurationPercent = new JLabel("0:00 / 0:00 = 0%");
		
		lblPriorityPercent = new JLabel("0 / 0 = 0%");
		
		final JScrollPane scrollPaneLists = new JScrollPane();
		final GroupLayout gl_panel = new GroupLayout(right);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addComponent(btnRename, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblPriority)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerPriority, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblPriorityPercent))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblDuration)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblDurationPercent))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblTracks)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTracksPercent)
							.addGap(96))
						.addComponent(scrollPaneLists, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDelete)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRename)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPaneLists, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTracks)
						.addComponent(lblTracksPercent))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDuration)
						.addComponent(lblDurationPercent))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPriority)
						.addComponent(spinnerPriority, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPriorityPercent))
					.addContainerGap())
		);
		
		listLists = new JList<>(listsListModel);
		scrollPaneLists.setViewportView(listLists);
		right.setLayout(gl_panel);
		
		listLists.addListSelectionListener(new ListSelectionListener()
		{
			
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				final int index = listLists.getSelectedIndex();
				TrackListModel newList;
				if(index == 0)
					newList = controller.getListProvider().getMasterList();
				else
					try
					{
						newList = controller.getListProvider().getDbList(listLists.getSelectedValue());
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.UNIMPORTANT_ERROR, Lists.this, ex);
						newList = null;
					}
				
				if(newList != currentList)
				{
					Lists.this.setLeftComponent(new PDJScrollList(newList));
					currentList = newList;
					durationCurrent = -1;
					updateStatistics();
					updatePriority();
				}
			}
		});
		
		btnAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final String name = JOptionPane.showInputDialog(Lists.this, "Name der Liste: ", "Party DJ", JOptionPane.PLAIN_MESSAGE);
				if(name != null && name.length() > 0)
				{
					try
					{
						if(data.getLists().contains(name))
						{
							JOptionPane.showMessageDialog(Lists.this, "Liste bereits vorhanden.", "PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.NORMAL_ERROR, Lists.this, ex, "Liste einfügen fehlgeschlagen.");
						return;
					}
					
					try
					{
						data.addList(name);
						listLists.setSelectedValue(name, true);
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.NORMAL_ERROR, Lists.this, ex, "Liste einfügen fehlgeschlagen.");
					}
				}
			}
		});
		
		btnDelete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final String name = listLists.getSelectedValue();
				if(JOptionPane.showConfirmDialog(Lists.this, name + " wirklich löschen?", "Party DJ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					try
					{
						data.removeList(name);
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.NORMAL_ERROR, Lists.this, ex, "Liste löschen fehlgeschlagen.");
					}
			}
		});
		
		btnRename.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final String oldName = listLists.getSelectedValue();
				final String newName = (String) JOptionPane.showInputDialog(Lists.this, "Name der Liste: ", "Party DJ", JOptionPane.PLAIN_MESSAGE, null, null, oldName);
				if(newName != null && !newName.equals(oldName) && newName.length() > 0)
				{
					try
					{
						if(data.getLists().contains(newName))
						{
							JOptionPane.showMessageDialog(Lists.this, "Liste bereits vorhanden.", "PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.NORMAL_ERROR, Lists.this, ex, "Liste umbenennen fehlgeschlagen.");
						return;
					}

					try
					{
						data.renameList(oldName, newName);
						listLists.setSelectedValue(newName, true);
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.NORMAL_ERROR, Lists.this, ex, "Liste umbenennen fehlgeschlagen.");
					}
				}
			}
		});
		
		spinnerPriority.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				if(listLists.getSelectedIndex() == 0)
					data.writeSetting("MasterListPriority", spinnerPriority.getValue().toString());
				else
					try
					{
						data.setListPriority(listLists.getSelectedValue(), (Integer)spinnerPriority.getValue());
					}
					catch (final ListException ex)
					{
						controller.logError(Controller.UNIMPORTANT_ERROR, Lists.this, ex);
					}
			}
		});
		
		listLists.setSelectedIndex(0);
		listener = new Listener();
		
		new Thread()
		{
			@Override
			public void run()
			{
				double duration = 0;
				for(final Track track : controller.getListProvider().getMasterList().getList())
					duration += track.getDuration();
				durationTotal = duration;
				updateStatistics();
			}
		}.start();
	}

	private void updateStatistics()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final int tracksTotal = controller.getListProvider().getMasterList().getSize();
				final int tracks = currentList.getSize();
				lblTracksPercent.setText(tracks + " / " + tracksTotal + " = " + (tracksTotal > 0 ? tracks * 100 / tracksTotal : 0) + "%");
				
				if(durationCurrent == -1)
				{
					double duration = 0;
					for(final Track track : currentList.getList())
						duration += track.getDuration();
					durationCurrent = duration;
				}
				lblDurationPercent.setText(formatTime(durationCurrent) + " / " + formatTime(durationTotal) + " = " + (durationTotal > 0 ? (int)(durationCurrent * 100 / durationTotal) : 0) + "%");
			}
		});
	}
	
	private void updatePriority()
	{
		try
		{
			final int masterPrio = Integer.parseInt(data.readSetting("MasterListPriority", "1"));
			final int prio;
			if(listLists.getSelectedIndex() == 0)
				prio = masterPrio;
			else
				prio = data.getListPriority(listLists.getSelectedValue());
			spinnerPriority.setValue(prio);
			int totalPrio = masterPrio;
			for(final String list : data.getLists())
				totalPrio += data.getListPriority(list);
			
			lblPriorityPercent.setText(prio + " / " + totalPrio + " = " + (totalPrio > 0 ? prio * 100 / totalPrio : 0) + "%");
		}
		catch (final ListException e)
		{
			controller.logError(Controller.UNIMPORTANT_ERROR, Lists.this, e);
		}
	}

	@Override
	public void close()
	{
		listener.close();
	}
	

	private class ListsListModel extends AbstractListModel<String> implements Closeable
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public int getSize()
		{
			try
			{
				return data.getLists().size() + 1;
			}
			catch (final ListException e)
			{
				controller.logError(Controller.UNIMPORTANT_ERROR, Lists.this, e);
				return 1;
			}
		}

		@Override
		public String getElementAt(final int index)
		{
			if(index == 0)
				return "Hauptliste";
			
			try
			{
				return data.getLists().get(index - 1);
			}
			catch (final ListException e)
			{
				controller.logError(Controller.UNIMPORTANT_ERROR, Lists.this, e);
				return null;
			}
		}

		@Override
		public void close()
		{
			listener.close();
		}
	}
	
	private class Listener extends ListAdapter implements Closeable, SettingListener
	{
		Listener()
		{
			data.addListListener(this);
			data.addSettingListener(this);
		}

		@Override
		public void trackAdded(final DbTrack track, final boolean eventsFollowing)
		{
			durationTotal += track.getDuration();
			if(listLists.getSelectedIndex() == 0)
				durationCurrent = durationTotal;
			updateStatistics();
		}

		@Override
		public void trackChanged(final DbTrack newTrack, final common.Track oldTrack, final boolean eventsFollowing)
		{
			double duration = 0;
			for(final Track track : controller.getListProvider().getMasterList().getList())
				duration += track.getDuration();
			durationTotal = duration;
			if(listLists.getSelectedIndex() == 0)
				durationCurrent = durationTotal;
			updateStatistics();
		}

		@Override
		public void trackDeleted(final DbTrack track, final boolean eventsFollowing)
		{
			durationTotal -= track.getDuration();
			if(listLists.getSelectedIndex() == 0)
				durationCurrent = durationTotal;
			updateStatistics();
		}

		@Override
		public void listAdded(final String listName)
		{
			listLists.updateUI();
		}

		@Override
		public void listRemoved(final String listName)
		{
			listLists.updateUI();
		}

		@Override
		public void listRenamed(final String oldName, final String newName)
		{
			listLists.updateUI();
		}
		
		@Override
		public void trackInserted(final String listName, final int position, final DbTrack track, final boolean eventsFollowing)
		{
			if(listName.equals(listLists.getSelectedValue()))
			{
				durationCurrent += track.getDuration();
				updateStatistics();
			}
		}

		@Override
		public void trackRemoved(final String listName, final int position, final boolean eventsFollowing)
		{
			if(listName.equals(listLists.getSelectedValue()))
			{
				durationCurrent = -1;
				updateStatistics();
			}
		}

		@Override
		public void listPriorityChanged(final String listName, final int newPriority)
		{
			updatePriority();
		}

		@Override
		public void settingChanged(final String name, final String value)
		{
			if(name.equals("MasterListPriority"))
				updatePriority();
		}

		@Override
		public void close()
		{
			data.removeListListener(this);
			data.removeSettingListener(this);
		}
	}
}

