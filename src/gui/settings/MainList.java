/*
 * Created by JFormDesigner on Tue Jun 02 16:05:38 CEST 2009
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
import lists.ListException;
import lists.SearchListModel;
import basics.Controller;
import com.jgoodies.forms.layout.*;
import common.Track;
import data.IData;
import data.SortOrder;


public class MainList extends JPanel 
{
	private static final long serialVersionUID = 6101715371957303072L;
	private PDJList list;
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final JFrame parent;
	private SearchListModel listModel;
	
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
				listModel.search(searchTextString, SortOrder.arrayIndexToSortOrder(sortOrderBox.getSelectedIndex()), selectedList);
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
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sam Meier
		try
		{
			label1 = new JLabel();
			addFolder = new JButton();
			modify = new JButton();
			addFile = new JButton();
			getDuration = new JButton();
			removeFile = new JButton();
			listModel = new SearchListModel(null, SortOrder.DEFAULT, null);
			scrollPane1 = new PDJScrollList(listModel);
			list = scrollPane1.getList();
			label2 = new JLabel();
			sortOrderBox = new JComboBox(SortOrder.getStringArray());
			label3 = new JLabel();
			listBox = new JComboBox();
			label4 = new JLabel();
			searchText = new JTextField();
			CellConstraints cc = new CellConstraints();

			list.setFontSize(16);
			//======== this ========
	
			setLayout(new FormLayout(
				"$rgap, $lcgap, [29dlu,pref], [52dlu,pref], [42dlu,pref], 11dlu, [35dlu,pref], [32dlu,pref], [16dlu,pref], 11dlu, [35dlu,pref], 39dlu, [11dlu,pref]:grow, $lcgap, $rgap",
				"$rgap, $lgap, pref, 16dlu, 3dlu, 2*($lgap, pref), $lgap, fill:pref:grow, 2*($lgap, pref)"));
			((FormLayout)getLayout()).setColumnGroups(new int[][] {{3, 4, 7, 8, 12}, {5, 9, 13}, {6, 10}});
			((FormLayout)getLayout()).setRowGroups(new int[][] {{5, 7, 9}});
	
			//---- label1 ----
			label1.setText("Hier k\u00f6nnen Sie die Hauptliste verwalten!");
			label1.setFont(new Font("Tahoma", Font.BOLD, 13));
			add(label1, cc.xywh(3, 3, 11, 1));
	
			//---- addFolder ----
			addFolder.setText("Verzeichnis einf\u00fcgen...");
			add(addFolder, cc.xywh(3, 5, 2, 1));
	
			//---- modify ----
			modify.setText("Track bearbeiten...");
			add(modify, cc.xywh(7, 5, 2, 1));
	
			//---- addFile ----
			addFile.setText("Datei einf\u00fcgen...");
			add(addFile, cc.xywh(3, 7, 2, 1));
	
			//---- getDuration ----
			getDuration.setText("Dauer einlesen");
			add(getDuration, cc.xywh(7, 7, 2, 1));
	
			//---- removeFile ----
			removeFile.setText("Tracks entfernen");
			add(removeFile, cc.xywh(3, 9, 2, 1));
	
			//======== scrollPane1 ========
			{
				scrollPane1.setViewportView(list);
			}
			add(scrollPane1, cc.xywh(3, 11, 11, 1));
	
			//---- label2 ----
			label2.setText("Sortierung:");
			add(label2, cc.xy(3, 13));
			add(sortOrderBox, cc.xywh(4, 13, 2, 1));
	
			//---- label3 ----
			label3.setText("Liste:");
			add(label3, cc.xy(7, 13));
			add(listBox, cc.xywh(8, 13, 2, 1));
	
			//---- label4 ----
			label4.setText("Suche:");
			add(label4, cc.xy(11, 13));
			add(searchText, cc.xywh(12, 13, 2, 1));
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}
		catch(ListException le)
		{
			JLabel listErrorMessage = new JLabel("Kann Liste nicht laden: \n" + le.getMessage());
			listErrorMessage.setForeground(Color.RED);
			add(listErrorMessage);
		}
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sam Meier
	private JLabel label1;
	private JButton addFolder;
	private JButton modify;
	private JButton addFile;
	private JButton getDuration;
	private JButton removeFile;
	private PDJScrollList scrollPane1;
	private JLabel label2;
	private JComboBox sortOrderBox;
	private JLabel label3;
	private JComboBox listBox;
	private JLabel label4;
	private JTextField searchText;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
