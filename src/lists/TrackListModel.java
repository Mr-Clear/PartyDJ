package lists;

import javax.swing.ListModel;
import common.Track;

public interface TrackListModel extends ListModel
{
	/** Gibt den Track an der angegebenen Position zurück*/
	public Track getElementAt(int index);
}
