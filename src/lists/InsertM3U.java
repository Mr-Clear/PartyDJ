package lists;

import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import common.Reporter;
import common.Track;

/**
 * F�gt eine M3U-Datei in eine Liste ein.
 * 
 * @author Eraser
 *
 * @see common.ReadM3U
 * @see StatusSupportedFunction
 * @see Reporter
 */
public class InsertM3U implements StatusSupportedFunction, Reporter<Track>
{
	private final String path;
	private final EditableListModel list;
	private boolean stopped = false;

	public InsertM3U(EditableListModel list, String filePath)
	{
		path = filePath;
		this.list = list;
	}

	public void runFunction(StatusDialog sd) 
	{
		common.ReadM3U.readM3U(path, this, sd);
	}
	
	public boolean report(Track track)
	{
		try
		{
			list.add(basics.Controller.getInstance().getListProvider().assignTrack(track));
			return true;
		}
		catch (ListException e)
		{
			return false;
		}
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
