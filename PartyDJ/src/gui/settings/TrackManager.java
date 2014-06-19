package gui.settings;

import basics.Controller;
import common.Track;
import data.IData;
import data.ListAdapter;
import data.SortOrder;
import lists.ListException;
import lists.data.DbTrack;
import lists.data.SearchListModel;
import gui.EditTrackWindow;
import gui.PDJList;
import gui.PDJScrollList;
import gui.StatusDialog;
import gui.settings.tools.CheckTrackProblems;
import gui.settings.tools.ReadDuration;
import gui.settings.tools.RemoveMP3s;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TrackManager extends javax.swing.JPanel implements Closeable
{
	private static final long serialVersionUID = 5801679410698498741L;
	private PDJScrollList scrollList;
	private JButton btnImportFile;
	private JPanel jPanel1;
	private JLabel lblStatus;
	private JTextField txtSearch;
	private JButton btnCheckTrack;
	private JComboBox<String> cmbSort;
	private JLabel lblSearch;
	private JComboBox<String> cmbList;
	private JLabel lblList;
	private JLabel lblSort;
	private JButton btnReadDuration;
	private JButton btnEditTrack;
	private JButton btnRemoveTracks;
	private JButton btnImportFolder;
	
	private final transient Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final JFrame parent;
	private final PDJList list;
	private SearchListModel lm;
	private final StatusListener statusListener;
	private transient ActionListener searchConditionsListener;
	
	public TrackManager(final JFrame parent)
	{
		super();
		initGUI();
		this.parent = parent;
		list = scrollList.getList();
		list.setScrollToPlayedEnabled(false);
		statusListener = new StatusListener();
		scrollList.getList().addListSelectionListener(statusListener);
		data.addListListener(statusListener);
	}
	
	private void initGUI()
	{
		final GroupLayout thisLayout = new GroupLayout(this);
		this.setLayout(thisLayout);
		setPreferredSize(new Dimension(600, 400));
		searchConditionsListener = new ListParameterActionListener();
		{
			try
			{
				lm = new SearchListModel(null, SortOrder.DEFAULT, null);
			}
			catch (final ListException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Konnte keine Suchliste erstellen.");
			}
			scrollList = new PDJScrollList(lm);
		}
		{
			lblSort = new JLabel();
			lblSort.setText("Sortierung:");
		}
		{
			final ComboBoxModel<String> cmbSortModel = new DefaultComboBoxModel<>(SortOrder.getStringArray());
			cmbSort = new JComboBox<>();
			cmbSort.setModel(cmbSortModel);
			cmbSort.setSelectedIndex(1);
			cmbSort.addActionListener(searchConditionsListener);
		}
		{
			lblList = new JLabel();
			lblList.setText("Liste:");
		}
		{
			final ComboBoxModel<String> cmbListModel = new DefaultComboBoxModel<>();
			cmbList = new JComboBox<>();
			cmbList.setModel(cmbListModel);
			
			List<String> listBoxContent;
			try
			{
				listBoxContent = data.getLists();
			}
			catch (final ListException e)
			{
				listBoxContent = new ArrayList<>();
				controller.logError(Controller.NORMAL_ERROR, this, e, "Suchen der Listen fehlgeschlagen.");
			}
			listBoxContent.add(0, "Hauptliste");
			String[] listBoxArray = new String[listBoxContent.size()];
			listBoxContent.toArray(listBoxArray);
			cmbList.setModel(new DefaultComboBoxModel<>(listBoxArray));
			cmbList.addActionListener(searchConditionsListener);
		}
		{
			lblSearch = new JLabel();
			lblSearch.setText("Suche:");
		}
		{
			txtSearch = new JTextField();
			txtSearch.addActionListener(searchConditionsListener);
		}
		{
			lblStatus = new JLabel();
			lblStatus.setText("Status...");
		}
		{
			jPanel1 = new JPanel();
			final GridBagLayout jPanel1Layout = new GridBagLayout();
			jPanel1Layout.rowWeights = new double[] {0.0, 0.0};
			jPanel1Layout.rowHeights = new int[] {30, 30};
			jPanel1Layout.columnWeights = new double[] {0.0, 0.0, 0.0};
			jPanel1Layout.columnWidths = new int[] {170, 170, 170};
			jPanel1.setLayout(jPanel1Layout);
			{
				btnImportFolder = new JButton();
				jPanel1.add(btnImportFolder, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnImportFolder.setText("Verzeichnis einlesen...");
				btnImportFolder.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent arg0)
					{
						final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
						chooser.setDialogType(JFileChooser.OPEN_DIALOG);
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
										        
						if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
							return;
						
						final File folder = chooser.getSelectedFile();
						final String folderPath = folder.getPath();
						
						new StatusDialog("Lese Verzeichnisse", parent, new gui.settings.tools.ReadFolder(folderPath, true));
					}
				});
			}
			{
				btnReadDuration = new JButton();
				jPanel1.add(btnReadDuration, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnReadDuration.setText("Dauer einlesen");
				btnReadDuration.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						List<Track> toRead;
						List<Track> selectedValues = list.getSelectedValuesList();
						if(selectedValues == null || selectedValues.size() == 0)
							toRead = list.getListModel().getValues();
						else
							toRead = list.getSelectedValuesList();
						
						if(toRead.size() > 2)
						{
							new StatusDialog("Dauer einlesen", parent, new ReadDuration(toRead));
						}
						else
							new ReadDuration(toRead).runFunction(null);
					}
				});
			}
			{
				btnEditTrack = new JButton();
				jPanel1.add(btnEditTrack, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnEditTrack.setText("Track bearbeiten...");
				btnEditTrack.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						new EditTrackWindow(list.getSelectedValue());
					}
				});
			}
			{
				btnRemoveTracks = new JButton();
				jPanel1.add(btnRemoveTracks, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnRemoveTracks.setText("Tracks entfernen");
				btnRemoveTracks.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent arg0)
					{
						synchronized(list)
						{
							final int[] selected = list.getSelectedIndices();
							if(selected.length == 0)
								return;

							new StatusDialog("Tracks entfernen", parent, new RemoveMP3s(list));	
						}
					}
				});
			}
			{
				btnImportFile = new JButton();
				jPanel1.add(btnImportFile, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnImportFile.setText("Datei einlesen...");
				btnImportFile.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent arg0)
					{
						final JFileChooser chooser = new JFileChooser("Datei wählen");
						chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setCurrentDirectory(new File(data.readSetting("FileDirectory", common.Functions.getFolder())));
				        				        
				        if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
				        	return;
				        
				        final File file = chooser.getSelectedFile();
				        final String filePath = file.getPath();
				        
						data.writeSetting("FileDirectory", file.getParent());
				        
						// TODO Andere Formate.
				        if(filePath.toLowerCase().endsWith(".m3u"))
				        {
							data.writeSetting("PlayListDirectory", file.getParent());
				        	new StatusDialog("Lese M3U", parent, new gui.settings.tools.AddM3U(filePath));
				        }
				        else
					    {
				        	try
							{
								data.addTrack(new Track(filePath, false), false);
							}
							catch(ListException e)
							{
								controller.logError(Controller.NORMAL_ERROR, this, e, "Track einfügen fehlgeschlagen.");
							}
					    }
					}
				});
			}
			{
				btnCheckTrack = new JButton();
				jPanel1.add(btnCheckTrack, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
				btnCheckTrack.setText("Tracks prüfen");
				btnCheckTrack.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent evt)
					{
						List<Track> toRead;
						List<Track> selectedValues = list.getSelectedValuesList();
						if(selectedValues == null || selectedValues.size() == 0)
							toRead = list.getListModel().getValues();
						else
							toRead = selectedValues;
						
						if(toRead.size() > 2)
						{
							new StatusDialog("Tracks überprüfen", parent, new CheckTrackProblems(toRead));
						}
						else
							new CheckTrackProblems(toRead).runFunction(null);
					}
				});
			}
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
			.addGap(19)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(cmbSort, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblSort, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblList, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(cmbList, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(lblSearch, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(txtSearch, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(scrollList, 0, 239, Short.MAX_VALUE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGap(8));
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(lblSort, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(cmbSort, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(lblList, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(7)
			        .addComponent(cmbList, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(txtSearch, 0, 156, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 510, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 66, Short.MAX_VALUE))
			    .addComponent(scrollList, GroupLayout.Alignment.LEADING, 0, 576, Short.MAX_VALUE)
			    .addComponent(lblStatus, GroupLayout.Alignment.LEADING, 0, 576, Short.MAX_VALUE))
			.addContainerGap());
	}
	
	@Override
	public void close()
	{
		scrollList.getList().removeListSelectionListener(statusListener);
		data.removeListListener(statusListener);
	}

	private class ListParameterActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			String searchTextString = txtSearch.getText();
			if(searchTextString.length() == 0)
				searchTextString = null;
			String selectedList;
			if(cmbList.getSelectedIndex() == 0)
				selectedList = null;
			else
				selectedList = (String)cmbList.getSelectedItem();
			try
			{
				lm.search(searchTextString, SortOrder.arrayIndexToSortOrder(cmbSort.getSelectedIndex()), selectedList);
				list.validate();
			}
			catch (final ListException e1)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e1, "Fehler beim durchsuchen.");
			}
			statusListener.refresh(false);
		}
	}
	
	protected class StatusListener extends ListAdapter implements ListSelectionListener
	{
		protected int[] count = new int[3];
		protected double[] duration = new double[3];
		protected long[] size = new long[3];
		
		protected long lastUpdate = 0;
		protected final Timer updateTimer = new Timer(1000, new ActionListener()
		{	
			@Override public void actionPerformed(ActionEvent e)
			{
				update();
			}
		});
		
		StatusListener()
		{
			readValues(controller.getListProvider().getMasterList().getList(), 0);
			refresh(false);
		}
		
		protected void update(final boolean wait)
		{
			if(!wait)
			{
				updateTimer.stop();
				if(SwingUtilities.isEventDispatchThread())
					update();
				else
				{
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							@Override public void run()
							{
								update();
							}
						});
					}
					catch(InterruptedException e)
					{
						controller.logError(Controller.UNIMPORTANT_ERROR, this, e, "GUI-Update fehlgeschlagen.");
					}
					catch(InvocationTargetException e)
					{
						controller.logError(Controller.UNIMPORTANT_ERROR, this, e, "GUI-Update fehlgeschlagen.");
					}
				}
			}
			else
			{
				updateTimer.restart();
			}
		}
		
		/**Sollte immer im EventDispatchThread laufen.*/
		protected void update()
		{
			final StringBuilder sb = new StringBuilder();
			final String[] titles = new String[]{"Hauptliste", "Angezeiget Liste", "Gewählte Tracks"};
			for(int i = 0; i < 3; i++)
			{
				sb.append(titles[i] + ": " + count[i] + ", " + common.Functions.formatTime(duration[i]) + ", " + common.Functions.formatSize(size[i]));
				if(i < 2)
					sb.append("    ");
			}
			
			lblStatus.setText(sb.toString());
		}
		
		public void refresh(final boolean wait)
		{
			readValues(lm.getList(), 1);
			readValues(scrollList.getList().getSelectedValuesList(), 2);
			update(wait);
		}
		
		protected void readValues(final List<Track> listToCount, final int index)
		{
			count[index] = listToCount.size();
			duration[index] = 0;
			size[index] = 0;
			synchronized(listToCount)
			{
				for(final Track track : listToCount)
				{
					duration[index] += track.getDuration();
					size[index] += track.getSize();
				}
			}
		}		
		protected void readValues(final Track[] toCount, final int index)
		{
			count[index] = toCount.length;
			duration[index] = 0;
			size[index] = 0;
			for(final Track track : toCount)
			{
				duration[index] += track.getDuration();
				size[index] += track.getSize();
			}
		}
		
		@Override
		public void valueChanged(final ListSelectionEvent e)
		{
			readValues(scrollList.getList().getSelectedValuesList(), 2);
			update(e.getValueIsAdjusting());
		}

		@Override
		public void trackAdded(final DbTrack track, boolean eventsFollowing)
		{
			if(track != null)
			{
				duration[0] += track.getDuration();
				size[0] += track.getSize();
			}
			update(eventsFollowing);
		}

		@Override
		public void trackChanged(final DbTrack newTrack, final Track oldTrack, boolean eventsFollowing)
		{
			readValues(controller.getListProvider().getMasterList().getList(), 0);
			refresh(eventsFollowing);
		}

		@Override
		public void trackDeleted(final DbTrack track, boolean eventsFollowing)
		{
			if(track != null)
			{
				duration[0] -= track.getDuration();
				size[0] -= track.getSize();
			}
			update(eventsFollowing);
		}
	}
}
