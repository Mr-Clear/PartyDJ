package gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import common.Track;

/**
 * 
 * @author Sam
 */
public class TrackSelection implements Transferable
{
	private final Track[] trackArray;
	private final List<File> files = new ArrayList<File>();
	private final List<String> names = new ArrayList<String>();
	private final DataFlavor trackFlavor = new DataFlavor(Track.class, "Track flavor");
	private final DataFlavor flavors[] = {trackFlavor, DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};

	public TrackSelection(Track[] trackArray)
	{
		this.trackArray = trackArray;
		for(Track track : trackArray)
		{
			files.add(new File(track.path));
			names.add(track.name);
		}
	}
	
	public synchronized Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException
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

	public synchronized DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor dataFlavor)
	{
		return (dataFlavor.equals(trackFlavor));
	}
}
