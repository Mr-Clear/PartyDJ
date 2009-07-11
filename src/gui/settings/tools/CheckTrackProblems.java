package gui.settings.tools;

import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import common.Track;

/**
 * Prüft ob die Tracks funktionieren.
 * <br>Die Tracks werden im Konstruktor in einem Array übergeben.
 * 
 * @author Eraser
 * 
 * @see StatusSupportedFunction
 * @see StatusDialog
 */
public class CheckTrackProblems implements StatusSupportedFunction
{
	private boolean goOn = true;
	private final Track[] list;
	
	public CheckTrackProblems(Track[] list)
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

			track.checkForProblem(true);
			
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