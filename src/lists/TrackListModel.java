package lists;

import javax.swing.ListModel;
import common.Track;

public interface TrackListModel extends ListModel
{
	/** Gibt den Track an der angegebenen Position zur�ck*/
	public Track getElementAt(int index);
}
