package network.remote;

import basics.PlayerContact;
import common.Track;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkPlayer extends NetworkInterface implements IPlayer, PlayStateListener
{
	protected final List<PlayStateListener> playStateListener = new ArrayList<>();
	protected long positionUpdateInterval = 10000;
	protected Object positionUpdateObserver = new Object();
	protected volatile long lastPositionTime;
	protected volatile double lastPosition;
	protected volatile boolean playState;
	
	public NetworkPlayer(final ObjectOutputStream oos, final Map<Long, Thread> invocationThreads, final Map<Long, Serializable> invocationAnswers)
	{
		super(oos, invocationThreads, invocationAnswers);
		addPlayStateListener(this);
	}

	@Override
	public void addPlayStateListener(final PlayStateListener listener)
	{
		synchronized(playStateListener)
		{
			playStateListener.add(listener);
		}
	}
	
	@Override
	public void removePlayStateListener(final PlayStateListener listener)
	{
		getPlayStateListener().remove(listener);
	}

	public List<PlayStateListener> getPlayStateListener()
	{
		return playStateListener;
	}

	@Override
	public boolean checkPlayable(final Track track)
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.CheckPlayable(invocationId, track));
		
		return (Boolean)waitForAnswer(invocationId);
	}

	@Override
	public void dispose()
	{
		sendInvocation(new Invocation.Dispose());
	}

	@Override
	public void fadeIn()
	{
		sendInvocation(new Invocation.FadeIn());
	}

	@Override
	public void fadeInOut()
	{
		sendInvocation(new Invocation.FadeInOut());
	}

	@Override
	public void fadeOut()
	{
		sendInvocation(new Invocation.FadeOut());
	}

	@Override
	public Track getCurrentTrack()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetCurrentTrack(invocationId));
		
		return (Track)waitForAnswer(invocationId);
	}

	@Override
	public double getDuration()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetDuration(invocationId));
		
		return (Double)waitForAnswer(invocationId);
	}

	@Override
	public double getDuration(final Track track) throws PlayerException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetDuration(invocationId, track));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof PlayerException)
			throw (PlayerException)answer;
		return (Double)answer;
	}

	@Override
	public double getDuration(final String filePath) throws PlayerException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetDuration(invocationId, filePath));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof PlayerException)
			throw (PlayerException)answer;
		return (Double)answer;
	}

	@Override
	public double getDuration(final File file) throws PlayerException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetDuration(invocationId, file));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof PlayerException)
			throw (PlayerException)answer;
		return (Double)answer;
	}

	@Override
	public boolean getPlayState()
	{
		return playState;
	}
	
	public boolean getPlayStateBlocking()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetPlayState(invocationId));
		
		final Serializable ret = waitForAnswer(invocationId);
		if(ret instanceof Boolean)
		{
			playState = (Boolean)ret;
			return playState;
		}
		return false;
	}

	@Override
	public double getPosition()
	{
		if(lastPositionTime == 0)
		{
			getPlayStateBlocking();
			return updatePosition();
		}
			
		if(System.currentTimeMillis() - lastPositionTime >= positionUpdateInterval)
			updatePositionAsynchronous();

		synchronized(positionUpdateObserver)
		{
			if(playState)
				return lastPosition + (System.currentTimeMillis() - lastPositionTime) / 1000d;
			return lastPosition;
		}
	}

	@Override
	public int getVolume()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetVolume(invocationId));
		
		return (Integer)waitForAnswer(invocationId);
	}
	
	@Override
	public void load(final Track track) throws PlayerException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.Load(invocationId, track));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof PlayerException)
			throw (PlayerException)answer;
	}

	@Override
	public void pause()
	{
		sendInvocation(new Invocation.Pause());
	}

	@Override
	public void play()
	{
		sendInvocation(new Invocation.Play());
	}

	@Override
	public void playNext()
	{
		sendInvocation(new Invocation.PlayNext());
	}

	@Override
	public void playPause()
	{
		sendInvocation(new Invocation.PlayPause());
	}

	@Override
	public void playPrevious()
	{
		sendInvocation(new Invocation.PlayPrevious());
	}

	@Override
	@Deprecated
	public void setContact(final PlayerContact contact)
	{
		throw new UnsupportedOperationException("NetworkPlayer kann setContact nicht aufrufen.");
	}

	@Override
	public void setPosition(final double seconds)
	{
		sendInvocation(new Invocation.SetPosition(seconds, false, false));
	}

	@Override
	public void setPosition(final double seconds, final boolean autoPlay)
	{
		sendInvocation(new Invocation.SetPosition(seconds, autoPlay, true));
	}

	@Override
	public void setVolume(final int volume)
	{
		sendInvocation(new Invocation.SetVolume(volume));
	}

	@Override
	public void start()
	{
		sendInvocation(new Invocation.Start());
	}

	@Override
	public void start(final Track track) throws PlayerException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.Start(invocationId, track));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof PlayerException)
			throw (PlayerException)answer;
	}	

	@Override
	public void stop()
	{
		sendInvocation(new Invocation.Stop());
	}
	
	protected void updatePositionAsynchronous()
	{
		final Thread updateThread = new Thread(){
			@Override public void run()
			{
				updatePosition();
			}
		};
		updateThread.setDaemon(true);
		updateThread.start();
	}
	
	protected double updatePosition()
	{
		final long invocationId = generateInvocationId();
		final long requestStart = System.currentTimeMillis();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetPosition(invocationId));
		
		final double position = (Double)waitForAnswer(invocationId);
		final long requestAnswered = System.currentTimeMillis();
		final long requestAverengeTime = (requestStart + requestAnswered) >> 1;
		
		synchronized(positionUpdateObserver)
		{
			lastPositionTime = requestAverengeTime;
			lastPosition = position;
		}
		
		return position;
	}

	@Override
	public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
	{
		updatePositionAsynchronous();
	}

	@Override
	public void playStateChanged(final boolean newPlayState)
	{
		playState = newPlayState;
		updatePositionAsynchronous();
	}

	@Override
	public void volumeChanged(final int volume) { /* not to implement */ }
	
	public boolean updatePlayState()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetPlayState(invocationId));
		
		return (Boolean)waitForAnswer(invocationId);
	}
}
