package gui.dnd;

import common.Track;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Sam
 */
public class TrackSelection implements Transferable
{
	private final Track[] trackArray;
	private final List<File> files = new ArrayList<>();
	private final List<String> names = new ArrayList<>();
	private final DataFlavor trackFlavor = new DataFlavor(Track.class, "Track flavor");
	private final DataFlavor[] flavors = {trackFlavor, DataFlavor.javaFileListFlavor};

	public TrackSelection(final Track[] trackArray)
	{
		this.trackArray = trackArray;
		for(final Track track : trackArray)
		{
			files.add(new File(track.getPath()));
			names.add(track.getName());
		}
	}
	
	public TrackSelection(final List<Track> tracks)
	{
		trackArray = new Track[tracks.size()];
		tracks.toArray(trackArray);
		for(final Track track : trackArray)
		{
			files.add(new File(track.getPath()));
			names.add(track.getName());
		}
	}
	
	@Override
	public synchronized Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException
	{
		if(dataFlavor.equals(trackFlavor))
			return trackArray;
		else if(dataFlavor.isFlavorJavaFileListType())
			return files;
		else if(dataFlavor.isFlavorTextType())
			return names;
		else
			throw new UnsupportedFlavorException(dataFlavor);
	}

	@Override
	public synchronized DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor dataFlavor)
	{
		return (dataFlavor.equals(trackFlavor));
	}
}
