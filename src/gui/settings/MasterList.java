package gui.settings;

import gui.EditTrackWindow;
import gui.PDJList;
import gui.PDJScrollList;
import gui.StatusDialog;
import gui.settings.tools.ReadDuration;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import lists.ListException;
import lists.SearchListModel;
import common.Track;
import data.IData;
import data.SortOrder;
import basics.Controller;

//TODO löschen

/**
 * Setting-Panel über das sie die Hauptliste verwalten lässt.
 * 
 * @author Eraser
 * 
 * @see SettingWindow
 */
@Deprecated
public class MasterList extends JPanel
{
	private static final long serialVersionUID = 6101715371957303072L;
	private PDJList list;
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final JFrame frame;
	
	private JComboBox sortOrderBox;
	private JComboBox listBox;
	private JTextField searchText;
	
	/**Für StatusDialog
	 */
	long time = 0;
	//private Track track;

	
	private SearchListModel listModel;

	public MasterList(final JFrame parent)
	{
		super();
		frame = parent;
		if(SwingUtilities.isEventDispatchThread())
			init();
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){
					@Override
					public void run()
					{
						init();
					}});
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	protected void init()
	{
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		Box topBox = Box.createHorizontalBox();
		Box buttonBox1 = Box.createVerticalBox();
		
		buttonBox1.add(Box.createVerticalStrut(8));
		
		JButton addFolder = new JButton("Verzeichnis einfügen...");
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
							
							new StatusDialog("Lese Verzeichnisse", frame, new gui.settings.tools.ReadFolder(folderPath, true));
						}});
		buttonBox1.add(addFolder);
		buttonBox1.add(Box.createVerticalStrut(5));
		
		JButton addFile = new JButton("Datei einfügen...");
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
					        	new StatusDialog("Lese M3U", frame, new gui.settings.tools.AddM3U(filePath));
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
		buttonBox1.add(addFile);
		buttonBox1.add(Box.createVerticalStrut(5));
		
		JButton removeFile = new JButton("Tracks entfernen");
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
		
		buttonBox1.add(removeFile);
		buttonBox1.add(Box.createVerticalStrut(8));
		topBox.add(Box.createHorizontalStrut(8));
		topBox.add(buttonBox1);
		
		Box buttonBox2 = Box.createVerticalBox();

		buttonBox2.add(Box.createVerticalStrut(5));
		JButton modify = new JButton("Bearbeiten");
		modify.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								new EditTrackWindow((Track)list.getSelectedValue());
							}
						});
		buttonBox2.add(modify);
		buttonBox2.add(Box.createVerticalStrut(5));
		
		JButton getDuration = new JButton("Dauer einlesen");
		getDuration.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								
								if(list.getSelectedValues() != null)
								{
									if(list.getSelectedValues().length > 2)
									{
										new StatusDialog("Dauer einlesen", frame, new ReadDuration(list.getSelectedValues()));
									}
									else
										new ReadDuration(list.getSelectedValues()).runFunction(null);
								}
							}
						});
		
		buttonBox2.add(getDuration);
		buttonBox2.add(Box.createVerticalGlue());
		topBox.add(buttonBox2);
		topBox.add(Box.createHorizontalGlue());
		
		box.add(topBox);
		
		try
		{
			listModel = new SearchListModel(null, SortOrder.DEFAULT, null);
			ActionListener listener = new ListParameterActionListener();
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(8));
			hbox.add(new JLabel("Reihenfolge:"));
			hbox.add(Box.createHorizontalStrut(4));
			
			PDJScrollList scrollList = new PDJScrollList(listModel);
			list = scrollList.getList();
			scrollList.setPreferredSize(scrollList.getMaximumSize());
			box.add(scrollList);
			box.add(Box.createVerticalStrut(8));
			
			sortOrderBox = new JComboBox(SortOrder.getStringArray());
			sortOrderBox.setSelectedIndex(1);
			sortOrderBox.addActionListener(listener);

			hbox.add(sortOrderBox);
			hbox.add(Box.createHorizontalStrut(8));
			hbox.add(new JLabel("Liste:"));
			hbox.add(Box.createHorizontalStrut(4));
			List<String> listBoxContent = data.getLists();
			listBoxContent.add(0, "Hauptliste");
			listBox = new JComboBox(listBoxContent.toArray());
			listBox.addActionListener(listener);
			hbox.add(listBox);
			hbox.add(Box.createHorizontalStrut(8));
			hbox.add(new JLabel("Suchtext:"));
			hbox.add(Box.createHorizontalStrut(4));
			searchText = new JTextField();
			searchText.addActionListener(listener);
			hbox.add(searchText);
			hbox.add(Box.createHorizontalStrut(8));
			box.add(hbox);
			box.add(Box.createVerticalStrut(8));
		}
		catch (ListException e)
		{
			JLabel listErrorMessage = new JLabel("Kann Liste nicht laden: \n" + e.getMessage());
			listErrorMessage.setForeground(Color.RED);
			box.add(listErrorMessage);
		}
		
		add(box);	
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
}