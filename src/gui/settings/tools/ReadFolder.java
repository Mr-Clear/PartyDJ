package gui.settings.tools;

import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import javax.swing.JOptionPane;
import basics.Controller;
import lists.ListException;
import common.Track;

/**
 * Durchsucht einen Ordner nach MP3-Dateien und fügt sie in die Hauptliste ein.
 * 
 * @author Eraser
 *
 * @see StatusSupportedFunction
 * @see StatusDialog
 */
public class ReadFolder implements StatusSupportedFunction
{
	private final String folderPath;
	private int count = 0;
	private boolean goOn = true;
	private final boolean searchSubFolders;

	public ReadFolder(String path, boolean subFolders)
	{
		folderPath = path;
		searchSubFolders = subFolders;
	}
	
	public void runFunction(StatusDialog sd)
	{
		sd.setBarMaximum(Integer.MAX_VALUE);
	
		sd.setLabel("Beginne.");
		search(new File(folderPath), 0, 1, sd, searchSubFolders);

		sd.setLabel("Fertig.");

		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**@param progress Bisheriger Vortschritt zwischen 0 und 1
	 * @param ratio Anteil am Vortschritt zwischen 0 und 1
	 */
	private void search(File folder, double progress, double ratio, StatusDialog sd, boolean subFolders)
	{
		if(!goOn)
			return;
		
		sd.setLabel(count + ": " + folder.getPath());
		String[] files = folder.list(new FilenameFilter (){
					public boolean accept(File path, String name)
					{
						return (name.toLowerCase().endsWith(".mp3"));
					}});

		for(String file : files)
		{
			addTrack(folder + System.getProperty("file.separator") + file, sd);
		}
		
		if(subFolders)
		{
			File[] folders = folder.listFiles(new FileFilter (){
				public boolean accept(File file)
				{
					return file.isDirectory();
				}});
			int folderCount = folders.length + 1;
			ratio /= folderCount;
			
			progress += ratio;
			sd.setBarPosition((int)(Integer.MAX_VALUE * progress));
			
			for(File subFolder : folders)
			{
				if(!goOn)
					return;
				
				search(subFolder, progress, ratio, sd, subFolders);
				progress += ratio;
			}
		}
	}
	
	private void addTrack(String path, StatusDialog sd)
	{
		String name = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
		try
		{
			//TODO New Track(String) verwenden.
			Track newTrack = new Track(-1, path, name, 0, new File(path).length(), Track.Problem.NONE, null);
			Controller.getInstance().getData().addTrack(newTrack);
			if(newTrack.index != -1)
			{
				count++;
				sd.setLabel(count + ": " + path);
			}
		}
		catch (ListException e){e.printStackTrace();}
	}
	
	public void stopTask()
	{
		goOn = false;
	}
}