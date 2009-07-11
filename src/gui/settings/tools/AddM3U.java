package gui.settings.tools;

import javax.swing.JOptionPane;
import common.Reporter;
import common.Track;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.EditableListModel;
import lists.data.DbTrack;

/**
 * Liest eine M3U-Datei und fügt die Tracks der Hauptliste hinzu.
 * 
 * @author Eraser
 *
 * @see common.ReadM3U
 * @see StatusSupportedFunction
 * @see StatusDialog
 * @see Reporter
 */
public class AddM3U implements StatusSupportedFunction, Reporter<Track>
{
	private final String filePath;
	private boolean stopped = false;
	protected EditableListModel listModel;

	public AddM3U(String path)
	{
		filePath = path;
	}
	
	public AddM3U(String path, EditableListModel elm)
	{
		filePath = path;
		listModel = elm;
	}
	
	@Override
	public void runFunction(StatusDialog sd) 
	{
		int count;
		
		count = common.ReadM3U.readM3U(filePath, this, sd, false, listModel == null);
		sd.stopTimer();
		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public boolean report(Track track)
	{
		if(track == null)
			return false;
		
		new DbTrack(track);

		//TODO herausfinden ob der Track neu ist.
		return true;
	}

	public void stopTask()
	{
		stopped = true;
	}

	public boolean isStopped()
	{
		return stopped;
	}
}
