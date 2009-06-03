/*
 * Created by JFormDesigner on Wed Jun 03 15:32:01 CEST 2009
 */

package gui.settings;

import gui.EditTrackWindow;
import gui.PDJList;
import gui.PDJScrollList;
import gui.StatusDialog;
import gui.settings.tools.ReadDuration;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lists.DbMasterListModel;
import lists.ListException;
import lists.SearchListModel;
import basics.Controller;
import com.jgoodies.forms.layout.*;
import common.Track;
import data.IData;
import data.ListAdapter;
import data.SortOrder;

/**
 * @author Sam Meier
 */
public class MainList extends JPanel 
{
	private static final long serialVersionUID = 6101715371957303072L;
	private PDJList list;
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final JFrame parent;
	private SearchListModel lm;
	protected int mainPlayTime;
	protected int selectedPlayTime;
	
	public MainList(JFrame parent) 
	{
		this.parent = parent;
		initComponents();
		initFuntions();
	}

	public void initFuntions()
	{
		addFolder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								        
				if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
					return;
				
				File folder = chooser.getSelectedFile();
				String folderPath = folder.getPath();
				
				new StatusDialog("Lese Verzeichnisse", parent, new gui.settings.tools.ReadFolder(folderPath, true));
			}});

		addFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("Datei wählen");
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setCurrentDirectory(new File(data.readSetting("FileDirectory", common.Functions.getFolder())));
		        				        
		        if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
		        	return;
		        
		        File file = chooser.getSelectedFile();
		        String filePath = file.getPath();
		        
				data.writeSetting("FileDirectory", file.getParent());
		        
		        if(filePath.toLowerCase().endsWith(".m3u"))
		        {
					data.writeSetting("PlayListDirectory", file.getParent());
		        	new StatusDialog("Lese M3U", parent, new gui.settings.tools.AddM3U(filePath));
		        }
		        else
			    {
					Track track = new Track(filePath, true);
					
					try
					{
						data.addTrack(track);
					}
					catch (ListException e)
					{
						JOptionPane.showMessageDialog(null, "Einfügen fehlgeschlagen:\n" + e.getMessage(), "Datei einfügen", JOptionPane.ERROR_MESSAGE);
					}
			    }
			}});
		
		removeFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				synchronized(list)
				{
					int[] selected = list.getSelectedIndices();
					if(selected.length == 0)
						return;
					//Sicherheitsabfrage
					if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Alle " + selected.length + " Tracks aus Hauptliste entfernen?", "Tracks entfernen", JOptionPane.YES_NO_OPTION))
						return;

					try
					{
						//Absteigend löschen, damit keine Indexfehler auftreten
						for(int i = selected.length - 1; i >= 0; i--)
						{
							//TODO ProgressBar o.ä.
							data.deleteTrack(list.getListModel().getElementAt(selected[i]));
						}									
					}
					catch (ListException e)
					{
						JOptionPane.showMessageDialog(null, "Löschen Fehlgeschlagen:\n" + e.getMessage(), "Tracks entfernen", JOptionPane.ERROR_MESSAGE);
					}								
					list.setSelectedIndex(-1);
					list.setSelectedIndices(new int[0]);
				}
			}});
		
		modify.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new EditTrackWindow((Track)list.getSelectedValue());
			}
		});
		
		getDuration.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				if(list.getSelectedValues() != null)
				{
					if(list.getSelectedValues().length > 2)
					{
						new StatusDialog("Dauer einlesen", parent, new ReadDuration(list.getSelectedValues()));
					}
					else
						new ReadDuration(list.getSelectedValues()).runFunction(null);
				}
			}
		});
		
		ActionListener listener = new ListParameterActionListener();
		
		sortOrderBox.setSelectedIndex(1);
		sortOrderBox.addActionListener(listener);
		
		try
		{
			List<String> listBoxContent = data.getLists();
			listBoxContent.add(0, "Hauptliste");
			listBox.setModel(new DefaultComboBoxModel(listBoxContent.toArray()));
			listBox.addActionListener(listener);
		}
		catch (ListException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		searchText.addActionListener(listener);
	}
	
	private class ListParameterActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
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
			catch (ListException e1)
			{
				JOptionPane.showMessageDialog(null, "Fehler beim durchsuchen.\n" + e1.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void initComponents() 
	{
		try
		{
			DbMasterListModel mlm = controller.getListProvider().getMasterList();
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
			sortOrderBox = new JComboBox(SortOrder.getStringArray());
			label3 = new JLabel();
			listBox = new JComboBox();
			label4 = new JLabel();
			searchText = new JTextField();
			lm = new SearchListModel(null, SortOrder.DEFAULT, null);
			scrollPane1 = new PDJScrollList(lm);
			list = scrollPane1.getList();
			list.setScrollToPlayedEnabled(false);
			SummaryListener sl = new SummaryListener();
			list.addListSelectionListener(sl);
			data.addListListener(sl);
			songsInList = new JLabel();
			completeDuration = new JLabel();
			songsSelected = new JLabel();
			playDuration = new JLabel();
			songsWithProblems = new JLabel();
			CellConstraints cc = new CellConstraints();
	
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
			completeDuration.setText("Gesamtspieldauer: " + common.Functions.formatTime(mainPlayTime));
			add(completeDuration, cc.xywh(7, 15, 3, 1));
	
			//---- songsSelected ----
			songsSelected.setText(list.getSelectedIndices().length + " Lieder ausgew\u00e4hlt");
			add(songsSelected, cc.xywh(11, 15, 3, 1));
	
			//---- playDuration ----
			selectedPlayTime = playTime(list.getSelectedValues());
			playDuration.setText("Spieldauer: " + common.Functions.formatTime(selectedPlayTime));
			add(playDuration, cc.xywh(15, 15, 3, 1));
	
			//---- songsWithProblems ----
			songsWithProblems.setText("...Lieder mit Problemen");
			add(songsWithProblems, cc.xywh(19, 15, 3, 1));
		}
		catch (ListException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	protected int playTime(ListModel listModel)
	{
		int playTime = 0;
		for(int i = 0; i < listModel.getSize(); i++)
		{
			if(listModel.getElementAt(i) instanceof Track)
				playTime += ((Track)listModel.getElementAt(i)).duration;
				
		}
		return playTime;
	}
	
	protected int playTime(Track[] tracks)
	{
		int playTime = 0;
		for(Track tr : tracks)
		{
			playTime += tr.duration;
		}
		return playTime;
	}
	
	protected class SummaryListener extends ListAdapter implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if(e.getValueIsAdjusting())
				return;
			playDuration.setText("Spieldauer: " + common.Functions.formatTime(playTime(list.getSelectedValues())));
			songsSelected.setText(list.getSelectedIndices().length + " Lieder ausgew\u00e4hlt");
		}

		@Override
		public void trackAdded(Track track)
		{
			mainPlayTime += track.duration;
			completeDuration.setText("Gesamtspieldauer: " + common.Functions.formatTime(mainPlayTime));
			completeDuration.repaint();
		}

		@Override
		public void trackChanged(Track track)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void trackDeleted(Track track)
		{
			mainPlayTime -= track.duration;
			completeDuration.setText("Gesamtspieldauer: " + common.Functions.formatTime(mainPlayTime));
			completeDuration.repaint();
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
	private JComboBox sortOrderBox;
	private JLabel label3;
	private JComboBox listBox;
	private JLabel label4;
	private JTextField searchText;
	private PDJScrollList scrollPane1;
	private JLabel songsInList;
	private JLabel completeDuration;
	private JLabel songsSelected;
	private JLabel playDuration;
	private JLabel songsWithProblems;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
