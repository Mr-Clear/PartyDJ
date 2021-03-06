package de.klierlinge.partydj.lists;

import java.util.List;
import javax.swing.ListModel;
import de.klierlinge.partydj.common.Track;

/**
 * Ein ListModel, das Elemente vom Typ Track aufnimmt.
 * <br>Wird von PDJList verwendet.
 * 
 * @author Eraser
 * 
 * @see ListModel
 * @see de.klierlinge.partydj.gui.PDJList
 */
public interface TrackListModel extends ListModel<Track>, Iterable<Track>
{
	/** Gibt den Track an der angegebenen Position zurück
	 * @param index Position.
	 * @return Track an der Position.*/
	@Override
	Track getElementAt(int index);
	
	/**Gibt die Position des Tracks in der Liste zurück
	 * @param track Track.
	 * @return Erste Position des Tracks.*/
	int getIndex(Track track);
	
	/**@return Die Daten im ListModel.*/
	List<Track> getList();
	
	/**@return Die Daten im ListModel.*/
	List<Track> getValues();
}
