package gui;
import gui.StatusDialog.StatusSupportedFunction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;

import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import basics.Controller;
import lists.EditableListModel;
import lists.ListException;
import lists.data.DbTrack;
import data.IData;

/**
 * Fenster das beim ersten Start des PartyDJ angezeigt wird.
 * <p>Ermöglicht es dem Benutzer Tracks hinzu zu fügen.
 * 
 * @author Eraser
 *
 */
public class SetupWindow extends javax.swing.JFrame
{
	private static final long serialVersionUID = 8084307658077082675L;
	private JLabel jLabel1;
	private JToggleButton addFile;
	private JToggleButton addFolder;
	private JButton close;
	private JToggleButton importPDJ;
	
	public SetupWindow() 
	{
		super();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				initGUI();
				setLocationRelativeTo(null);
				setVisible(true);
			}});
		
	}
	
	private void initGUI()
	{
		final Controller controller = Controller.getInstance();
		final IData data = controller.getData();
		
		GroupLayout thisLayout = new GroupLayout(getContentPane());
		getContentPane().setLayout(thisLayout);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle("PartyDJ Setup");
		{
			jLabel1 = new JLabel();
			jLabel1.setText("In PartyDJ wurden noch keine Lieder eingefügt.");
		}
		{
			addFile = new JToggleButton();
			addFile.setText("Datei Einfügen");
			addFile.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
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
			        	new StatusDialog("Lese M3U", SetupWindow.this, new gui.settings.tools.AddM3U(filePath));
			        }
			        else
				    {
						new DbTrack(filePath, true);
				    }
				}});
		}
		{
			addFolder = new JToggleButton();
			addFolder.setText("Verzeichnis Einfügen");
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
					
					new StatusDialog("Lese Verzeichnisse", SetupWindow.this, new gui.settings.tools.ReadFolder(folderPath, true));
				}});
		}
		{
			importPDJ = new JToggleButton();
			importPDJ.setText("Aus altem PartyDJ importieren");
			importPDJ.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					final JFileChooser chooser = new JFileChooser("Party DJ.exe wählen");
					chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			        chooser.setFileFilter(new FileFilter(){
						@Override public boolean accept(File f)
						{
							return f.isDirectory() || (f.isFile() && f.getName().equalsIgnoreCase("Party DJ.exe"));
						}
						@Override public String getDescription(){return null;}
						});
					chooser.setCurrentDirectory(new File(data.readSetting("FileDirectory", common.Functions.getFolder())));
			        				        
			        if(chooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
			        	return;
			        
			        File file = chooser.getSelectedFile();
			        String path = file.getParent();
			        
					importFromPDJ2(path);
				}});
		}
		{
			close = new JButton();
			close.setText("Fenster Schließen");
			close.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0)
				{
					controller.unregisterWindow(SetupWindow.this);
				}});
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(addFile, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(importPDJ, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(addFolder, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(close, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap());
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(addFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(47)
			        .addComponent(close, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 0, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(addFile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(importPDJ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 0, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 28, Short.MAX_VALUE)))
			.addContainerGap(36, 36));
		pack();
	}
	//E:\pdj\Party DJ.exe
	private void importFromPDJ2(String root)
	{
		final IData data = Controller.getInstance().getData();
		if(root.endsWith("\\"))
			root = root.substring(0, root.length() - 1);
		String quelle = root + "\\Quelle.lst";
		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(quelle));
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(SetupWindow.this, "Kann Quelle.lst nicht lesen:\n" + e.getMessage(), "Importieren", JOptionPane.ERROR_MESSAGE);
			return;
		}
		while(true)
		{
			String line;
			try
			{
				line = reader.readLine();
			}
			catch (IOException ignored)
			{
				break;
			}
			if(line == null)
				break;
			String [] sl = line.split("\"");
			
			if(sl.length == 4)
			{
				String path= sl[1];
				String type = (sl[3]);
				if(type.startsWith("0"))
				{
			        if(path.toLowerCase().endsWith(".m3u"))
			        {
						data.writeSetting("PlayListDirectory", new File(path).getParent());
			        	new StatusDialog("Lese M3U", SetupWindow.this, new gui.settings.tools.AddM3U(path));
			        }
			        else
				    {
			        	new DbTrack(path, true);
				    }
				}
				else
				{
					type = type.substring(0,1);
					boolean subFolders = false;
					if(type.startsWith("1"))
						subFolders = false;
					else if(type.startsWith("2"))
						subFolders = true;
					else
						break;
					
					new StatusDialog("Lese Verzeichnisse", null, new gui.settings.tools.ReadFolder(path, subFolders));
				}
			}
		}
		
		new StatusDialog("Lese Playlist", SetupWindow.this, new addPdj2List(root + "\\Playlist.lst", "Playlist"));
		new StatusDialog("Lese Wunschliste 1/2", SetupWindow.this, new addPdj2List(root + "\\Zurück.lst", "Wunschliste"));
		new StatusDialog("Lese Wunschliste 2/2", SetupWindow.this, new addPdj2List(root + "\\Wunsch.lst", "Wunschliste"));
		
		new StatusDialog("Lese LastPlayed", SetupWindow.this, new addPdj2LastPlayedList(root + "\\Alt.lst", Controller.getInstance().getLastPlayedName()));
		/*addPdj2List(root + "\\Playlist.lst", "Playlist");
		addPdj2List(root + "\\Zurück.lst", "Wunschliste");
		addPdj2List(root + "\\Wunsch.lst", "Wunschliste");
		addPdj2LastPlayedList(root + "\\Alt.lst", Controller.getInstance().getLastPlayedName());*/
		
		JOptionPane.showMessageDialog(SetupWindow.this, "Import beendet.", "Importieren", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private class addPdj2List implements StatusSupportedFunction
	{
		private final String path;
		private final String listName;
		private boolean stop = false;
		
		public addPdj2List(String path, String listName)
		{
			this.path = path;
			this.listName = listName;
		}

		public void runFunction(StatusDialog sd)
		{
			EditableListModel list;
			BufferedReader reader;
			try
			{
				list = Controller.getInstance().getListProvider().getDbList(listName);
				reader = new BufferedReader(new FileReader(path));
			}
			catch (ListException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(SetupWindow.this, "Fehler bei öffnen der Liste " + listName + ":\n" + e.getMessage(), "Importieren", JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(SetupWindow.this, "Kann Datei nicht lesen:\n" + e.getMessage(), "Importieren", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			sd.setBarMaximum((int)new File(path).length());
			int bytes = 0;
			
			while(true)
			{
				if(stop)
					break;
				String line;
				try
				{
					line = reader.readLine();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}				
				if(line == null)
					break;
				
				bytes += line.length();
				sd.setBarPosition(bytes);
				
				String [] sl = line.split("\"");
				if(sl.length >= 3)
				{
					String filePath = sl[1];
					try
					{
						sd.setLabel(filePath);
						list.add(new DbTrack(filePath, false));
					}
					catch (ListException ignored)
					{
						ignored.printStackTrace();
						break;
					}
				}
			}}

		@Override
		public void stopTask()
		{
			stop = true;
		}
	}
	
	private class addPdj2LastPlayedList implements StatusSupportedFunction
	{
		private final String path;
		private final String listName;
		private boolean stop = false;
		
		public addPdj2LastPlayedList(String path, String listName)
		{
			this.path = path;
			this.listName = listName;
		}
		
		public void runFunction(StatusDialog sd)
		{
			EditableListModel list;
			BufferedReader reader;
			try
			{
				list = Controller.getInstance().getListProvider().getDbList(listName);
				reader = new BufferedReader(new FileReader(path));
			}
			catch (ListException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(SetupWindow.this, "Fehler bei öffnen der Liste " + listName + ":\n" + e.getMessage(), "Importieren", JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(SetupWindow.this, "Kann Datei nicht lesen:\n" + e.getMessage(), "Importieren", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			List<String> fileList = new ArrayList<String>();
			
			while(true)
			{
				if(stop)
					break;
				String line;
				try
				{
					line = reader.readLine();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}
				if(line == null)
					break;
				String [] sl = line.split("\"");
				if(sl.length >= 3)
				{
					String filePath = sl[1];
					sd.setLabel(filePath);
					fileList.add(filePath);
				}
			}
			int min = fileList.size() > 100 ? fileList.size() - 100 : 0;
			int max = fileList.size();
			
			sd.setBarMaximum(max - min);
			
			for(int i = min; i < max; i++)
			{
				if(stop)
					break;
				sd.setBarPosition(i - min);
				sd.setLabel(fileList.get(i));
				try
				{
					list.add(new DbTrack(fileList.get(i), false));
				}
				catch (ListException ignored)
				{
					ignored.printStackTrace();
					break;
				}
			}
		}

		@Override
		public void stopTask()
		{
			stop = true;
		}
	}
}
