package gui.settings.tools;

import basics.Controller;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import players.PlayerException;
import common.Track;

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
	private final Track[] list;
	
	public ReadDuration(Track[] list)
	{
		this.list = list;
	}
	
	public void runFunction(StatusDialog status)
	{
		int count = 0;
	
		if(status != null)
			status.setBarMaximum(list.length);
		
		for(Track track : list)
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
			catch (PlayerException e){}
			
			if(status != null)
			{
				count++;
				status.setBarPosition(count);
			}
		}
	}

	public void stopTask()
	{
		goOn = false;
	}
}