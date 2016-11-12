package gui;

import basics.Controller;
import common.Functions;
import common.Track;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.LogManager;
import javax.swing.table.AbstractTableModel;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

public class Mp3TagTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -4327249399249652427L;

	protected final static Controller controller = Controller.getInstance();
	protected final List<String> names = new ArrayList<>();
	protected final List<String> values = new ArrayList<>();
	protected final Map<String, String> id3v2TagTypes = new TreeMap<>();
	
	{
		// Logging von Jaudiotagger abschalten.
		try
		{
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream("org.jaudiotagger.level = OFF".getBytes()));
		} 
		catch (final Exception ignored)
		{ /* Dann halt nicht. */ }
		
		// id3v2TagTypes füllen.
		id3v2TagTypes.put("TRCK", "TRACKNUMBER");
		id3v2TagTypes.put("TPOS", "DISCNUMBER");
		id3v2TagTypes.put("TIT2", "TITLE");
		id3v2TagTypes.put("TSOT", "TITLESORTORDER");
		id3v2TagTypes.put("TALB", "ALBUM");
		id3v2TagTypes.put("TSOA", "ALBUMSORTORDER");
		id3v2TagTypes.put("TOAL", "ORIGALBUM");
		id3v2TagTypes.put("TIT3", "SUBTITLE");
		id3v2TagTypes.put("TSST", "SETSUBTITLE");
		id3v2TagTypes.put("TPE1", "ARTIST");
		id3v2TagTypes.put("TSOP", "PERFORMERSORTORDER");
		id3v2TagTypes.put("WOAR", "WWWARTIST");
		id3v2TagTypes.put("TOPE", "ORIGARTIST");
		id3v2TagTypes.put("TPE2", "BAND");
		id3v2TagTypes.put("TCOM", "COMPOSER");
		id3v2TagTypes.put("TEXT", "LYRICIST");
		id3v2TagTypes.put("TOLY", "ORIGLYRICIST");
		id3v2TagTypes.put("TPE3", "CONDUCTOR");
		id3v2TagTypes.put("TPE4", "MIXARTIST");
		id3v2TagTypes.put("TPUB", "PUBLISHER");
		id3v2TagTypes.put("WPUB", "WWWPUBLISHER");
		id3v2TagTypes.put("TENC", "ENCODEDBY");
		id3v2TagTypes.put("TIPL", "INVOLVEDPEOPLE2");
		id3v2TagTypes.put("TMCL", "MUSICIANCREDITLIST");
		id3v2TagTypes.put("TCON", "GENRE");
		id3v2TagTypes.put("TMOO", "MOOD");
		id3v2TagTypes.put("TMED", "MEDIATYPE");
		id3v2TagTypes.put("WOAS", "WWWAUDIOSOURCE");
		id3v2TagTypes.put("TLAN", "LANGUAGE");
		id3v2TagTypes.put("TIT1", "CONTENTGROUP");
		id3v2TagTypes.put("TDRC", "RECORDINGTIME");
		id3v2TagTypes.put("TDRL", "RELEASETIME");
		id3v2TagTypes.put("TDEN", "ENCODINGTIME");
		id3v2TagTypes.put("TDTG", "TAGGINGTIME");
		id3v2TagTypes.put("TDOR", "ORIGRELEASETIME");
		id3v2TagTypes.put("COMM", "COMMENT");
		id3v2TagTypes.put("POPM", "POPULARIMETER");
		id3v2TagTypes.put("PCNT", "PLAYCOUNTER");
		id3v2TagTypes.put("TSSE", "ENCODERSETTINGS");
		id3v2TagTypes.put("TSSE", "ENCODERSETTINGS");
		id3v2TagTypes.put("TLEN", "SONGLEN");
		id3v2TagTypes.put("TFLT", "FILETYPE");
		id3v2TagTypes.put("SEEK", "SEEKFRAME");
		id3v2TagTypes.put("MLLT", "MPEGLOOKUP");
		id3v2TagTypes.put("ASPI", "AUDIOSEEKPOINT");
		id3v2TagTypes.put("TDLY", "PLAYLISTDELAY");
		id3v2TagTypes.put("ETCO", "EVENTTIMING");
		id3v2TagTypes.put("SYTC", "SYNCEDTEMPO");
		id3v2TagTypes.put("POSS", "POSITIONSYNC");
		id3v2TagTypes.put("RBUF", "BUFFERSIZE");
		id3v2TagTypes.put("TBPM", "BPM");
		id3v2TagTypes.put("TKEY", "INITIALKEY");
		id3v2TagTypes.put("RVA2", "VOLUMEADJ2");
		id3v2TagTypes.put("RVRB", "REVERB");
		id3v2TagTypes.put("EQU2", "EQUALIZATION2");
		id3v2TagTypes.put("TSRC", "ISRC");
		id3v2TagTypes.put("MCDI", "CDID");
		id3v2TagTypes.put("UFID", "UNIQUEFILEID");
		id3v2TagTypes.put("WCOM", "WWWCOMMERCIALINFO");
		id3v2TagTypes.put("WPAY", "WWWPAYMENT");
		id3v2TagTypes.put("TOWN", "FILEOWNER");
		id3v2TagTypes.put("COMR", "COMMERCIAL");
		id3v2TagTypes.put("TCOP", "COPYRIGHT");
		id3v2TagTypes.put("TPRO", "PRODUCEDNOTICE");
		id3v2TagTypes.put("USER", "TERMSOFUSE");
		id3v2TagTypes.put("WCOP", "WWWCOPYRIGHT");
		id3v2TagTypes.put("OWNE", "OWNERSHIP");
		id3v2TagTypes.put("AENC", "AUDIOCRYPTO");
		id3v2TagTypes.put("ENCR", "CRYPTOREG");
		id3v2TagTypes.put("GRID", "GROUPINGREG");
		id3v2TagTypes.put("SIGN", "SIGNATURE");
		id3v2TagTypes.put("USLT", "UNSYNCEDLYRICS");
		id3v2TagTypes.put("SYLT", "SYNCEDLYRICS");
		id3v2TagTypes.put("APIC", "PICTURE");
		id3v2TagTypes.put("GEOB", "GENERALOBJECT");
		id3v2TagTypes.put("PRIV", "PRIVATE");
		id3v2TagTypes.put("TXXX", "USERTEXT");
		id3v2TagTypes.put("LINK", "LINKEDINFO");
		id3v2TagTypes.put("TOFN", "ORIGFILENAME");
		id3v2TagTypes.put("TRSN", "NETRADIOSTATION");
		id3v2TagTypes.put("TRSO", "NETRADIOOWNER");
		id3v2TagTypes.put("WORS", "WWWRADIOPAGE");
		id3v2TagTypes.put("WXXX", "WWWUSER");
		id3v2TagTypes.put("WOAF", "WWWAUDIOFILE");
	}
		
	public Mp3TagTableModel(final Track track)
	{
		// Aus Track
		addData("Name", track.getName());
		addData("Dateipfad", track.getPath());
		addData("Dauer", Functions.formatTime(track.getDuration()));
		addData("Dateigröße", Functions.formatSize(track.getSize()));
		addData("Problem", track.getProblem().toString());
		if(track.getInfo() != null)
			addData("Info", track.getInfo());

		final MP3File file;
		try
		{
			file = new MP3File(new File(track.getPath()), MP3File.LOAD_ALL, true);
		}
		catch(final IOException e)
		{
			addData("Kann Datei nicht öffnen.", null);
			controller.logError(Controller.NORMAL_ERROR, this, e, "Kann Datei nicht öffnen.");
			return;
		}
		catch(final TagException e)
		{
			addData("Kein Tag gefunden.", null);
			controller.logError(Controller.NORMAL_ERROR, this, e, "Kein Tag gefunden.");
			return;
		}
		catch(final ReadOnlyFileException e)
		{
			addData("Unerwarteter Fehler.", null);
			controller.logError(Controller.NORMAL_ERROR, this, e, "Datei kann nicht zum Schreiben geöffnet werden.");
			return;
		}
		catch(final InvalidAudioFrameException e)
		{
			addData("Ungültiger Frame in Datei.", null);
			controller.logError(Controller.NORMAL_ERROR, this, e, "Ungültiger Frame in Datei.");
			return;
		}

		final ID3v1Tag tag1 = file.getID3v1Tag();
		final AbstractID3v2Tag tag2 = file.getID3v2Tag();

		// Allgemein
		addData("Track Infos", null);
		addField("Artist", FieldKey.ARTIST, tag1, tag2);
		addField("Album", FieldKey.ALBUM, tag1, tag2);
		addField("Titel", FieldKey.TITLE, tag1, tag2);
		String data1 = getField(FieldKey.DISC_NO, tag1, tag2);
		String data2 = getField(FieldKey.DISC_TOTAL, tag1, tag2);
		if(hatContent(data1))
		{
			if(hatContent(data2))
				addData("CD Nummer", data1 + " / " + data2);
			else
				addData("CD Nummer", data1);
		}
		data1 = getField(FieldKey.TRACK, tag1, tag2);
		data2 = getField(FieldKey.TRACK_TOTAL, tag1, tag2);
		if(hatContent(data1))
		{
			if(hatContent(data2))
				addData("Track Nummer", data1 + " / " + data2);
			else
				addData("Track Nummer", data1);
		}
		addField("Genre", FieldKey.GENRE, tag1, tag2);
		addField("Jahr", FieldKey.YEAR, tag1, tag2);
		
		// Audiodaten
		final AudioHeader header = file.getAudioHeader();
		addData("Audiodaten:", null);
		addData("Bit Rate", header.getBitRate() + " Kbps");
		addData("Kanäle", header.getChannels());
		addData("Format", header.getFormat());
		addData("Typ", header.getEncodingType());
		addData("Abtastrate", header.getSampleRate() + " Hz");
		addData("Dauer", header.getTrackLength() + " Sekunden");
		
		// ID3v1 Tag
		if(tag1 != null)
		{
			addData("ID3v1 Tag:", null);
			for(final FieldKey key : FieldKey.values())
			{
				final String value = tag1.getFirst(key);
				if(value.length() > 0)
					addData(key.name(), value);
			}
		}
		else
			addData("Kein ID3v1 Tag.", null);

		// ID3v2 Tag
		if(tag2 != null)
		{
			addData("ID3v2 Tag:", null);
			for(final Iterator<TagField> iterator =  tag2.getFields(); iterator.hasNext();)
			{
				final TagField field = iterator.next();
				if(id3v2TagTypes.containsKey(field.getId()))
					addData(field.getId() + " (" + id3v2TagTypes.get(field.getId()) + ")", tag2.getFirst(field.getId()));
				else
					addData(field.getId(), tag2.getFirst(field.getId()));
			}
		}
		else
			addData("Kein ID3v2 Tag.", null);
	}

	@Override
	public int getRowCount()
	{
		return names.size();
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public String getColumnName(final int columnIndex)
	{

		if(columnIndex == 0)
			return "Eigenschaft";
		return "Wert";
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		return String.class;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		if(columnIndex == 0)
			return names.get(rowIndex);
		return values.get(rowIndex);
	}
	
	protected void addData(final String name, final String value)
	{
		names.add(name);
		values.add(value);
	}
	
	protected static String getField(final FieldKey key, final ID3v1Tag tag1, final AbstractID3v2Tag tag2)
	{
		String val = null;
		if(tag2 != null)
			val = tag2.getFirst(key);
		if((val == null || val.length() == 0) && tag1 != null)
			val = tag1.getFirst(key);
		return val;
	}
	
	protected void addField(final String name, final FieldKey key, final ID3v1Tag tag1, final AbstractID3v2Tag tag2)
	{
		final String value = getField(key, tag1, tag2);
		if(hatContent(value))
			addData(name, value);
	}
	
	protected static boolean hatContent(final String value)
	{
		return value != null && value.length() > 0 && !value.equalsIgnoreCase("null");
	}
}
