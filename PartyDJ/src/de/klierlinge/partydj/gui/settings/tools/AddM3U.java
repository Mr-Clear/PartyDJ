package de.klierlinge.partydj.gui.settings.tools;

import javax.swing.JOptionPane;
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
	private static final Logger log = LoggerFactory.getLogger(AddM3U.class);
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
			log.error("Datei eingelesen aber Update der Hauptliste fehlgeschlagen.", e);
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
			log.error("Track einfügen fehlgeschlagen.", e);
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
