package de.klierlinge.partydj.network.remote;

import java.io.Serializable;
import java.util.List;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.ListListener;
import de.klierlinge.partydj.data.SettingListener;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.players.PlayStateListener;
import de.klierlinge.partydj.players.PlayStateListener.Reason;

/**
 * Ein Ereigniss das von RemoteServer an die Clients gesendet wird.  
 * 
 * @author Eraser
 */
public interface Event extends Serializable
{	
	/** Löst das Ereignis beim Client aus. 
	 * @param playStateListener Zu benachrichtigende PlayStateListener.
	 * @param listListener Zu benachrichtigende ListListener
	 * @param settingListener Zu benachrichtigende SettingListener
	 * */
	void invoke(List<PlayStateListener> playStateListener, List<ListListener> listListener, List<SettingListener> settingListener);

	public static class CurrentTrackChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 0;
		protected final Track playedLast;
		protected final Track playingCurrent;
		protected final Reason reason;
		
		public CurrentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
		{
			this.playedLast = playedLast;
			this.playingCurrent = playingCurrent;
			this.reason = reason;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			synchronized(playStateListener)
			{
				for(final PlayStateListener listener : playStateListener)
					listener.currentTrackChanged(playedLast, playingCurrent, reason);
			}
		}
	}
	
	public static class PlayStateChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 1;
		protected final boolean playState;
		
		public PlayStateChanged(final boolean playState)
		{
			this.playState = playState;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final PlayStateListener listener : playStateListener)
				listener.playStateChanged(playState);
		}
	}
	
	public static class VolumeChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 2;
		protected final int volume;
		
		public VolumeChanged(final int volume)
		{
			this.volume = volume;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final PlayStateListener listener : playStateListener)
				listener.volumeChanged(volume);
		}
	}
	
	public static class ListAdded implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 3;
		protected final String listName;
		
		public ListAdded(final String listName)
		{
			this.listName = listName;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.listAdded(listName);
		}
	}
	
	public static class ListCommentChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 4;
		protected final String listName;
		protected final String newComment;
		
		public ListCommentChanged(final String listName, final String newComment)
		{
			this.listName = listName;
			this.newComment = newComment;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.listCommentChanged(listName, newComment);
		}
	}
	
	public static class ListPriorityChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 5;
		protected final String listName;
		protected final int newPriority;
		
		public ListPriorityChanged(final String listName, final int newPriority)
		{
			this.listName = listName;
			this.newPriority = newPriority;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.listPriorityChanged(listName, newPriority);
		}
	}
	
	public static class ListRemoved implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 6;
		protected final String listName;
		
		public ListRemoved(final String listName)
		{
			this.listName = listName;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.listRemoved(listName);
		}
	}
	
	public static class ListRenamed implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 7;
		protected final String oldName;
		protected final String newName;
		
		public ListRenamed(final String oldName, final String newName)
		{
			this.oldName = oldName;
			this.newName = newName;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.listRenamed(oldName, newName);
		}
	}
	
	public static class TrackAdded implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 8;
		protected final DbTrack track;
		protected final boolean eventsFollowing;
		
		public TrackAdded(final DbTrack track, final boolean eventsFollowing)
		{
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.trackAdded(track, eventsFollowing);
		}
	}
	
	public static class TrackChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 9;
		protected final DbTrack newTrack;
		protected final Track oldTrack;
		protected final boolean eventsFollowing;
		
		public TrackChanged(final DbTrack newTrack, final Track oldTrack, final boolean eventsFollowing)
		{
			this.newTrack = newTrack;
			this.oldTrack = oldTrack;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.trackChanged(newTrack, oldTrack, eventsFollowing);
		}
	}
	
	public static class TrackDeleted implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 10;
		protected final DbTrack track;
		protected final boolean eventsFollowing;
		
		public TrackDeleted(final DbTrack track, final boolean eventsFollowing)
		{
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.trackDeleted(track, eventsFollowing);
		}
	}
	
	public static class TrackInserted implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 11;
		protected final String listName;
		protected final int position;
		protected final DbTrack track;
		protected final boolean eventsFollowing;
		
		public TrackInserted(final String listName, final int position, final DbTrack track, final boolean eventsFollowing)
		{
			this.listName = listName;
			this.position = position;
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.trackInserted(listName, position, track, eventsFollowing);
		}
	}
	
	public static class TrackRemoved implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 11;
		protected final String listName;
		protected final int position;
		protected final boolean eventsFollowing;
		
		public TrackRemoved(final String listName, final int position, final boolean eventsFollowing)
		{
			this.listName = listName;
			this.position = position;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.trackRemoved(listName, position, eventsFollowing);
		}
	}
	
	public static class TracksSwaped implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 12;
		protected final String listName;
		protected final int positionA;
		protected final int positionB;
		protected final boolean eventsFollowing;
		
		public TracksSwaped(final String listName, final int positionA, final int positionB, final boolean eventsFollowing)
		{
			this.listName = listName;
			this.positionA = positionA;
			this.positionB = positionB;
			this.eventsFollowing = eventsFollowing;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final ListListener listener : listListener)
				listener.tracksSwaped(listName, positionA, positionB, eventsFollowing);
		}
	}
	
	public static class SettingChanged implements Event
	{
		private static final long serialVersionUID = 4914128956918507587L + 12;
		protected final String name;
		protected final String value;
		
		public SettingChanged(final String name, final String value)
		{
			this.name = name;
			this.value = value;
		}

		@Override
		public void invoke(final List<PlayStateListener> playStateListener, final List<ListListener> listListener, final List<SettingListener> settingListener)
		{
			for(final SettingListener listener : settingListener)
				listener.settingChanged(name, value);
		}
	}
}