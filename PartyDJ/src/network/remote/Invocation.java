package network.remote;

import basics.Controller;
import common.Track;
import data.SettingException;
import data.SortOrder;
import lists.ListException;
import lists.data.DbTrack;
import lists.data.DbTrack.TrackElement;
import players.PlayerException;
import java.io.File;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public interface Invocation extends Serializable
{
	void invoke(RemoteServer server, Socket client);
	
	/* Einerignisse von IPlayer. */
	public static class CheckPlayable implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 0;
		final long invocationId;
		final Track track;

		public CheckPlayable(final long invocationId, final Track track)
		{
			this.invocationId = invocationId;
			this.track = track;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getPlayer().checkPlayable(track), client);
		}
	}
	
	public static class Dispose implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 1;

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().dispose();
		}
	}
	
	public static class FadeIn implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 2;

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().fadeIn();
		}
	}
	
	public static class FadeInOut implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 3;

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().fadeInOut();
		}
	}	
	
	public static class FadeOut implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 4;

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().fadeOut();
		}
	}
	
	public static class GetCurrentTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 5;
		final long invocationId;

		public GetCurrentTrack(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getCurrentTrack(), client);
		}
	}
	
	public static class GetDuration implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 6;
		final long invocationId;
		final Track track;
		final String filePath;
		final File file;

		public GetDuration(final long invocationId)
		{
			this.invocationId = invocationId;
			this.track = null;
			this.filePath = null;
			this.file = null;
		}
		
		public GetDuration(final long invocationId, final Track track)
		{
			this.invocationId = invocationId;
			this.track = track;
			this.filePath = null;
			this.file = null;
		}
		
		public GetDuration(final long invocationId, final String filePath)
		{
			this.invocationId = invocationId;
			this.track = null;
			this.filePath = filePath;
			this.file = null;
		}
		
		public GetDuration(final long invocationId, final File file)
		{
			this.invocationId = invocationId;
			this.track = null;
			this.filePath = null;
			this.file = file;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			if(track != null)
			{
				try
				{
					server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getDuration(track), client);
				}
				catch(PlayerException e)
				{
					server.sendAnswer(invocationId, e, client);
				}
			}
			else if(filePath != null)
			{
				try
				{
					server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getDuration(filePath), client);
				}
				catch(PlayerException e)
				{
					server.sendAnswer(invocationId, e, client);
				}
			}
			else if(file != null)
			{
				try
				{
					server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getDuration(file), client);
				}
				catch(PlayerException e)
				{
					server.sendAnswer(invocationId, e, client);
				}
			}
			else
			{
				server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getDuration(), client);
			}
		}
	}
	
	public static class GetPlayState implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 7;
		final long invocationId;

		public GetPlayState(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getPlayState(), client);
		}
	}
	
	public static class GetPosition implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 8;
		final long invocationId;

		public GetPosition(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getPosition(), client);
		}
	}
	
	public static class GetVolume implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 9;
		final long invocationId;

		public GetVolume(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getPlayer().getVolume(), client);
		}
	}
	
	public static class Load implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 10;
		final long invocationId;
		final Track track;
		
		public Load(final long invocationId, final Track track)
		{
			this.invocationId = invocationId;
			this.track = track;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getPlayer().load(track);
				server.sendAnswer(invocationId, null, client);
			}
			catch(PlayerException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class Pause implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 11;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().pause();
		}
	}
	
	public static class Play implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 12;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().play();
		}
	}
	
	public static class PlayNext implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 13;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().playNext();
		}
	}
	
	public static class PlayPause implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 14;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().playPause();
		}
	}
	
	public static class PlayPrevious implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 15;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().playPrevious();
		}
	}
	
	public static class SetPosition implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 16;
		final double seconds;
		final boolean autoPlaySet;
		final boolean autoPlay;
				
		public SetPosition(final double seconds, final boolean autoPlay, final boolean autoPlaySet)
		{
			this.seconds = seconds;
			this.autoPlay = autoPlay;
			this.autoPlaySet = autoPlaySet;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			if(autoPlaySet)
				Controller.getInstance().getPlayer().setPosition(seconds, autoPlay);
			else
				Controller.getInstance().getPlayer().setPosition(seconds);
		}
	}
	
	public static class SetVolume implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 17;
		final int volume;
		
		public SetVolume(final int volume)
		{
			this.volume = volume;
		}

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().setVolume(volume);
		}
	}
	
	public static class Start implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 18;
		final long invocationId;
		final Track track;
		
		public Start()
		{
			this.invocationId = 0;
			this.track = null;
		}
		
		public Start(final long invocationId, final Track track)
		{
			this.invocationId = invocationId;
			this.track = track;
		}

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			if(track != null)
			{
				try
				{
					Controller.getInstance().getPlayer().start(track);
					server.sendAnswer(invocationId, null, client);
				}
				catch(PlayerException e)
				{
					server.sendAnswer(invocationId, e, client);
				}
			}
			else
			{
				Controller.getInstance().getPlayer().start();
			}
		}
	}
	
	public static class Stop implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 19;
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			Controller.getInstance().getPlayer().stop();
		}
	}
	
	/* Einerignisse von IData. */
	public static class AddList implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 20;
		final long invocationId;
		final String listName;
		final String description;
			
		public AddList(final long invocationId, final String listName, final String description)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.description = description;
		}

		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
				try
				{
					if(description == null)
						Controller.getInstance().getData().addList(listName);
					else
						Controller.getInstance().getData().addList(listName, description);
					server.sendAnswer(invocationId, null, client);
				}
				catch(ListException e)
				{
					server.sendAnswer(invocationId, e, client);
				}
		}
	}
	
	public static class AddTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 21;
		final long invocationId;
		final Track track;
		final boolean eventsFollowing;
		
		public AddTrack(final long invocationId, final Track track, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				server.sendAnswer(invocationId, Controller.getInstance().getData().addTrack(track, eventsFollowing), client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class Close implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 22;
		final long invocationId;
		
		public Close(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().close();
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class DeleteTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 23;
		final long invocationId;
		final DbTrack track;
		final boolean eventsFollowing;
		
		public DeleteTrack(final long invocationId, final DbTrack track, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().deleteTrack(track, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class GetDbPath implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 24;
		final long invocationId;
		
		public GetDbPath(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getData().getDbPath(), client);
		}
	}
	
	public static class GetListDescription implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 25;
		final long invocationId;
		final String listName;
		
		public GetListDescription(final long invocationId, final String listName)
		{
			this.invocationId = invocationId;
			this.listName = listName;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				server.sendAnswer(invocationId, Controller.getInstance().getData().getListDescription(listName), client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class GetListPriority implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 26;
		final long invocationId;
		final String listName;
		
		public GetListPriority(final long invocationId, final String listName)
		{
			this.invocationId = invocationId;
			this.listName = listName;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				server.sendAnswer(invocationId, Controller.getInstance().getData().getListPriority(listName), client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class GetLists implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 27;
		final long invocationId;
		
		public GetLists(final long invocationId)
		{
			this.invocationId = invocationId;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				List<String> list = Controller.getInstance().getData().getLists();
				String[] answer = new String[list.size()];
				list.toArray(answer);
				server.sendAnswer(invocationId, answer, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class GetTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 28;
		final long invocationId;
		final int index;
		
		public GetTrack(final long invocationId, final int index)
		{
			this.invocationId = invocationId;
			this.index = index;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			server.sendAnswer(invocationId, Controller.getInstance().getData().getTrack(index), client);
		}
	}
	
	public static class InsertTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 29;
		final long invocationId;
		final String listName;
		final DbTrack track;
		final boolean eventsFollowing;
		
		public InsertTrack(final long invocationId, final String listName, final DbTrack track, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.track = track;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().insertTrack(listName, track, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class InsertTrackAt implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 30;
		final long invocationId;
		final String listName;
		final DbTrack track;
		final int trackPosition;
		final boolean eventsFollowing;
		
		public InsertTrackAt(final long invocationId, final String listName, final DbTrack track, final int trackPosition, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.track = track;
			this.trackPosition = trackPosition;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().insertTrackAt(listName, track, trackPosition, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class ReadList implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 31;
		final long invocationId;
		final String listName;
		final String searchString;
		final SortOrder order;
		
		public ReadList(final long invocationId, final String listName, final String searchString, final SortOrder order)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.searchString = searchString;
			this.order = order;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				List<? extends DbTrack> list = Controller.getInstance().getData().readList(listName, searchString, order);
				DbTrack[] answer = new DbTrack[list.size()];
				list.toArray(answer);
				server.sendAnswer(invocationId, answer, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class ReadSetting implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 32;
		final long invocationId;
		final String name;
		final String defaultValue;
		
		public ReadSetting(final long invocationId, final String name, final String defaultValue)
		{
			this.invocationId = invocationId;
			this.name = name;
			this.defaultValue = defaultValue;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				
				if(defaultValue == null)
					server.sendAnswer(invocationId, Controller.getInstance().getData().readSetting(name), client);
				else
					server.sendAnswer(invocationId, Controller.getInstance().getData().readSetting(name, defaultValue), client);
			}
			catch(SettingException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class RemoveList implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 33;
		final long invocationId;
		final String listName;
		
		public RemoveList(final long invocationId, final String listName)
		{
			this.invocationId = invocationId;
			this.listName = listName;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().removeList(listName);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class RemoveTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 34;
		final long invocationId;
		final String listName;
		final int trackPosition;
		final boolean eventsFollowing;
		
		public RemoveTrack(final long invocationId, final String listName, final int trackPosition, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.trackPosition = trackPosition;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().removeTrack(listName, trackPosition, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class RenameList implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 35;
		final long invocationId;
		final String oldName;
		final String newName;
		
		public RenameList(final long invocationId, final String oldName, final String newName)
		{
			this.invocationId = invocationId;
			this.oldName = oldName;
			this.newName = newName;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().renameList(oldName, newName);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class SetListDescription implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 36;
		final long invocationId;
		final String listName;
		final String description;
		
		public SetListDescription(final long invocationId, final String listName, final String description)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.description = description;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().setListDescription(listName, description);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class SetListPriority implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 37;
		final long invocationId;
		final String listName;
		final int priority;
		
		public SetListPriority(final long invocationId, final String listName, final int priority)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.priority = priority;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().setListPriority(listName, priority);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class SwapTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 38;
		final long invocationId;
		final String listName;
		final int positionA;
		final int positionB;
		final boolean eventsFollowing;
		
		public SwapTrack(final long invocationId, final String listName, final int positionA, final int positionB, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.listName = listName;
			this.positionA = positionA;
			this.positionB = positionB;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().swapTrack(listName, positionA, positionB, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class UpdateTrack implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 39;
		final long invocationId;
		final DbTrack track;
		final TrackElement element;
		final boolean eventsFollowing;
		
		public UpdateTrack(final long invocationId, final DbTrack track, final TrackElement element, final boolean eventsFollowing)
		{
			this.invocationId = invocationId;
			this.track = track;
			this.element = element;
			this.eventsFollowing = eventsFollowing;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().updateTrack(track, element, eventsFollowing);
				server.sendAnswer(invocationId, null, client);
			}
			catch(ListException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
	
	public static class WriteSetting implements Invocation
	{
		private static final long serialVersionUID = 5397835866330462568L + 40;
		final long invocationId;
		final String name;
		final String value;
		
		public WriteSetting(final long invocationId, final String name, final String value)
		{
			this.invocationId = invocationId;
			this.name = name;
			this.value = value;
		}
		
		@Override
		public void invoke(final RemoteServer server, final Socket client)
		{
			try
			{
				Controller.getInstance().getData().writeSetting(name, value);
				server.sendAnswer(invocationId, null, client);
			}
			catch(SettingException e)
			{
				server.sendAnswer(invocationId, e, client);
			}
		}
	}
}
