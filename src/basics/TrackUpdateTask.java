package basics;

import java.util.Stack;
import java.util.TimerTask;
import players.PlayerException;
import lists.ListException;
import common.Track;

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
						try
						{
							controller.getData().updateTrack(track, Track.TrackElement.DURATION);
						}
						catch (ListException e)
						{}
					}
					catch (PlayerException e)
					{
						e.printStackTrace();
						track.problem = e.problem;
						try
						{
							controller.getData().updateTrack(track, Track.TrackElement.PROBLEM);
						}
						catch (ListException e1)
						{}
					}
				}
				System.out.println("Controller updated duration: " + track.path);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
