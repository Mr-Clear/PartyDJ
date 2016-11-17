package de.klierlinge.partydj.gui.settings.tools;

import javax.swing.JOptionPane;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Reporter;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.StatusDialog.StatusSupportedFunction;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;

/**
 * Liest eine M3U-Datei und fügt die Tracks der Hauptliste hinzu.
 * 
 * @author Eraser
 *
 * @see de.klierlinge.partydj.common.ReadM3U
 * @see StatusSupportedFunction
 * @see StatusDialog
 * @see Reporter
 */
public class AddM3U implements StatusSupportedFunction, Reporter<Track>
{
	private final String filePath;
	private boolean stopped = false;
	protected EditableListModel listModel;
	protected final static IData data = Controller.getInstance().getData();

	public AddM3U(final String path)
	{
		filePath = path;
	}
	
	public AddM3U(final String path, final EditableListModel elm)
	{
		filePath = path;
		listModel = elm;
	}
	
	@Override
	public void runFunction(final StatusDialog sd) 
	{
		int count;
		
		count = de.klierlinge.partydj.common.ReadM3U.readM3U(filePath, this, sd, false, listModel == null);
		sd.stopTimer();
		try
		{
			Controller.getInstance().getData().addTrack(null, false);
		}
		catch(final ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Datei eingelesen aber Update der Hauptliste fehlgeschlagen.");
		}
		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}
	
	@Override
	public boolean report(final Track track)
	{
		if(track == null)
			return false;
		
		boolean count = false;
		
		try
		{
			count = data.getTrack(track.getPath(), false) == null;
			data.addTrack(track, true);
		}
		catch(final ListException e)
		{
			stopTask();
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Track einfügen fehlgeschlagen.");
		}

		return (count);
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