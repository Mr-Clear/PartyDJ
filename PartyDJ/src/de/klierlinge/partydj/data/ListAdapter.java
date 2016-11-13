package de.klierlinge.partydj.data;

import de.klierlinge.partydj.lists.data.DbTrack;

/**
 * Empfängt Änderungen in den Listen.
 * <br>Alle Funktionen sind mit leerem Rumpf implementiert.
 * 
 * @author Eraser
 */

public abstract class ListAdapter implements ListListener
{
	@Override
	public void trackAdded(final DbTrack track, final boolean eventsFollowing) { /* Empty default implementation. */ }
	@Override
	public void trackChanged(final DbTrack newTrack, final de.klierlinge.partydj.common.Track oldTrack, final boolean eventsFollowing) { /* Empty default implementation. */ }
	@Override
	public void trackDeleted(final DbTrack track, final boolean eventsFollowing) { /* Empty default implementation. */ }
	
	@Override
	public void listAdded(final String listName) { /* Empty default implementation. */ }
	@Override
	public void listRemoved(final String listName) { /* Empty default implementation. */ }
	@Override
	public void listCommentChanged(final String listName, final String newComment) { /* Empty default implementation. */ }
	@Override
	public void listRenamed(final String oldName, final String newName) { /* Empty default implementation. */ }
	
	@Override
	public void listPriorityChanged(final String listName, final int newPriority) { /* Empty default implementation. */ }
	
	@Override
	public void trackInserted(final String listName, final int position, final DbTrack track, final boolean eventsFollowing) { /* Empty default implementation. */ }
	@Override
	public void trackRemoved(final String listName, final int position, final boolean eventsFollowing) { /* Empty default implementation. */ }
	@Override
	public void tracksSwaped(final String listName, final int positionA, final int positionB, final boolean eventsFollowing) { /* Empty default implementation. */ }
}
