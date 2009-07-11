package gui.dnd;

import gui.PDJList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import basics.Controller;
import lists.EditableListModel;
import lists.ListException;
import common.Track;

/**
 * 
 * @author Sam
 */
public class DragDropHandler extends TransferHandler
{
	private static final long serialVersionUID = 6601023550058648978L;
	
	@Override
	public boolean canImport(TransferHandler.TransferSupport info)
	{
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
			return false;
		
        return true;
	}
	
	public synchronized boolean importData(PDJList list, Transferable transferable)
	{
		if (!transferable.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
		{
			Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Drop mit unsupported flavor.");
			return false;
		}
		
		Object[] data = null;
		PDJList pdjList = list;
		ListModel listModel = pdjList.getModel();
		
		try
		{
			data = (Object[])transferable.getTransferData(new DataFlavor(Track.class, "Track flavor"));
		}
		catch (UnsupportedFlavorException e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e, "Drop mit unsupported flavor.");
			return false;
		}
		catch (IOException e)
		{
			Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Drop.");
			return false;
		}
		
		switch (list.getListDropMode())
		{
		case NONE:					break;
		
		case COPY:					try
									{
										for(int i = data.length; i > 0; i--)
										{
											((EditableListModel)listModel).add(list.getSelectedIndex() + 1, (Track)data[i-1]);
										}	
									}
									catch (ListException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
					
		case MOVE:					System.out.println("MOVE not supported");
									break;
									
		case DELETE:				break;
									
		case COPY_OR_MOVE:			try
									{
										for(int i = data.length; i > 0; i--)
										{
											((EditableListModel)listModel).add(list.getSelectedIndex() + 1, (Track)data[i-1]);
										}	
									}
									catch (ListException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
		}
		return true;
	}
	
	@Override
	public synchronized boolean importData(TransferHandler.TransferSupport info)
	{
		Object[] data = null;
		
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
		{
			Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Drop mit unsupported flavor.");
			return false;
		}
	
		if(info.getComponent() instanceof PDJList)
		{
			PDJList pdjList = (PDJList)info.getComponent();
			ListModel listModel = pdjList.getModel();
			
			try
			{
				data = (Object[])info.getTransferable().getTransferData(new DataFlavor(Track.class, "Track flavor"));
			}
			catch (UnsupportedFlavorException e)
			{
				Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e, "Drop mit unsupported flavor.");
				return false;
			}
			catch (IOException e)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Drop.");
				return false;
			}
			
			if(!info.isDrop()) 
	        {
					for(int i = data.length; i > 0; i--)
					{
						try
						{
							((EditableListModel)listModel).add((Track)data[i-1]);
						}
						catch (ListException e)
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
							Controller.getInstance().getListProvider().getDbList("Wunschliste").add(tracks[i]);
						}
						catch (ListException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}
			catch (UnsupportedFlavorException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
			
	}
	
	@Override
	public Transferable createTransferable(JComponent c)
	{
		PDJList pdjList = (PDJList)c;
		Track[] values = pdjList.getSelectedValues();

		return new TrackSelection(values);
	}
	
	@Override
	public int getSourceActions(JComponent c)
	{
		 return COPY;
	}
	
	@Override
	public void exportDone(JComponent component, Transferable data, int action) 
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
	        	PDJList pdjList = (PDJList)component;
	        	
	        	if(pdjList.getModel() instanceof EditableListModel)
	        	{
	        		EditableListModel model = (EditableListModel)pdjList.getModel();
			        try
					{
			        	for(int i = pdjList.getSelectedIndices().length; i > 0; i--)
			        	{
			        		model.remove(pdjList.getSelectedIndices()[i-1]);
			        	}
						
					}
					catch (ListException e)
					{
						e.printStackTrace();
					}
	        	}
	        }
        }
    }
}
	


