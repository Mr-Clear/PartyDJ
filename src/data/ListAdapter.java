package data;

import common.DbTrack;

/**
 * Empfängt Änderungen in den Listen.
 * <br>Alle Funktionen sind mit leerem Rumpf implementiert.
 * 
 * @author Eraser
 */

public abstract class ListAdapter implements ListListener
{
	public void trackAdded(DbTrack track){}
	public void trackChanged(DbTrack track){}
	public void trackDeleted(DbTrack track){}
	
	public void listAdded(String listName){}
	public void listRemoved(String listName){}
	public void listCommentChanged(String listName, String newComment){}
	public void listRenamed(String oldName, String newName){}
	
	public void listPriorityChanged(String listName, int newPriority){}
	
	public void trackInserted(String listName, int position, DbTrack track){}
	public void trackRemoved(String listName, int position){}
	public void tracksSwaped(String listName, int positionA, int positionB){}
}
