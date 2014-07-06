package common;

import basics.Controller;
import common.annotation.NonNull;
import players.PlayerException;
import java.io.File;
import java.io.Serializable;

/**
 * Stellt einen Titel mit seinen Eigenschaften dar.
 * <p>Die Eigenschaften sind veränderlich.
 * 
 * @author Eraser
 * @author Sam
 */
public class Track implements Serializable, Comparable<Track>
{
	private static final long serialVersionUID = -7621302519719815302L;
	
	protected String name;
	protected String info;
	protected File file;
	protected double duration;
	protected long size;
	protected Problem problem;
	
	/**Erstellt einen neuen Track mit den angegebenen Werten.
	 * @param path Pfad der Datei.
	 * @param name Angezeigter Name.
	 * @param duration Dauer in Sekunden.
	 * @param size Dateigröße in Byte.
	 * @param problem Problem mit dem Track.
	 * @param info Zusätzliche Info.
	 */
	public Track(final String path, final String name, final double duration, final long size, final Problem problem, final String info)
	{
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
	public Track(final String filePath, final boolean readDuration)
	{
		file = new File(filePath);
		
		name = file.getName();
		if(name.contains("."))
			name = name.substring(0, name.lastIndexOf('.'));
		problem = Problem.NONE;
		if(file.exists())
		{
			if(readDuration)
			{
				try
				{
					duration = Controller.getInstance().getPlayer().getDuration(file);
				}
				catch (final PlayerException ignore)
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
	
	/**Kopiert den übergebenen Track.
	 * 
	 * @param track Zu kopierender Track.
	 */
	public Track(Track track)
	{
		if(track != null)
		{
			this.name = track.name;
			this.duration = track.duration;
			this.size = track.size;
			this.problem = track.problem;
			this.info = track.info;
			
			this.file = new File(track.file.getPath());
		}
	}

	/** @return Angezeigter Name. */
	public String getName()
	{
		return name;
	}
	/** @param name Angezeigter Name. */
	public void setName(final String name)
	{
		this.name = name;
	}

	/** @return Beliebige Info über den Track. */
	public String getInfo()
	{
		return info;
	}
	/** @param info Beliebige Info über den Track. */
	public void setInfo(final String info)
	{
		this.info = info;
	}
	
	/** @return Pfad der Datei die dieser AudioTrack wiederspiegelt. */
	@NonNull public String getPath()
	{
		final String ret = file.getAbsolutePath();
		if(ret != null)
			return ret;
		throw new RuntimeException("file.getAbsolutePath() returned null!");
	}
	/** @param path Pfad der Datei die dieser AudioTrack wiederspiegelt. */
	public void setPath(final String path)
	{
		this.file = new File(path);
	}
	
	/** @return Dauer des Tracks in Sekunden. */
	public double getDuration()
	{
		return duration;
	}
	/** @param duration Dauer des Tracks in Sekunden. */
	public void setDuration(final double duration)
	{
		this.duration = duration;
	}
	
	/** @return Größe der Datei in Byte. */
	public long getSize()
	{
		return size;
	}
	/** @param size Größe der Datei in Byte. */
	public void setSize(final long size)
	{
		this.size = size;
	}
	
	/** @return Bekannte Probleme mit dem Track. */
	public Problem getProblem()
	{
		return problem;
	}
	/** @param problem Bekannte Probleme mit dem Track. */
	public void setProblem(final Problem problem)
	{
		this.problem = problem;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	/** Spielt den Track ab. */
	public void play()
	{
		try
		{
			Controller.getInstance().getPlayer().start(this);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			Controller.getInstance().logError(Controller.REGULAR_ERROR, this, e, "Track konnte nicht abgespielt werden.");
		}
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(o instanceof Track)
			return compareTo((Track)o) == 0;
		return false;
	}
	
	@Override
	public int compareTo(final Track o)
	{
		return file.compareTo(o.file);
	}
	
	@Override
	public int hashCode()
	{
		return file.hashCode();
	}
	
	public Problem checkForProblem(final boolean chekPlayable)
	{
		Problem prob = Problem.NONE;
		
		if(!file.exists())
			prob = Problem.FILE_NOT_FOUND;
		else if(chekPlayable)
			if(!Controller.getInstance().getPlayer().checkPlayable(this))
				prob = Problem.CANT_PLAY;
		
		if(problem == prob)
			return problem;
		
		setProblem(prob);
		return prob;
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
		
		public static int problemToArrayIndex(final Problem problem)
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
		
		public static Problem arrayIndexToProblem(final int index)
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
}


