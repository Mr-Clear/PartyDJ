package gui.settings.tools;

import javax.swing.JOptionPane;
import common.Reporter;
import common.Track;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.ListException;
import basics.Controller;

public class AddM3U implements StatusSupportedFunction, Reporter<Track>
{
	private final String filePath;
	private boolean stopped = false;

	public AddM3U(String path)
	{
		filePath = path;
	}

	public void runFunction(StatusDialog sd) 
	{
		int count;
		
		count = common.ReadM3U.readM3U(filePath, this, sd, false);

		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public boolean report(Track track)
	{
		if(track == null)
			return false;
		
		int newIndex;
		try
		{
			newIndex = Controller.getInstance().getData().addTrack(track);
		}
		catch (ListException ignored)
		{
			newIndex = -1;
		}

		return newIndex != -1;
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
