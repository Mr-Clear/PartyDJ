package de.klierlinge.partydj.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.lists.TrackListModel;

/**Bietet Funktionen zum Schreiben von Playlists an.
 * 
 * @author Eraser
 */
public final class PlaylistWriter
{
	private final static Logger log = LoggerFactory.getLogger(PlaylistWriter.class);
	private PlaylistWriter(){}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(final Iterable<Track> tracks, final String fileName)
	{
		write(toArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final Iterable<Track> tracks, final String fileName, final Format format)
	{
		write(toArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final Iterable<Track> tracks, final String fileName, final String format)
	{
		write(toArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(final List<Track> tracks, final String fileName)
	{
		write(toArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final List<Track> tracks, final String fileName, final Format format)
	{
		write(toArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final List<Track> tracks, final String fileName, final String format)
	{
		write(toArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(final TrackListModel tracks, final String fileName)
	{
		write(toArray(tracks), fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final TrackListModel tracks, final String fileName, final Format format)
	{
		write(toArray(tracks), fileName, format);
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final TrackListModel tracks, final String fileName, final String format)
	{
		write(toArray(tracks), fileName, getFormatByName(format));
	}
	
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 */
	public static void write(final Track[] tracks, final String fileName)
	{
		write(tracks, fileName, getFormatByFileName(fileName));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final Track[] tracks, final String fileName, final String format)
	{
		write(tracks, fileName, getFormatByName(format));
	}
	/**Schreibt eine Playlist in eine Datei.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param fileName Dateiname der Playlist.
	 * @param format Format in dem die Playlist gespeichert wird.
	 */
	public static void write(final Track[] tracks, final String fileName, final Format format)
	{
		try
		{
			try(final OutputStream fos = new FileOutputStream(fileName))
			{
				write(tracks, fos, new File(fileName).getParentFile(), format);
			}
		}
		catch (final IOException e)
		{
			log.error("Fehler bei schreiben in Playlist-Datei.", e);
			// TODO: Notify user.
		}		
	}
	
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final Iterable<Track> tracks, final OutputStream stream, final File targetFile, final Format format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Aufzählung der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final Iterable<Track> tracks, final OutputStream stream, final File targetFile, final String format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final List<Track> tracks, final OutputStream stream, final File targetFile, final Format format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Liste der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final List<Track> tracks, final OutputStream stream, final File targetFile, final String format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final TrackListModel tracks, final OutputStream stream, final File targetFile, final Format format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks TrackListModel der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final TrackListModel tracks, final OutputStream stream, final File targetFile, final String format) throws IOException
	{
		write(toArray(tracks), stream, targetFile, format);
	}
	
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final Track[] tracks, final OutputStream stream, final File targetFile, final String format) throws IOException
	{
		write(tracks, stream, targetFile, getFormatByName(format));
	}
	/**Schreibt eine Playlist in einen OutputStream.
	 * 
	 * @param tracks Array der Tracks die gespeichert werden.
	 * @param stream Stream zu der Playlist.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param format Format in dem die Playlist gespeichert wird.
	 * @throws IOException 
	 */
	public static void write(final Track[] tracks, final OutputStream stream, final File targetFile, final Format format) throws IOException
	{
		if("M3U".equalsIgnoreCase(format.name) || "M3U8".equalsIgnoreCase(format.name))
			writeM3U(tracks, stream, targetFile, false, format.encoding);
		else if("EXTM3U".equalsIgnoreCase(format.name) || "EXTM3U8".equalsIgnoreCase(format.name))
			writeM3U(tracks, stream, targetFile, true, format.encoding);
		else if("PLS".equalsIgnoreCase(format.name))
			writePls(tracks, stream);
		else
			throw new IllegalArgumentException("Format wird nicht unterstützt: " + format);		
	}
	

	protected static Track[] toArray(final TrackListModel listModel)
	{
		final Track[] array = new Track[listModel.getSize()];
		synchronized(listModel)
		{
			for(int i = 0; i < listModel.getSize(); i++)
				array[i] = listModel.getElementAt(i);
		}
		return array;
	}
	protected static Track[] toArray(final Iterable<Track> iterable)
	{
		final List<Track> list = new ArrayList<>();
		for(final Track track : iterable)
			list.add(track);
		return toArray(list);
	}
	protected static Track[] toArray(final List<Track> list)
	{
		final Track[] array = new Track[list.size()];
		list.toArray(array);
		return array;
	}
	
	/** Gibt die unterstützten Formate zurück. 
	 * @param onlyHighestPriority Wenn true, wird zu jeder Dateinamenerweiterung nur das Format mit der größten Priorität zurück gegeben. 
	 * @return Array mit allen unterstützen Formaten.*/
	public static Format[] getFormats(final boolean onlyHighestPriority)
	{
		Format[] ret = new Format[]{
			new Format("M3U", ".m3u", "Einfache M3U Playlist", "Cp1252", 0),
			new Format("EXTM3U", ".m3u", "M3U Playlist mit zusätzlichen Informationen", "Cp1252", 5),
			new Format("M3U8", ".m3u8", "Einfache M3U Playlist im Format UTF-8", "UTF-8", 0),
			new Format("EXTM3U8", ".m3u8", "M3U Playlist mit zusätzlichen Informationen im Format UTF-8", "UTF-8", 5),
			new Format("PLS", ".pls", "PLS-Playlist", "Cp1252", 0),
			/* TODO Weitere Formate
			 * PdjList -> Serialisable
			 * iTunes Format
			 * http://gonze.com/playlists/playlist-format-survey.html
			 */
		};
		
		if(onlyHighestPriority)
		{
			final Map<String, Format> formats = new HashMap<>();
			for(final Format format : ret)
			{
				final Format f = formats.get(format.getExtension());
				if(f == null || f.priority < format.priority)
					formats.put(format.getExtension(), format);
			}
			ret = new Format[formats.size()];
			formats.values().toArray(ret);
		}
		
		return ret;
	}
	
	/** Gibt die unterstützten Formate zurück. 
	 * @return Array mit allen unterstützen Formaten.*/
	public static Format[] getFormats()
	{
		return getFormats(false);
	}
	
	/** Stellt fest, ob das angegebene Format unterstützt wird. 
	 * @param format Format dass überprüft wird.
	 * @return True, wenn das angegebene Format unterstützt wird. */
	public static boolean isFormatSupported(final String format)
	{
		for(final Format supported : getFormats())
			if(supported.name.equalsIgnoreCase(format))
				return true;
		return false;
	}
	
	/** Gibt ein passendes Format für eine Dateinamenerweiterung zurück. 
	 * @param fileName Dateiname.
	 * @return Passendes Format zu dem Dateinamen.*/
	public static Format getFormatByFileName(final String fileName)
	{
		Format ret = null;
		final int priority = 0;
		for(final Format f : getFormats())
		{
			if(fileName.toLowerCase().endsWith(f.getExtension().toLowerCase()))
				if(f.priority >= priority)
					ret = f;
		}
		return ret;
	}
	
	/** Gibt das format mit dem angegbenen Namen zurück. 
	 * @param name Name des Formates.
	 * @return Format mir dem Namen.*/
	public static Format getFormatByName(final String name)
	{
		for(final Format f : getFormats())
		{
			if(f.name.equalsIgnoreCase(name))
				return f;
		}
		return null;
	}
	
	/** Schreibt  eine Playliste im M3U-Format. 
	 * 
	 * @param tracks Tu speichernde Tracks.
	 * @param os OutputStream in den die Playliste geschrieben werden soll.
	 * @param targetFile Zieldatei, falls die Pfadangaben relativ sein sollen.
	 * @param ext True wenn Metadaten gespeichert werden sollen.
	 * @throws IOException
	 */
	protected static void writeM3U(final Track[] tracks, final OutputStream os, final File targetFile, final boolean ext, final String encoding) throws IOException
	{
		PrintStream pw;
		try
		{
			pw = new PrintStream(os, false, encoding);
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new IOException(e);
		}
		
		if(ext)
			pw.println("#EXTM3U");
		for(final Track track : tracks)
		{
			if(ext)
			{
				pw.print("#EXTINF:");
				pw.print(Math.round(track.duration));
				pw.print(',');
				pw.println(track.name);
			}
			pw.println(getRelativePath(targetFile, new File(track.getPath())));
		}
	}
	
	protected static void writePls(final Track[] tracks, final OutputStream os) throws IOException
	{
		PrintStream pw;
		try
		{
			pw = new PrintStream(os, false, "Cp1252");
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new IOException(e);
		}
		
		pw.println("[Playlist]");
		pw.print("NumberOfEntries=");
		pw.println(tracks.length);
		
		int i = 1;
		for(final Track track : tracks)
		{
			pw.print("File");
			pw.print(i);
			pw.print('=');
			pw.println(track.getPath());
			
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
	
	/**Erstellt einen relativen Pfad.
	 * 
	 * @param home Verzeichnis von dem der Pfad ausgehen soll.
	 * @param file Pfad der zu einem relativen Pfad umgewandelt werden soll. 
	 * @return Relativer Pfad.
	 */
	public static String getRelativePath(final File home, final File file)
	{
		List<String> homelist;
		List<String> filelist;
		String s;
		
		char homeDrive;
		char fileDrive;
		
		String absolutePath = home.getAbsolutePath();
		if(absolutePath.charAt(1) == ':')
			homeDrive = absolutePath.charAt(0);
		else
			homeDrive = 0;
		
		absolutePath = file.getAbsolutePath();
		if(absolutePath.charAt(1) == ':')
			fileDrive = absolutePath.charAt(0);
		else
			fileDrive = 0;

		if(homeDrive != 0 && homeDrive != fileDrive)
			return absolutePath;
		
			
		homelist = getPathList(home);
		filelist = getPathList(file);
		s = matchPathLists(homelist, filelist);

		return s;
	}
	
	/**Schreibt die Einzelnen Elemente eines Pfades in eine Liste.
	 * Bsp: /a/b/c/d.txt => [d.txt,c,b,a]
	 * 
	 * @param f Zu zerlegender Pfad.
	 * @return a Eine Liste mit allen Elementen des Pfades in umgekehrter Reihenfolge.
	 */
	private static List<String> getPathList(final File f)
	{
		final List<String> l = new ArrayList<>();
		File r;
		
		for(r = f.getAbsoluteFile(); r != null; r = r.getParentFile())
		{
			l.add(r.getName());
		}
		
		return l;
	}

	/**Erstellt den relativen Pfad aus zwei Pfadangaben in Listen.
	 * 
	 * @param r Verzeichnis von dem der Pfad ausgehen soll.
	 * @param f Pfad der zu einem relativen Pfad umgewandelt werden soll.
	 * @return Relativer Pfad.
	 */
	private static String matchPathLists(final List<String> r, final List<String> f)
	{
		int i;
		int j;
		// start at the beginning of the lists
		// iterate while both lists are equal
		i = r.size() - 1;
		j = f.size() - 1;

		// first eliminate common root
		while((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j))))
		{
			i--;
			j--;
		}

		final StringBuilder sb = new StringBuilder();
		// for each remaining level in the home path, add a ..
		for(; i >= 0; i--)
		{
			sb.append("..");
			sb.append(File.separator);
		}

		// for each level in the file path, add the path
		for(; j >= 1; j--)
		{
			sb.append(f.get(j));
			sb.append(File.separator);
		}

		// file name
		sb.append(f.get(j));
		return sb.toString();
	}
	
	/** Format einer Playlist */
	public static class Format
	{
		/** Name des Formates. Nicht case sensitive. */
		protected final String name;
		/** Übliche Dateinamenerweiterung für das Format. */
		private final String extension;
		/** Kurze Beschreibung des formates. */
		private final String description;
		/** Codierung des Formats */
		protected final String encoding;
		/** Bei zwei Formaten mit gleicher Erweiterung wird das Format mit der größeren Priorität gewählt */
		protected final int priority;
		
		public Format(final String formatName, final String extension, final String description, final String encoding)
		{
			this(formatName, extension, description, encoding, 0);
		}
		
		public Format(final String formatName, final String extension, final String description, final String encoding, final int priority)
		{
			this.name = formatName;
			this.extension = extension;
			this.description = description;
			this.encoding = encoding;
			this.priority = priority;
		}
		
		@Override
		public boolean equals(final Object o)
		{
			if(o instanceof Format)
				return name.equalsIgnoreCase(((Format)o).name);
			return false;
		}

		@Override
		public int hashCode()
		{
			return name.hashCode() + getExtension().hashCode() + getDescription().hashCode() + encoding.hashCode() + priority;
		}
		
		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}

		public String getExtension()
		{
			return extension;
		}
		
		public String getEncoding()
		{
			return encoding;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
