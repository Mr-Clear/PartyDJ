package network.remote;

import common.Track;

import data.IData;
import data.ListListener;
import data.SettingException;
import data.SettingListener;
import data.SortOrder;

import lists.ListException;
import lists.data.DbTrack;
import lists.data.DbTrack.TrackElement;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NetworkData extends NetworkInterface implements IData
{
	private final List<ListListener> listListener = new ArrayList<>();
	private final List<SettingListener> settingListener = new ArrayList<>();
	
	public NetworkData(final ObjectOutputStream oos, final Map<Long, Thread> invocationThreads, Map<Long, Serializable> invocationAnswers)
	{
		super(oos, invocationThreads, invocationAnswers);
	}

	@Override
	public void addList(String listName) throws ListException
	{
		addList(listName, null);
	}

	@Override
	public void addList(String listName, String description) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.AddList(invocationId, listName, description));

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void addListListener(ListListener listener)
	{
		getListListener().add(listener);
	}

	@Override
	public void addSettingListener(SettingListener listener)
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
	public DbTrack addTrack(Track track, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.AddTrack(invocationId, track, eventsFollowing));

		Serializable answer = waitForAnswer(invocationId);
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

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void deleteTrack(DbTrack track, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.DeleteTrack(invocationId, track, eventsFollowing));

		Serializable answer = waitForAnswer(invocationId);
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
	public String getListDescription(String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetListDescription(invocationId, listName));

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return (String)answer;
	}

	@Override
	public int getListPriority(String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetListPriority(invocationId, listName));

		Serializable answer = waitForAnswer(invocationId);
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

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return Arrays.asList((String[])answer);
	}

	@Override
	@Deprecated
	public DbTrack getTrack(String path, boolean autoCreate) throws ListException
	{
		throw new UnsupportedOperationException("Pfadangaben über Fernsteuerung nicht zuverlässig.");
	}

	@Override
	public DbTrack getTrack(int index)
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.GetTrack(invocationId, index));

		Serializable answer = waitForAnswer(invocationId);
		return (DbTrack)answer;
	}

	@Override
	public void insertTrack(String listName, DbTrack track, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.InsertTrack(invocationId, listName, track, eventsFollowing));

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void insertTrackAt(String listName, DbTrack track, int trackPosition, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.InsertTrackAt(invocationId, listName, track, trackPosition, eventsFollowing));

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	@Deprecated
	public boolean isInDb(String trackPath) throws ListException
	{
		throw new UnsupportedOperationException("Pfadangaben über Fernsteuerung nicht zuverlässig.");
	}

	@Override
	public List<DbTrack> readList(String listName, String searchString, SortOrder order) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.ReadList(invocationId, listName, searchString, order));

		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
		return Arrays.asList((DbTrack[])answer);
	}

	@Override
	public String readSetting(String name) throws SettingException
	{
		return readSetting(name, null);
	}

	@Override
	public String readSetting(String name, String defaultValue) throws SettingException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.ReadSetting(invocationId, name, defaultValue));

		Serializable answer = waitForAnswer(invocationId);
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
	public void removeList(String listName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RemoveList(invocationId, listName));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void removeListListener(ListListener listener)
	{
		getListListener().remove(listener);
	}

	@Override
	public void removeSettingListener(SettingListener listener)
	{
		getSettingListener().remove(listener);
	}

	@Override
	public void removeTrack(String listName, int trackPosition, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RemoveTrack(invocationId, listName, trackPosition, eventsFollowing));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void renameList(String oldName, String newName) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.RenameList(invocationId, oldName, newName));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void setListDescription(String listName, String description) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SetListDescription(invocationId, listName, description));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void setListPriority(String listName, int priority) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SetListPriority(invocationId, listName, priority));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void swapTrack(String listName, int positionA, int positionB, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.SwapTrack(invocationId, listName, positionA, positionB, eventsFollowing));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void updateTrack(DbTrack track, TrackElement element, boolean eventsFollowing) throws ListException
	{
		final long invocationId = generateInvocationId();
		invocationThreads.put(invocationId, Thread.currentThread());
		sendInvocation(new Invocation.UpdateTrack(invocationId, track, element, eventsFollowing));
		
		Serializable answer = waitForAnswer(invocationId);
		if(answer instanceof ListException)
			throw (ListException)answer;
	}

	@Override
	public void writeSetting(String name, String value) throws SettingException
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
