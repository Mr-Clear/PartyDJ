package common;

import java.util.ArrayList;
import java.util.List;
import lists.TrackListModel;

/**Bietet Funktionen zum Schreiben von Playlists an.
 * 
 * @author Eraser
 */
public class PlaylistWriter
{
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(Iterable<Track> tracks, String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName).name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Iterable<Track> tracks, String fileName, Format format)
	{
		write(tracks, fileName, format.name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Iterable<Track> tracks, String fileName, String format)
	{
		List<Track> list = new ArrayList<Track>();
		for(Track track : tracks)
			list.add(track);
		write(list, fileName, format);
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(List<Track> tracks, String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName).name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(List<Track> tracks, String fileName, Format format)
	{
		write(tracks, fileName, format.name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(List<Track> tracks, String fileName, String format)
	{
		Track[] array = new Track[tracks.size()];
		tracks.toArray(array);
		write(array, fileName, format);
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(TrackListModel tracks, String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName).name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(TrackListModel tracks, String fileName, Format format)
	{
		write(tracks, fileName, format.name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(TrackListModel tracks, String fileName, String format)
	{
		Track[] array = new Track[tracks.getSize()];
		synchronized(tracks)
		{
			for(int i = 0; i < tracks.getSize(); i++)
				array[i] = tracks.getElementAt(i);
		}
		write(array, fileName, format);
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(Track[] tracks, String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName).name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Track[] tracks, String fileName, Format format)
	{
		write(tracks, fileName, format.name);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Track[] tracks, String fileName, String format)
	{
		throw new UnsupportedOperationException("common.PlaylistWriter.write(Track[], String, String) ist Noch nicht implementiert :(");
	}
	
	/** Gibt die unterstützten Formate zurück. */
	public static Format[] getFormats()
	{
		return new Format[]{
				new Format("M3U", ".m3u", "Einfache M3U Playlist", "Cp1252", 0),
				new Format("EXTM3U", ".m3u", "M3U Playlist mit zusätzlichen Informationen", "Cp1252", 5),
		};
	}
	
	/** Stellt fest, ob das angegebene Format unterstützt wird. */
	public static boolean isFormatSupported(String format)
	{
		for(Format supported : getFormats())
			if(supported.equals(format))
				return true;
		return false;
	}
	
	/** Gibt ein passendes Format für eine Dateinamenerweiterung zurück. */
	public static Format getFormatByFileName(String fileName)
	{
		Format ret = null;
		int priority = 0;
		for(Format f : getFormats())
		{
			if(fileName.toLowerCase().endsWith(f.extension.toLowerCase()))
				if(f.priority >= priority)
					ret = f;
		}
		return ret;
	}
	
	/** Format einer Playlist */
	public static class Format
	{
		/** Name des Formates. Nicht case sensitive. */
		final protected String name;
		/** Übliche Dateinamenerweiterung für das Format. */
		final protected String extension;
		/** Kurze Beschreibung des formates. */
		final protected String description;
		/** Codierung des Formats */
		final protected String encoding;
		/** Bei zwei Formaten mit gleicher Erweiterung wird das Format mit der größeren Priorität gewählt */
		final protected int priority;
		
		public Format(String formatName, String extension, String description, String encoding)
		{
			this(formatName, extension, description, encoding, 0);
		}
		
		public Format(String formatName, String extension, String description, String encoding, int priority)
		{
			this.name = formatName;
			this.extension = extension;
			this.description = description;
			this.encoding = encoding;
			this.priority = priority;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof Format)
				return name.equalsIgnoreCase(((Format)o).name);
			if(o instanceof String)
				return name.equalsIgnoreCase((String)o);
			return false;
		}
	}
}
