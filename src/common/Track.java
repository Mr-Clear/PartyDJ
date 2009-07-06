package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import data.IData;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import lists.ListException;
import players.PlayerException;
import basics.Controller;

/**
 * Stellt einen Titel mit seinen Eigenschaften dar.
 * <p>Die eigenschaften sind veränderlich.
 * 
 * @author Eraser
 */
public class Track implements Serializable, Comparable<Track>
{
	private static final long serialVersionUID = -4142764593365608567L;

	/** Index in der Hauptliste */
	public int index;
	/** Absoluter Pfad der Datei */
	public String path;
	/** Angezeigter Name */
	public String name;
	/** Dauer des Tracks */
	public double duration;
	/** Größe der Datei */
	public long size;
	/** Bekannte Probleme mit dem Track */
	public Problem problem;
	/** Beliebige Info über den Track */
	public String info;
	
	/** File-Objekt */
	private File file;
	
	/**Erstellt einen neuen Track mit den angegebenen Werten.
	 * @param index Index in der Hauptliste.
	 * @param path Pfad der Datei.
	 * @param name Angezeigter Name.
	 * @param duration Dauer in Sekunden.
	 * @param size Dateigröße in Byte.
	 * @param problem Problem mit dem Track.
	 * @param info Zusätzliche Info.
	 */
	public Track(int index, String path, String name, double duration, long size, Problem problem, String info)
	{
		this.index = index;
		this.path = path;
		this.name = name;
		this.duration = duration;
		this.size = size;
		this.problem = problem;
		this.info = info;
		
		this.file = new File(path);
	}
	
	/**Erstellt einen neuen Track für die angegebene Datei.
	 * 
	 * @param filePath Pfad der Datei.
	 * @param readDuration Wenn true, wird die Dauer sofort eingelesen.
	 */
	public Track(String filePath, boolean readDuration)
	{
		file = new File(filePath);
		try
		{
			filePath = file.getCanonicalPath();
		}
		catch (IOException ignore){}
		
		index = -1;
		path = filePath;
		name = file.getName();
		name = name.substring(0, name.lastIndexOf('.'));
		problem = Problem.NONE;
		if(file.exists())
		{
			if(readDuration)
			{
				try
				{
					duration = Controller.getInstance().getPlayer().getDuration(path);
				}
				catch (PlayerException ignore)
				{
					duration = 0;
					problem = Problem.CANT_PLAY;
				}
			}
			size = file.length();
		}
		else
		{
			problem = Problem.FILE_NOT_FOUND;
		}
		info = null;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Track)
			return compareTo((Track)o) == 0;
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return file.hashCode();
	}
	
	@Override
	public int compareTo(Track o)
	{
		if(o == null)
			throw new NullPointerException();
		return file.compareTo(o.file);
	}
	
	public Problem hasProblem()
	{
		Problem prob = null;
		IData data = Controller.getInstance().getData();
		boolean exists = new File(path).exists();
		if(!exists)
		{
			prob = Problem.FILE_NOT_FOUND;
		}
		else
		{
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(path);
			}
			catch (FileNotFoundException impossible){}
			Bitstream bs = new Bitstream(fis);
			
			try
			{
				for(int i = 0; i < 16; i++)
					bs.readFrame();
				prob = Problem.NONE;
			}
			catch (BitstreamException e)
			{
				prob = Problem.CANT_PLAY;
			}
		}
		
		if(problem == prob)
			return problem;
		problem = prob;
		
		try
		{
			data.updateTrack(this, TrackElement.PROBLEM);
		}
		catch (ListException le)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, le, "Update eines Tracks fehlgeschlagen");
		}
		return problem;
	}
	
	/**Stellt ein Problem mit einem Track dar.
	 * 
	 * @author Eraser
	 */
	public static enum Problem
	{
		NONE,
		FILE_NOT_FOUND,
		CANT_PLAY,
		OTHER;
		
		public static String[] getStringArray()
		{
			return new String[]{"Kein Problem", 
								"Datei nicht gefunden", 
								"Datei kann nicht abgespielt werden",
								"Unbestimmtes Problem"};
		}
		
		public static int problemToArrayIndex(Problem problem)
		{
			switch(problem)
			{
			case NONE:
				return 0;
			case FILE_NOT_FOUND:
				return 1;
			case CANT_PLAY:
				return 2;
			case OTHER:
			default:
				return 3;
			}
		}
		
		public static Problem arrayIndexToProblem(int index)
		{
			switch(index)
			{
			case 0:
				return NONE;
			case 1:
				return FILE_NOT_FOUND;
			case 2:
				return CANT_PLAY;
			case 3:
			default:
				return OTHER;
			}
		}
	}
	
	/**Wählt ein Element aus dem Track aus.
	 * 
	 * @author Eraser
	 */
	public enum TrackElement
	{
		PATH,
		NAME,
		DURATION,
		SIZE,
		PROBLEM,
		INFO;
	}
}


