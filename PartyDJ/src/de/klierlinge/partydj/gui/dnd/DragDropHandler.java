package de.klierlinge.partydj.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;

/**
 * 
 * @author Sam
 */
public class DragDropHandler extends TransferHandler
{
	private static final long serialVersionUID = 6601023550058648978L;
	private static final Logger log = LoggerFactory.getLogger(DragDropHandler.class);

	@Override
	public boolean canImport(final TransferHandler.TransferSupport info)
	{
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
			return false;
		
        return true;
	}
	
	public synchronized static boolean importData(final PDJList list, final Transferable transferable)
	{
		if (!transferable.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
		{
			log.warn("Drop mit unsupported flavor.");
			return false;
		}
		
		Object[] data = null;
		final PDJList pdjList = list;
		final ListModel<Track> listModel = pdjList.getModel();
		
		try
		{
			data = (Object[])transferable.getTransferData(new DataFlavor(Track.class, "Track flavor"));
		}
		catch (final UnsupportedFlavorException | IOException e)
		{
			log.warn("Drop mit unsupported flavor.", e);
			return false;
		}
		
		switch (list.getListDropMode())
		{
		case NONE:
			break;
		case COPY:
			try
			{
				for(int i = data.length - 1; i >= 0; i--)
				{
					((EditableListModel)listModel).add(list.getSelectedIndex() + 1, (Track)data[i], i > 0);
				}	
			}
			catch (final ListException e)
			{
				log.error("Zugriff auf Liste bei DnD fehlgeschlagen.", e);
			}
			break;
		case MOVE:
			System.out.println("MOVE not supported");
			break;				
		case DELETE:
			break;				
		case COPY_OR_MOVE:
			try
			{
				for(int i = data.length - 1; i >= 0; i--)
				{
					((EditableListModel)listModel).add(list.getSelectedIndex() + 1, (Track)data[i], i > 0);
				}	
			}
			catch (final ListException e)
			{
				log.error("Zugriff auf Liste bei DnD fehlgeschlagen.", e);
			}
			break;
		}
		return true;
	}
	
	@Override
	public synchronized boolean importData(final TransferHandler.TransferSupport info)
	{
		Object[] data = null;
		
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
		{
			log.warn("Drop mit unsupported flavor.");
			return false;
		}
	
		if(info.getComponent() instanceof PDJList)
		{
			final PDJList pdjList = (PDJList)info.getComponent();
			final ListModel<Track> listModel = pdjList.getModel();
			
			try
			{
				data = (Object[])info.getTransferable().getTransferData(new DataFlavor(Track.class, "Track flavor"));
			}
			catch (final UnsupportedFlavorException e)
			{
				log.warn("Drop mit unsupported flavor.", e);
				return false;
			}
			catch (final IOException e)
			{
				log.warn("Fehler bei Drop.", e);
				return false;
			}
			
			if(!info.isDrop()) 
	        {
				for(int i = data.length - 1; i >= 0; i--)
				{
					try
					{
						((EditableListModel)listModel).add((Track)data[i], i > 0);
					}
					catch (final ListException e)
					{
						e.printStackTrace();
					}
				} 
				return true;
	        }
		}
		
		else if(info.getComponent() instanceof JPanel)
		{
			Track[] tracks;
			try
			{
				tracks = (Track[]) info.getTransferable().getTransferData(new DataFlavor(Track.class, "Track flavor"));
				
				for(int i = 0; i < tracks.length; i++)
				{
					if(i == 0)
					{
						tracks[0].play();
					}
					else
						try
						{
							Controller.getInstance().getListProvider().getDbList("Wunschliste").add(tracks[i], i < tracks.length - 1);
						}
						catch (final ListException e1)
						{
							log.error("Zugriff auf Liste bei DnD fehlgeschlagen.", e1);
						}
				}
			}
			catch (final UnsupportedFlavorException e)
			{
				log.error("Unbekannter Datentyp per DnD eingefügt.", e);
			}
			catch (final IOException e)
			{
				log.error("Datenzugriff bei DnD fehlgeschlagen.", e);
			}
		}
		return true;
			
	}
	
	@Override
	public Transferable createTransferable(final JComponent c)
	{
		final PDJList pdjList = (PDJList)c;
		final List<Track> values = pdjList.getSelectedValuesList();

		return new TrackSelection(values);
	}
	
	@Override
	public int getSourceActions(final JComponent c)
	{
		return COPY;
	}
	
	@Override
	public void exportDone(final JComponent component, final Transferable data, final int action) 
	{
		//Clipboard export
		/*StringTransfer transfer = new StringTransfer();
		StringBuffer buffer = new StringBuffer();
		String export = "";
		
		for(int i = 0; i < ((PDJList)component).getSelectedValues().length; i++)
		{
			buffer.append(((Track)(((PDJList)component).getSelectedValues()[i])).name + "\n");
		}
		
		StringTokenizer tokenizer = new StringTokenizer(buffer.toString(), "\n");
		int count = tokenizer.countTokens();
		
		while(count > 0)
		{
			count--;
			export += tokenizer.nextToken() + System.getProperty("line.separator");
		}
		
		transfer.setClipboardContents(export);*/

       
		if(action == MOVE)
		{
        	if(component instanceof PDJList)
	        {
	        	final PDJList pdjList = (PDJList)component;
	        	
	        	if(pdjList.getModel() instanceof EditableListModel)
	        	{
	        		final EditableListModel model = (EditableListModel)pdjList.getModel();
			        try
					{
			        	for(int i = pdjList.getSelectedIndices().length - 1; i >= 0; i--)
			        	{
			        		model.remove(pdjList.getSelectedIndices()[i], i > 0);
			        	}
						
					}
					catch (final ListException e)
					{
						e.printStackTrace();
					}
	        	}
	        }
        }
    }
}
	


