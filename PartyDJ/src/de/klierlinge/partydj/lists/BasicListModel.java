package de.klierlinge.partydj.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.players.PlayStateListener;

/**
 * Stellt die grundlegenden Funktion einer TrackList dar.
 *
 * @author Eraser
 *
 * @see TrackListModel
 */
public class BasicListModel implements TrackListModel, PlayStateListener
{
	private static final Logger log = LoggerFactory.getLogger(BasicListModel.class);
	protected Controller controller;
	protected final Set<ListDataListener> dataListener = new HashSet<>();
	protected List<Track> list;

	public BasicListModel()
	{
		this(new ArrayList<Track>(0));
	}

	public BasicListModel(final List<Track> list)
	{
		this.list = list;

		controller = Controller.getInstance();
		controller.getPlayer().addPlayStateListener(this);
	}

	@Override
	public int getSize()
	{
		return list.size();
	}

	@Override
	public Track getElementAt(final int index)
	{
		synchronized(list)
		{
			if(index < list.size())
				return list.get(index);
			return null;
		}
	}

	@Override
	public void addListDataListener(final ListDataListener listener)
	{
		dataListener.add(listener);
	}

	@Override
	public void removeListDataListener(final ListDataListener listener)
	{
		dataListener.remove(listener);
	}

	@Override
	public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
	{
		synchronized(list)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if((playedLast != null && list.get(i) == playedLast) || (playingCurrent != null && list.get(i) == playingCurrent))
				{
					for(final ListDataListener listener : dataListener)
					{
						try
						{
							listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i));
						}
						catch (final Exception e)
						{
							log.error("Failed to notify ListDataListener: " + dataListener, e);
						}
					}
				}
			}
		}
	}

	@Override
	public int getIndex(final Track track)
	{
		return list.indexOf(track);
	}

	@Override
	public void playStateChanged(final boolean playState) { /* ignore */ }

	@Override
	public void volumeChanged(final int volume) { /* ignore */ }

	@Override
	public List<Track> getList()
	{
		return java.util.Collections.unmodifiableList(list);
	}

	@Override
	public List<Track> getValues()
	{
		return Collections.unmodifiableList(list);
	}

	@Override
	public Iterator<Track> iterator()
	{
		return list.iterator();
	}
}
