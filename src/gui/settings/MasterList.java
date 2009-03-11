package gui.settings;

import gui.PDJList;
import gui.PDJScrollList;
import java.awt.BorderLayout;
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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import common.ListException;
import common.PlayerException;
import common.Track;
import data.IData;
import basics.Controller;

public class MasterList extends JPanel
{
	private static final long serialVersionUID = 6101715371957303072L;
	private PDJList list;
	private final Controller controller = Controller.instance;
	private final IData data = controller.data;
	private final Frame frame;

	public MasterList(Frame parent)
	{
		super();
		frame = parent;

		setLayout(new BorderLayout());
		
		Box box = Box.createVerticalBox();
		
		box.add(Box.createRigidArea(new Dimension(8, 8)));
		
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
		box.add(addFolder);
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
						        	duration = controller.player.getDuration(filePath);
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
		box.add(addFile);
		
		
		box.add(Box.createRigidArea(new Dimension(8, 8)));
		
		JButton removeFile = new JButton("Tracks entfernen");
		removeFile.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0)
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
									data.deleteTrack(controller.listProvider.getMasterList().getElementAt(selected[i]));
								}
							}
							catch (ListException e)
							{
								JOptionPane.showMessageDialog(null, "Löschen Fehlgeschlagen:\n" + e.getMessage(), "Tracks entfernen", JOptionPane.ERROR_MESSAGE);
							}
						}});
		box.add(removeFile);
		
		box.add(Box.createRigidArea(new Dimension(8, 8)));
		
		try
		{
			PDJScrollList scrollList = new PDJScrollList(controller.listProvider.getMasterList());
			list = scrollList.getList();
			box.add(scrollList);
		}
		catch (ListException e)
		{
			JLabel list = new JLabel("Kann hauptliste nicht laden:\n" + e.getMessage());
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
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			label = new JLabel("Öffne Datei...");
			box.add(label);
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			progressBar = new JProgressBar();
			box.add(progressBar);
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			
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
			this.addWindowListener(this);
			
			setLayout(new BorderLayout());
			
			Box box = Box.createVerticalBox();
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			lblFolder = new JLabel("Öffne Ordner...");
			box.add(lblFolder);
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			lblTrack = new JLabel("...");
			box.add(lblTrack);
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			progressBar = new JProgressBar();
			box.add(progressBar);
			box.add(Box.createRigidArea(new Dimension(8, 8)));
			
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
				addTrack(file);
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
}