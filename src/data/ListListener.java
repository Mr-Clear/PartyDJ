package data;

import common.Track;

/**Empf�ngt �nderungen in den Listen.
 * 
 * @author Eraser
 */
public interface ListListener
{
	/**Ein Track wurde der Hauptliste hinzugef�gt.
	 * 
	 * @param track Hinzugef�gter Track.
	 */
	void trackAdded (Track track);
	/**Ein Track wurde bearbeitet.
	 * 
	 * @param track Bearbeiteter Track.
	 */
	void trackChanged (Track track);
	/**Ein Track wurde aus der Hauptliste gel�scht.
	 * 
	 * @param track Gel�schter Track.
	 */
	void trackDeleted (Track track);
	
	/**Eine Liste wurde erstellt.
	 * 
	 * @param listName Name der erstellten Liste.
	 */
	void listAdded(String listName);
	/**Eine Liste wurde gel�scht.
	 * 
	 * @param listName Name der gel�schten Liste.
	 */
	void listRemoved(String listName);
	/**Die Beschreibung einer Liste wurde ge�ndert. 
	 * 
	 * @param listName Name der Liste.
	 * @param newComment Neue Beschreibung der Liste.
	 */
	void listCommentChanged(String listName, String newComment);
	/**Eine Liste wurde umbenannt.
	 * 
	 * @param oldName Name der Liste bevor sie umbenannt wurde.
	 * @param newName Jetziger Name der Liste.
	 */
	void listRenamed(String oldName, String newName);
	
	/**Die Priorit�t einer Liste wurde ge�ndert.
	 * 
	 * @param listName Name der Liste.
	 * @param newPriority Neue Priorit�t der Liste.
	 */
	void listPriorityChanged(String listName, int newPriority);
	
	/**Track wurde in einer Liste eingef�gt.
	 * 
	 * @param listName Name der Liste.
	 * @param position Position in der Liste, an der der Track eingef�gt wurde.
	 * @param track Track der eingef�gt wurde.
	 */
	void trackInserted(String listName, int position, Track track);
	/**Ein Track wurde aus einer Liste entfernt.
	 * 
	 * @param listName Name der Liste.
	 * @param position Position in der Liste, an der der Track stand.
	 */
	void trackRemoved(String listName, int position);
	/**Zwei Tracks wurden innerhalb einer Liste vertauscht.
	 * 
	 * @param listName Name der Liste in der die Tracks sind.
	 * @param positionA Position des einen Tracks.
	 * @param positionB Position des anderen Tracks.
	 */
	void tracksSwaped(String listName, int positionA, int positionB);
}
