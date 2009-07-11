package basics;

import java.util.Stack;
import java.util.TimerTask;
import players.PlayerException;
import lists.ListException;
import lists.data.DbTrack;

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
	private final Stack<DbTrack> trackUpdateStack;
	private final Controller controller = Controller.getInstance();
	public TrackUpdateTask(Stack<DbTrack> trackUpdateStack)
	{
		this.trackUpdateStack = trackUpdateStack;
	}
	
	@Override
	public void run() 
	{
		try
		{
			DbTrack track = null;
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
	
					if(track.getDuration() == 0 && track.getProblem() == DbTrack.Problem.NONE)
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
					catch (PlayerException e)
					{
						track.setProblem(e.problem);
						try
						{
							controller.getData().updateTrack(track, DbTrack.TrackElement.PROBLEM);
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
