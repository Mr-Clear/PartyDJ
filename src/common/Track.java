package common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
	
	/**Erstellt einen neuen Track mit den angegebenen Werten
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
		else
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


