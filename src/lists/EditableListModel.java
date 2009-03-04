package lists;

import common.ListException;
import common.Track;

public interface EditableListModel extends TrackListModel 
{
	/**F�gt einen Track am Ende der Liste ein.
	 * 
	 * @param track Track der eingef�gt wird.
	 * @throws ListException
	 */
	public void add(Track track) throws ListException;
	
	/**F�gt einen Tack an der angegebenen Position ein.
	 * 
	 * Nachfolgende Tracks werden nach unten verschoben.
	 * Wenn der Index gr��er ist, als die Liste, wird der Track am Ende der Liste eingef�gt.
	 * 
	 * @param index Position an der der Track eingef�gt wird.
	 * @param track Track der eingef�gt wird.
	 * @throws ListException
	 */
	public void add(int index, Track track) throws ListException;
	
	/**L�scht den Track mit dem angegebenen Index aus der Liste.
	 * 
	 * Wenn kein Track mit dem Index existiert, wird nichts gel�scht.
	 * 
	 * @param index Index des zu l�schenden Tracks.
	 * @throws ListException
	 */
	public void remove(int index) throws ListException;
	
	/**Verschiebt den angegebenen Track an eine andere Position.
	 * 
	 * Wenn eine Position nicht in die Liste passt, wird nichts verschoben.
	 * 
	 * @param oldIndex Position des Tracks der verschoben wird.
	 * @param newIndex Neue Position des Tracks.
	 * @throws ListException
	 */
	public void move(int oldIndex, int newIndex) throws ListException;
}
