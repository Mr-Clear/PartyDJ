package lists;

import common.Track;

/**
 * Erweitert das TrackListModel um Funktionen zur Manipulation der Listen.
 *  
 * @author Eraser
 *
 * @see TrackListModel
 */
public interface EditableListModel extends TrackListModel
{
	/**Fügt einen Track am Ende der Liste ein.
	 * 
	 * @param track Track der eingefügt wird.
	 * @throws ListException Wenn beim Bearbeiten der Liste ein Fehler auftritt.
	 *                       Wird nicht von allen ListModels ausgelöst.
	 */
	void add(Track track) throws ListException;
	
	/**Fügt einen Tack an der angegebenen Position ein.
	 * 
	 * Nachfolgende Tracks werden nach unten verschoben.
	 * Wenn der Index größer ist, als die Liste, wird der Track am Ende der Liste eingefügt.
	 * 
	 * @param index Position an der der Track eingefügt wird.
	 * @param track Track der eingefügt wird.
	 * @throws ListException Wenn beim Bearbeiten der Liste ein Fehler auftritt.
	 *                       Wird nicht von allen ListModels ausgelöst.
	 */
	void add(int index, Track track) throws ListException;
	
	/**Löscht den Track mit dem angegebenen Index aus der Liste.
	 * 
	 * Wenn kein Track mit dem Index existiert, wird nichts gelöscht.
	 * 
	 * @param index Index des zu löschenden Tracks.
	 * @throws ListException Wenn beim Bearbeiten der Liste ein Fehler auftritt.
	 *                       Wird nicht von allen ListModels ausgelöst.
	 */
	void remove(int index) throws ListException;
	
	/**Verschiebt den angegebenen Track an eine andere Position.
	 * 
	 * Wenn eine Position nicht in die Liste passt, wird nichts verschoben.
	 * 
	 * @param oldIndex Position des Tracks der verschoben wird.
	 * @param newIndex Neue Position des Tracks.
	 * @throws ListException Wenn beim Bearbeiten der Liste ein Fehler auftritt.
	 *                       Wird nicht von allen ListModels ausgelöst.
	 */
	void move(int oldIndex, int newIndex) throws ListException;
	
	/**Vertauscht die Tracks an den Positionan A und B.
	 * 
	 * @param indexA Index des einen Tracks.
	 * @param indexB Index des anderen Tracks.
	 * @throws ListException Wenn beim Bearbeiten der Liste ein Fehler auftritt.
	 *                       Wird nicht von allen ListModels ausgelöst.
	 */
	void swap(int indexA, int indexB) throws ListException;
}
