package de.klierlinge.partydj.data.derby;

import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.lists.data.DbTrack;

/**DbTrack der von DerbyDb erstellt werden kann.
 * 
 * @author Eraser
 */
public class DerbyDbTrack extends DbTrack
{
	private static final long serialVersionUID = -6427376456888051168L;

	/**Erstellt einen neuen DbTrack mit den angegebenen Werten.
	 * @param index Index in der Hauptliste.
	 * @param path Pfad der Datei.
	 * @param name Angezeigter Name.
	 * @param duration Dauer in Sekunden.
	 * @param size Dateigröße in Byte.
	 * @param problem Problem mit dem Track.
	 * @param info Zusätzliche Info.
	 */
	protected DerbyDbTrack(final DerbyDB data, final int index, final String path, final String name, final double duration, final long size, final Problem problem, final String info)
	{
		super(data, index, path, name, duration, size, problem, info);
	}
	
	/**Erstellt einen DerbyDbTrack aus dem angegebenen Track.
	 * Index muss danach noch zugewiesen werden.
	 * 
	 * @param track Track aus dem die Daten kopiert werden.
	 */
	protected DerbyDbTrack(final DerbyDB data, final Track track)
	{
		super(data, track);
	}
}
