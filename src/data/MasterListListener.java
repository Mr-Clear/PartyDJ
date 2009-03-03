package data;

import common.Track;

/**Empf�ngt �nderungen in der Hauptliste.
 * 
 * @author Eraser
 */
public interface MasterListListener
{
	/**Ein Track wurde hinzugef�gt.
	 * 
	 * @param track Hunzugef�gter Track.
	 */
	void trackAdded (Track track);
	/**Ein Track wurde bearbeitet.
	 * 
	 * @param track Bearbeiteter Track.
	 */
	void trackChanged (Track track);
	/**Ein Track wurde gel�scht.
	 * 
	 * @param track Gel�schter Track.
	 */
	void trackDeleted (Track track);
}
