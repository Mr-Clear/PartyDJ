package data;

import common.DbTrack;

/**
 * Empfängt Änderungen in den Listen.
 * 
 * @author Eraser
 */
public interface ListListener
{
	/**Ein Track wurde der Hauptliste hinzugefügt.
	 * 
	 * @param track Hinzugefügter Track.
	 */
	void trackAdded (DbTrack track);
	/**Ein Track wurde bearbeitet.
	 * 
	 * @param track Bearbeiteter Track.
	 */
	void trackChanged (DbTrack track);
	/**Ein Track wurde aus der Hauptliste gelöscht.
	 * 
	 * @param track Gelöschter Track.
	 */
	void trackDeleted (DbTrack track);
	
	/**Eine Liste wurde erstellt.
	 * 
	 * @param listName Name der erstellten Liste.
	 */
	void listAdded(String listName);
	/**Eine Liste wurde gelöscht.
	 * 
	 * @param listName Name der gelöschten Liste.
	 */
	void listRemoved(String listName);
	/**Die Beschreibung einer Liste wurde geändert. 
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
	
	/**Die Priorität einer Liste wurde geändert.
	 * 
	 * @param listName Name der Liste.
	 * @param newPriority Neue Priorität der Liste.
	 */
	void listPriorityChanged(String listName, int newPriority);
	
	/**Track wurde in einer Liste eingefügt.
	 * 
	 * @param listName Name der Liste.
	 * @param position Position in der Liste, an der der Track eingefügt wurde.
	 * @param track Track der eingefügt wurde.
	 */
	void trackInserted(String listName, int position, DbTrack track);
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
