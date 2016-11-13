package de.klierlinge.partydj.gui.settings.tools;

import java.util.List;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.StatusDialog.StatusSupportedFunction;
import de.klierlinge.partydj.players.PlayerException;

/**
 * Liest die Dauer von Tracks ein.
 * <br>Die Tracks werden im Konstruktor in einem Array übergeben.
 * <br>List die Dauer auch ein, wenn die Tracks bereits eine Dauer > 0 haben.
 * 
 * @author Eraser, Sam
 * 
 * @see StatusSupportedFunction
 * @see StatusDialog
 */
public class ReadDuration implements StatusSupportedFunction
{
	private boolean goOn = true;
	private final List<Track> list;
	
	public ReadDuration(final List<Track> list)
	{
		this.list = list;
	}
	
	@Override
	public void runFunction(final StatusDialog status)
	{
		int count = 0;
	
		if(status != null)
			status.setBarMaximum(list.size());
		
		for(final Track track : list)
		{
			if(!goOn)
				break;
			
			if(status != null)
			{
				status.setLabel(track.getName());
			}

			try
			{					
				Controller.getInstance().getPlayer().getDuration(track);
			}
			catch (final PlayerException e) { /* ignored */ }
			
			if(status != null)
			{
				count++;
				status.setBarPosition(count);
			}
		}
	}

	@Override
	public void stopTask()
	{
		goOn = false;
	}
}