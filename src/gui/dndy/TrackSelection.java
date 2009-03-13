package gui.dndy;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import common.Track;

public class TrackSelection implements Transferable
{
	private final Object[] trackArray;
	private final DataFlavor trackFlavor = new DataFlavor(Track.class, "Track flavor");
	private final DataFlavor flavors[] = {trackFlavor};

	public TrackSelection(Object[] trackArray)
	{
		this.trackArray = trackArray;
	}
	
	public synchronized Object[] getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException
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
