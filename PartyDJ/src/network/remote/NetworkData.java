package network.remote;

import common.Track;
import data.IData;
import data.ListListener;
import data.SettingException;
import data.SettingListener;
import data.SortOrder;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lists.ListException;
import lists.data.DbTrack;
import lists.data.DbTrack.TrackElement;

public class NetworkData extends NetworkInterface implements IData
{
	private final List<ListListener> listListener = new ArrayList<>();
	private final List<SettingListener> settingListener = new ArrayList<>();
	
	public NetworkData(final ObjectOutputStream oos, final Map<Long, Thread> invocationThreads, final Map<Long, Serializable> invocationAnswers)
	{
		super(oos, invocationThreads, invocationAnswers);
	}

	@Override
	public void addList(final String listName) throws ListException
	{
		addList(listName, null);
	}

	@Override
	public void addList(final String listName, final String description) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.AddList(invocationId, listName, description));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void addListListener(final ListListener listener)
	{
		getListListener().add(listener);
	}

	@Override
	public void addSettingListener(final SettingListener listener)
	{
		getSettingListener().add(listener);
	}

	public List<ListListener> getListListener()
	{
		return listListener;
	}

	public List<SettingListener> getSettingListener()
	{
		return settingListener;
	}

	@Override
	public DbTrack addTrack(final Track track, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.AddTrack(invocationId, track, eventsFollowing));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return (DbTrack)answer;
	}

	@Override
	public void close() throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.Close(invocationId));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void deleteTrack(final DbTrack track, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.DeleteTrack(invocationId, track, eventsFollowing));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public String getDbPath()
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetDbPath(invocationId));

		return (String)waitForAnswer(invocationId);
	}

	@Override
	public String getListDescription(final String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetListDescription(invocationId, listName));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return (String)answer;
	}

	@Override
	public int getListPriority(final String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetListPriority(invocationId, listName));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return (Integer)answer;
	}

	@Override
	public List<String> getLists() throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetLists(invocationId));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return Arrays.asList((String[])answer);
	}

	@Override
	@Deprecated
	public DbTrack getTrack(final String path, final boolean autoCreate) throws ListException
	{
		throw new UnsupportedOperationException("Pfadangaben über Fernsteuerung nicht zuverlässig.");
	}

	@Override
	public DbTrack getTrack(final int index)
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetTrack(invocationId, index));

		final Serializable answer = waitForAnswer(invocationId);
		return (DbTrack)answer;
	}

	@Override
	public void insertTrack(final String listName, final DbTrack track, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.InsertTrack(invocationId, listName, track, eventsFollowing));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void insertTrackAt(final String listName, final DbTrack track, final int trackPosition, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.InsertTrackAt(invocationId, listName, track, trackPosition, eventsFollowing));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	@Deprecated
	public boolean isInDb(final String trackPath) throws ListException
	{
		throw new UnsupportedOperationException("Pfadangaben über Fernsteuerung nicht zuverlässig.");
	}

	@Override
	public List<DbTrack> readList(final String listName, final String searchString, final SortOrder order) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.ReadList(invocationId, listName, searchString, order));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return Arrays.asList((DbTrack[])answer);
	}

	@Override
	public String readSetting(final String name) throws SettingException
	{
		return readSetting(name, null);
	}

	@Override
	public String readSetting(final String name, final String defaultValue) throws SettingException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.ReadSetting(invocationId, name, defaultValue));

		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof SettingException)
			throw (SettingException)answer;
		return (String)answer;
	}
	
	@Override
	public Map<String, String> readAllSettings() throws SettingException
	{
	    return null;
	}

	@Override
	public void removeList(final String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RemoveList(invocationId, listName));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void removeListListener(final ListListener listener)
	{
		getListListener().remove(listener);
	}

	@Override
	public void removeSettingListener(final SettingListener listener)
	{
		getSettingListener().remove(listener);
	}

	@Override
	public void removeTrack(final String listName, final int trackPosition, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RemoveTrack(invocationId, listName, trackPosition, eventsFollowing));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void renameList(final String oldName, final String newName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RenameList(invocationId, oldName, newName));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void setListDescription(final String listName, final String description) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SetListDescription(invocationId, listName, description));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void setListPriority(final String listName, final int priority) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SetListPriority(invocationId, listName, priority));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void swapTrack(final String listName, final int positionA, final int positionB, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SwapTrack(invocationId, listName, positionA, positionB, eventsFollowing));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void updateTrack(final DbTrack track, final TrackElement element, final boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.UpdateTrack(invocationId, track, element, eventsFollowing));
		
		final Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void writeSetting(final String name, final String value) throws SettingException
	{
//		final long invocationId = rnd.nextLong();
//		invocationThreads.put(invocationId, Thread.currentThread());
//		sendInvocation(new Invocation.WriteSetting(invocationId, name, value));
//		
//		Serializable answer = waitForAnswer(invocationId);
//		if(answer instanceof SettingException)
//			throw (SettingException)answer;
		//TODO Was machen
	}
}
