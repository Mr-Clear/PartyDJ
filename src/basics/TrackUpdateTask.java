package basics;

import java.util.Stack;
import java.util.TimerTask;
import players.PlayerException;
import lists.ListException;
import common.Track;

/**
 * Liest die Dauer der Tracks ein.
 * <p>Tracks die eingelesen werden sollen werden im Controller per pushTrackToUpdate
 * auf einen Stack geschrieben. Dieser Stack wird von dieser Klasse abgearbeitet.
 * <p>Tracks die bereits eine Dauer != 0 haben, werden übersprungen.
 * 
 * @author Eraser
 */
class TrackUpdateTask extends TimerTask 
{
	private final Stack<Track> trackUpdateStack;
	private final Controller controller = Controller.getInstance();
	public TrackUpdateTask(Stack<Track> trackUpdateStack)
	{
		this.trackUpdateStack = trackUpdateStack;
	}
	
	public void run() 
	{
		try
		{
			Track track = null;
			synchronized(trackUpdateStack)
			{
				while(true)
				{
					if(trackUpdateStack.empty())
					{
						track = null;
						break;
					}
					track = trackUpdateStack.pop();
	
					if(track.duration == 0 && track.problem == Track.Problem.NONE)
					{
						break;
					}
				}
			}
			
			if(track == null)
			{
				controller.trackUpdateTimer = null;
				this.cancel();
			}
			else
			{
				if(track.duration == 0)
				{
					try
					{
						track.duration = controller.getPlayer().getDuration(track);
					}
					catch (PlayerException e)
					{
						track.problem = e.problem;
						try
						{
							controller.getData().updateTrack(track, Track.TrackElement.PROBLEM);
						}
						catch (ListException e1)
						{}
					}
				}
			}
		}
		catch (Exception e){}
	}
}
