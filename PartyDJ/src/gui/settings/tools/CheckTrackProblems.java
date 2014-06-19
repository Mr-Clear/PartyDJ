package gui.settings.tools;

import common.Track;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import java.util.List;

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
	private final List<Track> list;
	
	public CheckTrackProblems(final List<Track> list)
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

			track.checkForProblem(true);
			
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