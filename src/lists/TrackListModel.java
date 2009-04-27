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
	/** Gibt den Track an der angegebenen Position zur�ck*/
	public Track getElementAt(int index);
}
