package gui.settings.tools;

import gui.StatusDialog;
import gui.StatusDialog.StatusSupportetFunction;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import javax.swing.JOptionPane;
import basics.Controller;
import lists.ListException;
import common.Track;

public class ReadFolder implements StatusSupportetFunction
{
	private final String folderPath;
	private int count = 0;
	private boolean goOn = true;

	public ReadFolder(String path)
	{
		folderPath = path;
	}
	
	public void runFunction(StatusDialog sd)
	{
		sd.setBarMaximum(Integer.MAX_VALUE);
	
		sd.setLabel("Beginne.");
		search(new File(folderPath), 0, 1, sd);

		sd.setLabel("Fertig.");

		JOptionPane.showMessageDialog(sd, count + " Tracks eingef�gt.", "Datei einf�gen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**@param progress Bisheriger Vortschritt zwischen 0 und 1
	 * @param ratio Anteil am Vortschritt zwischen 0 und 1
	 */
	private void search(File folder, double progress, double ratio, StatusDialog sd)
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
			
			search(subFolder, progress, ratio, sd);
			progress += ratio;
		}
	}
	
	private void addTrack(String path, StatusDialog sd)
	{
		String name = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
		try
		{
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