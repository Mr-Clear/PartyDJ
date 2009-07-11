package lists.data;

import java.io.File;
import common.Track;
import data.IData;
import lists.ListException;
import basics.Controller;

/**
 * Audio-Track der in der Datenbank gespeichert ist..
 * <p>Die eigenschaften sind veränderlich.
 * 
 * @author Eraser
 */
public class DbTrack extends Track
{
	private static final long serialVersionUID = -4142764593365608567L;
	protected transient final IData data = Controller.getInstance().getData();
	
	/** Index in der Hauptliste */
	protected int index = -1;
	
	public DbTrack(int index, String path, String name, double duration, long size, Problem problem, String info)
	{
		super(path, name, duration, size, problem, info);
		setIndex(index);
	}
	
	public DbTrack(String filePath, boolean readDuration)
	{
		super(filePath, readDuration);
		try
		{
			data.addTrack(this);
		}
		catch (ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Verbinden von Track mit Datenbank fehlgeschlagen.");
		}
	}

	public DbTrack(String path, String name, double duration, long size, Problem problem, String info)
	{
		super(path, name, duration, size, problem, info);
		try
		{
			data.addTrack(this);
		}
		catch (ListException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Verbinden von Track mit Datenbank fehlgeschlagen.");
		}
	}
	
	public DbTrack(Track track)
	{
		this(track.getPath(), track.getName(), track.getDuration(), track.getSize(), track.getProblem(), track.getInfo());
	}

	@Override
	public void setDuration(double duration)
	{
		if(this.duration != duration)
		{
			double d = this.duration;
			this.duration = duration;
			try
			{
				data.updateTrack(this, TrackElement.DURATION);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.duration = d;
			}
		}
	}

	@Override
	public void setName(String name)
	{
		if(!this.name.equals(name))
		{
			String d = this.name;
			this.name = name;
			try
			{
				data.updateTrack(this, TrackElement.NAME);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.name = d;
			}
		}
	}
	
	@Override
	public void setPath(String path)
	{
		File newFile = new File(path);
		if(!file.equals(newFile))
		{
			File d = this.file;
			this.file = newFile;
			try
			{
				data.updateTrack(this, TrackElement.PATH);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.file = d;
			}
		}
	}

	@Override
	public void setSize(long size)
	{
		if(this.size != size)
		{
			long d = this.size;
			this.size = size;
			try
			{
				data.updateTrack(this, TrackElement.SIZE);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.size = d;
			}
		}
	}
	
	@Override
	public void setProblem(Problem problem)
	{
		if(this.problem != problem)
		{
			Problem d = this.problem;
			this.problem = problem;
			try
			{
				data.updateTrack(this, TrackElement.PROBLEM);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.problem = d;
			}
		}
	}

	@Override
	public void setInfo(String info)
	{
		if(!this.info.equals(info))
		{
			String d = this.info;
			this.info = info;
			try
			{
				data.updateTrack(this, TrackElement.INFO);
			}
			catch (ListException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Änderung von Daten in der Datenbank fehlgeschlagen.");
				this.info = d;
			}
		}
	}
	
	/** @return Der Index des Tracks in der Datenbank. */
	public int getIndex()
	{
		return index;
	}
	
	/** @param index Der Index des Tracks in der Datenbank. */
	public void setIndex(int index)
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