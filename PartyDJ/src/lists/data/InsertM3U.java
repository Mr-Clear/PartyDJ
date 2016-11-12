package lists.data;

import basics.Controller;
import common.Reporter;
import common.Track;
import data.IData;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.EditableListModel;
import lists.ListException;

/**
 * Fügt eine M3U-Datei in eine Liste ein.
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
	protected static final IData data = Controller.getInstance().getData();

	public InsertM3U(final EditableListModel list, final String filePath)
	{
		path = filePath;
		this.list = list;
	}

	@Override
	public void runFunction(final StatusDialog sd) 
	{
		common.ReadM3U.readM3U(path, this, sd);
		try
		{
			list.add(null, false);
			data.addTrack(null, false);
		}
		catch(final ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Datei eingelesen, aber Update der Liste fehlgeschlagen.");
		}
	}
	
	@Override
	public boolean report(final Track track)
	{
		try
		{
			data.addTrack(track, true);
			list.add(track, true);
			return true;
		}
		catch (final ListException e)
		{
			return false;
		}
	}

	@Override
	public void stopTask()
	{
		stopped = true;
	}

	@Override
	public boolean isStopped()
	{
		return stopped;
	}
}
