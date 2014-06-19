package basics;

import common.Track;
import players.PlayerException;
import java.util.Stack;
import java.util.TimerTask;

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
	public TrackUpdateTask(final Stack<Track> trackUpdateStack)
	{
		this.trackUpdateStack = trackUpdateStack;
	}
	
	@Override
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
	
					if(track.getDuration() == 0 && track.getProblem() == Track.Problem.NONE)
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
				if(track.getDuration() == 0)
				{
					try
					{
						track.setDuration(controller.getPlayer().getDuration(track));
					}
					catch (final PlayerException e)
					{
						track.setProblem(e.getProblem());
					}
				}
			}
		}
		catch (final Exception e) { /* ignore */ }
	}
}
