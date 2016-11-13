package de.klierlinge.partydj.lists.data;

import java.io.File;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.lists.ListException;

/**
 * Audio-Track der in der Datenbank gespeichert ist..
 * <p>Die Eigenschaften sind veränderlich.
 * 
 * @author Eraser
 */
public abstract class DbTrack extends Track
{
	private static final long serialVersionUID = -4142764593365608567L;
	protected final transient IData data;
	
	/** Index in der Hauptliste */
	protected int index = -1;
	
	/**Erstellt einen neuen DbTrack mit den angegebenen Werten.
	 * @param index Index in der Hauptliste.
	 * @param path Pfad der Datei.
	 * @param name Angezeigter Name.
	 * @param duration Dauer in Sekunden.
	 * @param size Dateigröße in Byte.
	 * @param problem Problem mit dem Track.
	 * @param info Zusätzliche Info.
	 */
	protected DbTrack(final IData data, final int index, final String filePath, final String name, final double duration, final long size, final Problem problem, final String info)
	{
		super(filePath, name, duration, size, problem, info);
		if(data == null)
			throw new NullPointerException("Data must not be null.");
		this.data = data;
		setIndex(index);
	}
	
	/**Erstellt einen DbTrack aus dem angegebenen Track.
	 * Index muss danach noch zugewiesen werden.
	 * 
	 * @param track Track aus dem die Daten kopiert werden.
	 */
	protected DbTrack(final IData data, final Track track)
	{
		this(data, -1, track.getPath(), track.getName(), track.getDuration(), track.getSize(), track.getProblem(), track.getInfo());
	}

	@Override
	public void setDuration(final double duration)
	{
		if(this.duration != duration)
		{
			super.setDuration(duration);
			
			final double d = this.duration;
			try
			{
				data.updateTrack(this, TrackElement.DURATION, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.duration = d;
			}
		}
	}

	@Override
	public void setName(final String name)
	{
		if(!this.name.equals(name))
		{
			super.setName(name);
			
			final String d = this.name;
			try
			{
				data.updateTrack(this, TrackElement.NAME, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.name = d;
			}
		}
	}
	
	@Override
	public void setPath(final String path)
	{
		final File oldFile = file;
		super.setPath(path);
		
		if(!file.equals(oldFile))
		{
			final File d = this.file;
			try
			{
				data.updateTrack(this, TrackElement.PATH, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.file = d;
			}
		}
	}

	@Override
	public void setSize(final long size)
	{
		if(this.size != size)
		{
			super.setSize(size);
			
			final long d = this.size;
			try
			{
				data.updateTrack(this, TrackElement.SIZE, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.size = d;
			}
		}
	}
	
	@Override
	public void setProblem(final Problem problem)
	{
		if(this.problem != problem)
		{
			super.setProblem(problem);
			
			final Problem d = this.problem;
			try
			{
				data.updateTrack(this, TrackElement.PROBLEM, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.problem = d;
			}
		}
	}

	@Override
	public void setInfo(final String info)
	{
		if(!this.info.equals(info))
		{
			super.setInfo(info);
			
			final String d = this.info;
			try
			{
				data.updateTrack(this, TrackElement.INFO, false);
			}
			catch (final ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.info = d;
			}
		}
	}
	
	@Override
	public boolean equals(final Object o)
	{
		return super.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	/** @return Der Index des Tracks in der Datenbank. */
	public int getIndex()
	{
		return index;
	}
	
	/** @param index Der Index des Tracks in der Datenbank. */
	public void setIndex(final int index)
	{
		if(this.index == -1)
			this.index = index;
		else
			throw new UnsupportedOperationException("Der Index eines DbTracks kann nicht geändert werden.");
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