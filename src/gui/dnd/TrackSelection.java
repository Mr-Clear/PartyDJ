package gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import common.Track;

/**
 * 
 * @author Sam
 */
public class TrackSelection implements Transferable
{
	private final Track[] trackArray;
	private final DataFlavor trackFlavor = new DataFlavor(Track.class, "Track flavor");
	private final DataFlavor flavors[] = {trackFlavor};

	public TrackSelection(Track[] trackArray)
	{
		this.trackArray = trackArray;
	}
	
	public synchronized Track[] getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException
	{
		return trackArray;
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
