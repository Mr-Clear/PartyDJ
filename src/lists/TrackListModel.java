package lists;

import javax.swing.ListModel;
import common.Track;

/**
 * Ein ListModel, das Elemente vom Typ Track aufnimmt.
 * <br>Wird von PDJList verwendet.
 * 
 * @author Eraser
 * 
 * @see ListModel
 * @see gui.PDJList
 */
public interface TrackListModel extends ListModel
{
	/** Gibt den Track an der angegebenen Position zurück
	 * @param index Position.
	 * @return Track an der Position.*/
	public Track getElementAt(int index);
	
	/**Gibt die Position des Tracks in der Liste zurück
	 * @param track Track.
	 * @return Erste Position des Tracks.*/
	public int getIndex(Track track);
}
