package gui.settings;

import gui.EditTrackWindow;
import gui.PDJList;
import gui.PDJScrollList;
import gui.StatusDialog;
import gui.StatusDialog.ÖlaPalöma;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import players.PlayerException;
import lists.ListException;
import lists.SearchListModel;
import lists.TrackListModel;
import common.Track;
import data.IData;
import data.SortOrder;
import basics.Controller;

public class MasterList extends JPanel
{
	private static final long serialVersionUID = 6101715371957303072L;
	private PDJList list;
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private final Frame frame;
	
	private JComboBox sortOrderBox;
	private JComboBox listBox;
	private JTextField searchText;
	
	//Für StatusDialog
	long time = 0;
	private Track track;

	
	private SearchListModel listModel;

	public MasterList(Frame parent)
	{
		super();
		frame = parent;
		
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
							
							new ReadFolder(folderPath, frame);
						}});
		buttonBox1.add(addFolder);
		JButton addFile = new JButton("Datei einfügen...");
		addFile.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0)
						{
							final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
							chooser.setDialogType(JFileChooser.OPEN_DIALOG);
					        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					        				        
					        if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
					        	return;
					        
					        File file = chooser.getSelectedFile();
					        String filePath = file.getPath();
					        
					        if(filePath.toLowerCase().endsWith(".m3u"))
					        {
					        	new ReadM3U(filePath, frame);
					        }
					        else
						        {
						        double duration = 0;
						        common.Track.Problem problem = common.Track.Problem.NONE;
						        try
								{
						        	duration = controller.getPlayer().getDuration(filePath);
								}
								catch (PlayerException e)
								{
									problem = common.Track.Problem.CANT_PLAY;
								}
								
								if(duration == 0)
									if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Datei kann nicht wiedergegeben werden.\nTrotdem hinzufügen?", "Datei einfügen", JOptionPane.YES_NO_OPTION))
										return;
								
								Track track = new Track(-1, filePath, null, duration, file.length(), problem, null);
								
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
		
		buttonBox1.add(Box.createVerticalStrut(8));
		
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
										data.deleteTrack(((TrackListModel)list.getListModel()).getElementAt(selected[i]));
									}
								}
								catch (ListException e)
								{
									JOptionPane.showMessageDialog(null, "Löschen Fehlgeschlagen:\n" + e.getMessage(), "Tracks entfernen", JOptionPane.ERROR_MESSAGE);
								}
							}
						}});
		
		buttonBox1.add(removeFile);
		buttonBox1.add(Box.createVerticalStrut(8));
		topBox.add(Box.createHorizontalStrut(8));
		topBox.add(buttonBox1);
		
		Box buttonBox2 = Box.createVerticalBox();

		buttonBox2.add(Box.createVerticalStrut(8));
		JButton modify = new JButton("Bearbeiten");
		modify.addActionListener(new ActionListener()
									{
										public void actionPerformed(ActionEvent e)
										{
											new EditTrackWindow((Track)list.getSelectedValue());
										}
									});
		buttonBox2.add(modify);
		
		JButton getDuration = new JButton("Dauer einlesen");
		getDuration.addActionListener(new ActionListener()
										{
											public void actionPerformed(ActionEvent e)
											{
												
												if(list.getSelectedValues() != null)
												{
													if(list.getSelectedValues().length > 2)
													{
														new StatusDialog("Dauer einlesen", frame, new ReadDuration(), list);
													}
													else
														new ReadDuration().runStatusDialog(null, list);
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
			PDJScrollList scrollList = new PDJScrollList(listModel);
			list = scrollList.getList();
			scrollList.setPreferredSize(scrollList.getMaximumSize());
			box.add(scrollList);
		}
		catch (ListException e)
		{
			JLabel list = new JLabel("Kann Liste nicht laden: \n" + e.getMessage());
			list.setForeground(Color.RED);
			box.add(list);
		}
		
		add(box);		
	}
	
	class ReadM3U extends JDialog implements WindowListener, Runnable
	{
		private static final long serialVersionUID = 2818162238292989571L;
		private Thread addThread = new Thread(this);
		private final String filePath;
		private final Frame owner;
		private final JLabel label;
		private final JProgressBar progressBar;

		ReadM3U(String path, Frame owner)
		{
			super(owner, "Lese M3U...");
			filePath = path;
			this.owner = owner;
			
			setSize(new Dimension(500, 100));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			this.addWindowListener(this);
			
			setLayout(new BorderLayout());
			
			Box box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(8));
			label = new JLabel("Öffne Datei...");
			box.add(label);
			box.add(Box.createVerticalStrut(8));
			progressBar = new JProgressBar();
			box.add(progressBar);
			box.add(Box.createVerticalStrut(8));
			
			add(box);
			
			setVisible(true);
					
			addThread.start();
		}
		
		public void run()
		{
			int count = 0;
			int bytes = 0;
			
			try
			{
				BufferedReader r = new BufferedReader(new FileReader(filePath));
				progressBar.setMinimum(0);
				progressBar.setMaximum((int)new File(filePath).length());
				String line;
				Track newTrack;
				label.setText("Beginne.");
				while ((line = r.readLine()) != null)
				{
					if(Thread.currentThread() != addThread)
						break;
					
					bytes += line.length();
					
					if(line.charAt(0) != '#')
					{
						String name = line.substring(line.lastIndexOf("\\") + 1, line.lastIndexOf("."));
						try
						{
							newTrack = new Track(-1, line, name, 0, 0, Track.Problem.NONE, null);
							data.addTrack(newTrack);
							if(newTrack.index != -1)
								count++;
						}
						catch (ListException e){}
						label.setText(count + ": " + name);
						progressBar.setValue(bytes);
					}
				}
			}
			catch (IOException e){}
			label.setText("Fertig.");

			JOptionPane.showMessageDialog(owner, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
		
		public void windowClosing(WindowEvent arg0)
		{
			addThread = null;
		}

		public void windowActivated(WindowEvent arg0){}
		public void windowClosed(WindowEvent arg0){}
		public void windowDeactivated(WindowEvent arg0){}
		public void windowDeiconified(WindowEvent arg0){}
		public void windowIconified(WindowEvent arg0){}
		public void windowOpened(WindowEvent arg0){}
	}
	
	class ReadFolder extends JDialog implements WindowListener, Runnable
	{
		private static final long serialVersionUID = 2818162238292989571L;
		private Thread addThread = new Thread(this);
		private final String folderPath;
		private final Frame owner;
		private final JLabel lblFolder;
		private final JLabel lblTrack;
		private final JProgressBar progressBar;
		private int count = 0;

		ReadFolder(String path, Frame owner)
		{
			super(owner, "Lese Verzeichnis...");
			folderPath = path;
			this.owner = owner;
			
			setSize(new Dimension(500, 100));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(this);
			
			setLayout(new BorderLayout());
			
			Box box = Box.createVerticalBox();
			box.add(Box.createVerticalStrut(8));
			lblFolder = new JLabel("Öffne Ordner...");
			box.add(lblFolder);
			box.add(Box.createVerticalStrut(8));
			lblTrack = new JLabel("...");
			box.add(lblTrack);
			box.add(Box.createVerticalStrut(8));
			progressBar = new JProgressBar();
			box.add(progressBar);
			box.add(Box.createVerticalStrut(8));
			
			add(box);
			
			setVisible(true);
					
			addThread.start();
		}
		
		public void run()
		{
			progressBar.setMinimum(0);
			progressBar.setMaximum(Integer.MAX_VALUE);
		
			lblFolder.setText("Beginne.");
			search(new File(folderPath), 0, 1);

			lblFolder.setText("Fertig.");

			JOptionPane.showMessageDialog(owner, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
		
		/**@param progress Bisheriger Vortschritt zwischen 0 und 1
		 * @param ratio Anteil am Vortschritt zwischen 0 und 1
		 */
		private void search(File folder, double progress, double ratio)
		{
			if(Thread.currentThread() != addThread)
				return;
			
			lblFolder.setText(folder.getPath());
			String[] files = folder.list(new FilenameFilter (){
						public boolean accept(File path, String name)
						{
							return (name.toLowerCase().endsWith(".mp3"));
						}});

			for(String file : files)
			{
				addTrack(folder + System.getProperty("file.separator") + file);
			}
			
			File[] folders = folder.listFiles(new FileFilter (){
				public boolean accept(File file)
				{
					return file.isDirectory();
				}});
			int folderCount = folders.length + 1;
			ratio /= folderCount;
			
			progress += ratio;
			progressBar.setValue((int)(Integer.MAX_VALUE * progress));
			
			for(File subFolder : folders)
			{
				if(Thread.currentThread() != addThread)
					return;
				
				search(subFolder, progress, ratio);
				progress += ratio;
			}
		}
		
		private void addTrack(String path)
		{
			String name = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
			try
			{
				Track newTrack = new Track(-1, path, name, 0, 0, Track.Problem.NONE, null);
				data.addTrack(newTrack);
				if(newTrack.index != -1)
				{
					count++;
					lblTrack.setText(count + ": " + name);
				}
			}
			catch (ListException e){e.printStackTrace();}
		}
		
		public void windowClosing(WindowEvent arg0)
		{
			addThread = null;
		}

		public void windowActivated(WindowEvent arg0){}
		public void windowClosed(WindowEvent arg0){}
		public void windowDeactivated(WindowEvent arg0){}
		public void windowDeiconified(WindowEvent arg0){}
		public void windowIconified(WindowEvent arg0){}
		public void windowOpened(WindowEvent arg0){}
	}
	
	class ReadDuration implements ÖlaPalöma
	{
		private boolean goOn = true;
		public void runStatusDialog(StatusDialog status, Object object)
		{
			/*java.io.PrintWriter pw = null;
			try
			{
				 pw = new java.io.PrintWriter(new java.io.FileOutputStream("C:/Users/Eraser/Desktop/ReadDuration.csv"));
			}
			catch (java.io.FileNotFoundException e)
			{
				e.printStackTrace();
			}
			pw.println("Pfad;Lieddauer;Dateigröße;Lesedauer");*/
			
			int count = 0;
			PDJList list = null;
			
			if(object instanceof PDJList)
				list = (PDJList)object;
			
			if(status != null)
				status.setBarMaximum(list.getSelectedValues().length);
			
			for(int i = 0; i < list.getSelectedValues().length; i++)
			{
				if(!goOn)
					break;
					
				if(list.getSelectedValues()[i] instanceof Track)
					track = (Track)list.getSelectedValues()[i];
				else
					continue;
				
				try
				{					
					//long time = System.nanoTime();
					controller.getPlayer().getDuration(track);
					//pw.println("\"" + track.path + "\";" + Double.toString(track.duration).replace('.', ',') + ";" + new File(track.path).length() + ";" + (System.nanoTime() - time));
				}
				catch (PlayerException pe){}
				
				if(status != null)
				{
					count++;
					status.setLabel(track.name);
					status.setBarPosition(count);
				}
			}
			/*pw.flush();
			pw.close();*/
		}
	
		public void stopTask()
		{
			goOn = false;
		}
	}
	
	class ListParameterActionListener implements ActionListener
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