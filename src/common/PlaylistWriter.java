package common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import basics.Controller;
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
		write(IterableToArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Iterable<Track> tracks, String fileName, Format format)
	{
		write(IterableToArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Iterable<Track> tracks, String fileName, String format)
	{
		write(IterableToArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(List<Track> tracks, String fileName)
	{
		write(ListToArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(List<Track> tracks, String fileName, Format format)
	{
		write(ListToArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(List<Track> tracks, String fileName, String format)
	{
		write(ListToArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(TrackListModel tracks, String fileName)
	{
		write(listModelToArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(TrackListModel tracks, String fileName, Format format)
	{
		write(listModelToArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(TrackListModel tracks, String fileName, String format)
	{
		write(listModelToArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(Track[] tracks, String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Track[] tracks, String fileName, String format)
	{
		write(tracks, fileName, getFormatByName(format));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(Track[] tracks, String fileName, Format format)
	{
		try
		{
			OutputStream fos = new FileOutputStream(fileName);
			write(tracks, fos, format);
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, null, e, "Kann Playlist-Datei nicht erstellen.");
		}
		catch (IOException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, null, e, "Fehler bei schreiben in Playlist-Datei.");
		}		
	}
	
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(Iterable<Track> tracks, OutputStream stream, Format format) throws IOException
	{
		write(IterableToArray(tracks), stream, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(Iterable<Track> tracks, OutputStream stream, String format) throws IOException
	{
		write(IterableToArray(tracks), stream, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(List<Track> tracks, OutputStream stream, Format format) throws IOException
	{
		write(ListToArray(tracks), stream, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(List<Track> tracks, OutputStream stream, String format) throws IOException
	{
		write(ListToArray(tracks), stream, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(TrackListModel tracks, OutputStream stream, Format format) throws IOException
	{
		write(listModelToArray(tracks), stream, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(TrackListModel tracks, OutputStream stream, String format) throws IOException
	{
		write(listModelToArray(tracks), stream, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(Track[] tracks, OutputStream stream, String format) throws IOException
	{
		write(tracks, stream, getFormatByName(format));
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(Track[] tracks, OutputStream stream, Format format) throws IOException
	{
		if(format.equals("M3U") || format.equals("M3U8"))
			writeM3U(tracks, stream, false, format.encoding);
		else if(format.equals("EXTM3U") || format.equals("EXTM3U8"))
			writeM3U(tracks, stream, true, format.encoding);
		else if(format.equals("PLS"))
			writePls(tracks, stream);
		else
			throw new IllegalArgumentException("Format wird nicht unterstützt: " + format);		
	}
	

	protected static Track[] listModelToArray(TrackListModel listModel)
	{
		Track[] array = new Track[listModel.getSize()];
		synchronized(listModel)
		{
			for(int i = 0; i < listModel.getSize(); i++)
				array[i] = listModel.getElementAt(i);
		}
		return array;
	}
	protected static Track[] IterableToArray(Iterable<Track> iterable)
	{
		List<Track> list = new ArrayList<Track>();
		for(Track track : iterable)
			list.add(track);
		return ListToArray(list);
	}
	protected static Track[] ListToArray(List<Track> list)
	{
		Track[] array = new Track[list.size()];
		list.toArray(array);
		return array;
	}
	
	/** Gibt die unterstützten Formate zurück. 
	 * @return Array mit allen unterstützen Formaten.*/
	public static Format[] getFormats()
	{
		return new Format[]{
				new Format("M3U", ".m3u", "Einfache M3U Playlist", "Cp1252", 0),
				new Format("EXTM3U", ".m3u", "M3U Playlist mit zusätzlichen Informationen", "Cp1252", 5),
				new Format("M3U8", ".m3u8", "Einfache M3U Playlist im Format UTF-8", "UTF-8", 0),
				new Format("EXTM3U8", ".m3u8", "M3U Playlist mit zusätzlichen Informationen im Format UTF-8", "UTF-8", 5),
				new Format("PLS", ".pls", "PLS-Playlist", "Cp1252", 0),
				/* Geplant:
				 * PLS: http://de.wikipedia.org/wiki/PLS_(Dateiformat)
				 * PdlList -> Serialisable
				 * I-Tunes Format
				 * http://gonze.com/playlists/playlist-format-survey.html
				 */
		};
	}
	
	/** Stellt fest, ob das angegebene Format unterstützt wird. 
	 * @param format Format dass überprüft wird.
	 * @return True, wenn das angegebene Format unterstützt wird. */
	public static boolean isFormatSupported(String format)
	{
		for(Format supported : getFormats())
			if(supported.equals(format))
				return true;
		return false;
	}
	
	/** Gibt ein passendes Format für eine Dateinamenerweiterung zurück. 
	 * @param fileName Dateiname.
	 * @return Passendes Format zu dem Dateinamen.*/
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
	
	/** Gibt das format mit dem angegbenen Namen zurück. 
	 * @param name Name des Formates.
	 * @return Format mir dem Namen.*/
	public static Format getFormatByName(String name)
	{
		for(Format f : getFormats())
		{
			if(f.equals(name))
				return f;
		}
		return null;
	}
	
	/** Schreibt M3U 
	 * 
	 * @param tracks
	 * @param os
	 * @param ext True wenn Metadaten gespeichert werden sollen.
	 * @throws IOException
	 */
	//TODO relativ http://www.devx.com/tips/Tip/13737
	protected static void writeM3U(Track[] tracks, OutputStream os, boolean ext, String encoding) throws IOException
	{
		PrintStream pw;
		try
		{
			pw = new PrintStream(os, false, encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IOException(e);
		}
		
		if(ext)
			pw.println("#EXTM3U");
		for(Track track : tracks)
		{
			if(ext)
			{
				pw.print("#EXTINF:");
				pw.print(Math.round(track.duration));
				pw.print(',');
				pw.println(track.name);
			}
			pw.println(track.path);
		}
	}
	
	protected static void writePls(Track[] tracks, OutputStream os) throws IOException
	{
		PrintStream pw;
		try
		{
			pw = new PrintStream(os, false, "Cp1252");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IOException(e);
		}
		
		pw.println("[Playlist]");
		pw.print("NumberOfEntries=");
		pw.println(tracks.length);
		
		int i = 1;
		for(Track track : tracks)
		{
			pw.print("File");
			pw.print(i);
			pw.print('=');
			pw.println(track.path);
			
			pw.print("Title");
			pw.print(i);
			pw.print('=');
			pw.println(track.name);
			
			pw.print("Length");
			pw.print(i);
			pw.print('=');
			pw.println(Math.round(track.duration));
			
			i++;
		}
		
		pw.println("Version=2");
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
