package data;

import common.Track;

public abstract class ListAdapter implements ListListener
{
	public void trackAdded(Track track){}
	public void trackChanged(Track track){}
	public void trackDeleted(Track track){}
	
	public void listAdded(String listName){}
	public void listRemoved(String listName){}
	public void listCommentChanged(String listName, String newComment){}
	public void listRenamed(String oldName, String newName){}
	
	public void trackInserted(String listName, int position, Track track){}
	public void trackRemoved(String listName, int position){}
	public void tracksSwaped(String listName, int positionA, int positionB){}
}
