/*
 * Created by JFormDesigner on Wed Jun 03 15:32:01 CEST 2009
 */

package de.klierlinge.partydj.gui.settings;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.ListAdapter;
import de.klierlinge.partydj.data.SortOrder;
import de.klierlinge.partydj.gui.EditTrackWindow;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.gui.PDJScrollList;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.settings.tools.ReadDuration;
import de.klierlinge.partydj.gui.settings.tools.RemoveMP3s;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbMasterListModel;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.lists.data.SearchListModel;

/**
 * @author Sam Meier
 */
public class MainList extends JPanel 
{
	private static final long serialVersionUID = 6101715371957303072L;
	private static final Logger log = LoggerFactory.getLogger(MainList.class);
	private PDJList list;
	private final transient Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final JFrame parent;
	private SearchListModel lm;
	protected int mainPlayTime;
	protected int selectedPlayTime;
	
	public MainList(final JFrame parent) 
	{
		this.parent = parent;
		initComponents();
		initFuntions();
	}

	public void initFuntions()
	{
		addFolder.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								        
				if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
					return;
				
				final File folder = chooser.getSelectedFile();
				final String folderPath = folder.getPath();
				
				new StatusDialog("Lese Verzeichnisse", parent, new de.klierlinge.partydj.gui.settings.tools.ReadFolder(folderPath, true));
			}
		});

		addFile.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("Datei wählen");
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setCurrentDirectory(new File(data.readSetting("FileDirectory", klierlinge.utils.Functions.getFolder())));
		        				        
		        if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
		        	return;
		        
		        final File file = chooser.getSelectedFile();
		        final String filePath = file.getPath();
		        
				data.writeSetting("FileDirectory", file.getParent());
		        
		        if(filePath.toLowerCase().endsWith(".m3u"))
		        {
					data.writeSetting("PlayListDirectory", file.getParent());
		        	new StatusDialog("Lese M3U", parent, new de.klierlinge.partydj.gui.settings.tools.AddM3U(filePath));
		        }
		        else
			    {
		        	try
					{
						data.addTrack(new Track(filePath, false), false);
					}
					catch(final ListException e)
					{
						log.error("Track einfügen fehlgeschlagen.", e);
					}
			    }
			}
		});
		
		removeFile.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent arg0)
			{
				synchronized(list)
				{
					final int[] selected = list.getSelectedIndices();
					if(selected.length == 0)
						return;

					new StatusDialog("Tracks entfernen", null, new RemoveMP3s(list));	
				}
			}
		});
		
		modify.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				new EditTrackWindow(list.getSelectedValue());
			}
		});
		
		getDuration.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final List<Track> selectedValues = list.getSelectedValuesList();
				if(selectedValues != null)
				{
					if(selectedValues.size() > 2)
					{
						new StatusDialog("Dauer einlesen", parent, new ReadDuration(selectedValues));
					}
					else
						new ReadDuration(selectedValues).runFunction(null);
				}
			}
		});
		
		final ActionListener listener = new ListParameterActionListener();
		
		sortOrderBox.setSelectedIndex(1);
		sortOrderBox.addActionListener(listener);
		
		try
		{
			final List<String> listBoxContent = data.getLists();
			listBoxContent.add(0, "Hauptliste");
			final String[] listBoxArray = new String[listBoxContent.size()];
			listBoxContent.toArray(listBoxArray);
			listBox.setModel(new DefaultComboBoxModel<>(listBoxArray));
			listBox.addActionListener(listener);
		}
		catch (final ListException e1)
		{
			log.error("Fehler bei Zugriff auf Datenbank.", e1);
		}
		
		searchText.addActionListener(listener);
	}
	
	private class ListParameterActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			String searchTextString = searchText.getText();
			if(searchTextString.length() == 0)
				searchTextString = null;
			String selectedList;
			if(listBox.getSelectedIndex() == 0)
				selectedList = null;
			else
				selectedList = (String)listBox.getSelectedItem();
			try
			{
				lm.search(searchTextString, SortOrder.arrayIndexToSortOrder(sortOrderBox.getSelectedIndex()), selectedList);
				list.validate();
			}
			catch (final ListException e1)
			{
				JOptionPane.showMessageDialog(null, "Fehler beim durchsuchen.\n" + e1.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void initComponents() 
	{
		try
		{
			final DbMasterListModel mlm = controller.getListProvider().getMasterList();
			label1 = new JLabel();
			addFolder = new JButton();
			button1 = new JButton();
			modify = new JButton();
			addFile = new JButton();
			button2 = new JButton();
			getDuration = new JButton();
			removeFile = new JButton();
			button3 = new JButton();
			label2 = new JLabel();
			sortOrderBox = new JComboBox<>(SortOrder.getStringArray());
			label3 = new JLabel();
			listBox = new JComboBox<>();
			label4 = new JLabel();
			searchText = new JTextField();
			lm = new SearchListModel(null, SortOrder.DEFAULT, null);
			scrollPane1 = new PDJScrollList(lm);
			list = scrollPane1.getList();
			list.setScrollToPlayedEnabled(false);
			final SummaryListener sl = new SummaryListener();
			list.addListSelectionListener(sl);
			data.addListListener(sl);
			songsInList = new JLabel();
			completeDuration = new JLabel();
			songsSelected = new JLabel();
			playDuration = new JLabel();
//			songsWithProblems = new JLabel();
			final CellConstraints cc = new CellConstraints();
	
			//======== this ========
	
			setLayout(new FormLayout(
				"$rgap, $lcgap, 36dlu, $lcgap, 49dlu, $lcgap, 47dlu, $lcgap, 37dlu, $lcgap, 33dlu, $lcgap, 31dlu, $lcgap, 34dlu, $lcgap, 38dlu, $lcgap, 40dlu, $lcgap, 47dlu:grow, $rgap",
				"$rgap, $lgap, 12dlu, 7dlu, 17dlu, $lgap, 18dlu, $lgap, 10dlu, $lgap, 12dlu, $lgap, fill:105dlu:grow, $lgap, 17dlu"));
			((FormLayout)getLayout()).setColumnGroups(new int[][] {{3, 5, 9, 11}, {7, 13}});
			((FormLayout)getLayout()).setRowGroups(new int[][] {{5, 7, 9, 11, 15}});
	
			//---- label1 ----
			label1.setText("Hauptliste verwalten...");
			label1.setFont(new Font("Tahoma", Font.BOLD, 14));
			add(label1, cc.xywh(3, 3, 17, 1));
	
			//---- addFolder ----
			addFolder.setText("Verzeichnis einf\u00fcgen...");
			add(addFolder, cc.xywh(3, 5, 3, 1));
	
			//---- button1 ----
			button1.setText("text");
			add(button1, cc.xy(3, 5));
	
			//---- modify ----
			modify.setText("Track bearbeiten");
			add(modify, cc.xywh(9, 5, 3, 1));
	
			//---- addFile ----
			addFile.setText("Datei einf\u00fcgen...");
			add(addFile, cc.xywh(3, 7, 3, 1));
	
			//---- button2 ----
			button2.setText("text");
			add(button2, cc.xy(3, 7));
	
			//---- getDuration ----
			getDuration.setText("Dauer einlesen");
			add(getDuration, cc.xywh(9, 7, 3, 1));
	
			//---- removeFile ----
			removeFile.setText("Tracks entfernen");
			add(removeFile, cc.xywh(3, 9, 3, 1));
	
			//---- button3 ----
			button3.setText("text");
			add(button3, cc.xy(3, 9));
	
			//---- label2 ----
			label2.setText("Sortierung:");
			add(label2, cc.xy(3, 11));
			add(sortOrderBox, cc.xywh(5, 11, 3, 1));
	
			//---- label3 ----
			label3.setText("Liste:");
			add(label3, cc.xy(9, 11));
			add(listBox, cc.xywh(11, 11, 3, 1));
	
			//---- label4 ----
			label4.setText("Suche:");
			add(label4, cc.xy(15, 11));
			add(searchText, cc.xywh(17, 11, 5, 1));
	
			//======== scrollPane1 ========
			{
				scrollPane1.setViewportView(list);
			}
			add(scrollPane1, cc.xywh(3, 13, 19, 1));
	
			//---- songsInList ----
			songsInList.setText(mlm.getSize() + " Lieder in der Hauptliste!");
			add(songsInList, cc.xywh(3, 15, 3, 1));
	
			//---- completeDuration ----
			mainPlayTime = playTime(mlm);
			completeDuration.setText("Gesamtspieldauer: " + klierlinge.utils.Functions.formatTime(mainPlayTime));
			add(completeDuration, cc.xywh(7, 15, 3, 1));
	
			//---- songsSelected ----
			songsSelected.setText("        " + list.getSelectedIndices().length + " Lieder ausgew\u00e4hlt    -->");
			add(songsSelected, cc.xywh(11, 15, 3, 1));
	
			//---- playDuration ----
			selectedPlayTime = playTime(list.getSelectedValuesList());
			playDuration.setText("Spieldauer: " + klierlinge.utils.Functions.formatTime(selectedPlayTime));
			add(playDuration, cc.xywh(15, 15, 3, 1));
	
			//---- songsWithProblems ----
//			songsWithProblems.setText("...Lieder mit Problemen");
//			add(songsWithProblems, cc.xywh(19, 15, 3, 1));
		}
		catch (final ListException e1)
		{
			log.error("Fehler bei Zugriff auf Datenbank.", e1);
		}
	}
	
	protected static int playTime(final List<Track> tracks)
	{
		int playTime = 0;
		for(final Track track : tracks)
			playTime += track.getDuration();
		return playTime;
	}
	
	protected static int playTime(final ListModel<Track> listModel)
	{
		int playTime = 0;
		for(int i = 0; i < listModel.getSize(); i++)
			playTime += listModel.getElementAt(i).getDuration();
		return playTime;
	}
	
	protected static int playTime(final Track[] tracks)
	{
		int playTime = 0;
		for(final Track tr : tracks)
		{
			playTime += tr.getDuration();
		}
		return playTime;
	}
	
	protected class SummaryListener extends ListAdapter implements ListSelectionListener
	{
		private long lastRepaint = System.currentTimeMillis();
		private final Timer repaintTimer = new Timer(1000, new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent e)
			{
				completeDuration.repaint();
			}
		});
		
		public SummaryListener()
		{
			repaintTimer.setRepeats(false);
		}
		
		private void repaint(final boolean wait)
		{
			if(!wait || System.currentTimeMillis() - lastRepaint > 1000)
			{
				repaintTimer.stop();
				lastRepaint = System.currentTimeMillis();
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						@Override public void run()
						{
							completeDuration.repaint();
						}
					});
				}
				catch(final InterruptedException | InvocationTargetException e)
				{
					log.error("Update der Gesamt-Dauer fehlgeschlagen.", e);
				}
			}
			else
				repaintTimer.restart();
		}
		
		@Override
		public void valueChanged(final ListSelectionEvent e)
		{
			if(e.getValueIsAdjusting())
				return;
			playDuration.setText("Spieldauer: " + klierlinge.utils.Functions.formatTime(playTime(list.getSelectedValuesList())));
			songsSelected.setText("        " + list.getSelectedIndices().length + " Lieder ausgew\u00e4hlt");
		}

		@Override
		public void trackAdded(final DbTrack track, final boolean eventsFollowing)
		{
			mainPlayTime += track.getDuration();
			completeDuration.setText("Gesamtspieldauer: " + klierlinge.utils.Functions.formatTime(mainPlayTime));
			repaint(eventsFollowing);
		}

		@Override
		public void trackChanged(final DbTrack newTrack, final Track oldTrack, final boolean eventsFollowing)
		{
			mainPlayTime -= oldTrack.getDuration();
			mainPlayTime += newTrack.getDuration();
			completeDuration.setText("Gesamtspieldauer: " + klierlinge.utils.Functions.formatTime(mainPlayTime));
			repaint(eventsFollowing);
		}

		@Override
		public void trackDeleted(final DbTrack track, final boolean eventsFollowing)
		{
			mainPlayTime -= track.getDuration();
			completeDuration.setText("Gesamtspieldauer: " + klierlinge.utils.Functions.formatTime(mainPlayTime));
			repaint(eventsFollowing);
		}
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sam Meier
	private JLabel label1;
	private JButton addFolder;
	private JButton button1;
	private JButton modify;
	private JButton addFile;
	private JButton button2;
	private JButton getDuration;
	private JButton removeFile;
	private JButton button3;
	private JLabel label2;
	private JComboBox<String> sortOrderBox;
	private JLabel label3;
	private JComboBox<String> listBox;
	private JLabel label4;
	private JTextField searchText;
	private PDJScrollList scrollPane1;
	private JLabel songsInList;
	private JLabel completeDuration;
	private JLabel songsSelected;
	private JLabel playDuration;
//	private JLabel songsWithProblems;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
