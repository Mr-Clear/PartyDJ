package de.klierlinge.partydj.lists.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Reporter;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.StatusDialog.StatusSupportedFunction;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;

/**
 * Fügt eine M3U-Datei in eine Liste ein.
 * 
 * @author Eraser
 *
 * @see de.klierlinge.partydj.common.ReadM3U
 * @see StatusSupportedFunction
 * @see Reporter
 */
public class InsertM3U implements StatusSupportedFunction, Reporter<Track>
{
	private static final Logger log = LoggerFactory.getLogger(InsertM3U.class);
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
		de.klierlinge.partydj.common.ReadM3U.readM3U(path, this, sd);
		try
		{
			list.add(null, false);
			data.addTrack(null, false);
		}
		catch(final ListException e)
		{
			log.error("Datei eingelesen, aber Update der Liste fehlgeschlagen.", e);
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
