package gui.settings.tools;

import basics.Controller;
import common.Track;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import javax.swing.JOptionPane;
import lists.EditableListModel;
import lists.ListException;

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
	protected EditableListModel listModel;

	public ReadFolder(final String path, final boolean subFolders)
	{
		folderPath = path;
		searchSubFolders = subFolders;
	}
	
	public ReadFolder(final String path, final boolean subFolders, final EditableListModel elm)
	{
		listModel = elm;
		folderPath = path;
		searchSubFolders = subFolders;
	}
	
	@Override
	public void runFunction(final StatusDialog sd)
	{
		sd.setBarMaximum(Integer.MAX_VALUE);
	
		sd.setLabel("Beginne.");
		search(new File(folderPath), 0, 1, sd, searchSubFolders);
		
		try
		{
			Controller.getInstance().getData().addTrack(null, false);
			if(listModel != null)
				listModel.add(null, true);
		}
		catch(final ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Tracks eingefügt, aber update der Listen fehlgeschlagen.");
		}
		
		sd.setLabel("Fertig.");
		sd.stopTimer();
		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**@param progress Bisheriger Vortschritt zwischen 0 und 1
	 * @param ratio Anteil am Vortschritt zwischen 0 und 1
	 */
	private void search(final File folder, double progress, double ratio, final StatusDialog sd, final boolean subFolders)
	{
		if(!goOn)
			return;
		
		sd.setLabel(count + ": " + folder.getPath());
		final String[] files = folder.list(new FilenameFilter()
		{
			@Override
			public boolean accept(final File path, final String name)
			{
				return (name.toLowerCase().endsWith(".mp3"));
			}
		});

		if(files == null)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, null, "Kann angegebenen Pfad nicht öffnen.");
			return;
		}
		
		for(final String file : files)
		{
			if(!goOn)
				return;
			addTrack(folder + System.getProperty("file.separator") + file, sd);
		}
		
		if(subFolders)
		{
			final File[] folders = folder.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(final File file)
				{
					return file.isDirectory();
				}
			});
			final int folderCount = folders.length + 1;
			ratio /= folderCount;
			
			progress += ratio;
			sd.setBarPosition((int)(Integer.MAX_VALUE * progress));
			
			for(final File subFolder : folders)
			{
				if(!goOn)
					return;
				
				search(subFolder, progress, ratio, sd, subFolders);
				progress += ratio;
			}
		}
	}
	
	private void addTrack(final String path, final StatusDialog sd)
	{
		try
		{
			if(!Controller.getInstance().getData().isInDb(path))
				count++;
			
			final Track track = new Track(path, false);
			if(listModel != null)
				listModel.add(track, true);
			else
				Controller.getInstance().getData().addTrack(track, true);
			
			sd.setLabel(count + ": " + path);
		}
		catch (final ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Neuer Track konnte nicht in Liste eingefügt werden.");
		}
	}
	
	@Override
	public void stopTask()
	{
		goOn = false;
	}
}