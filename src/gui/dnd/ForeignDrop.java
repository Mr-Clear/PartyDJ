package gui.dnd;

import gui.PDJList;
import gui.StatusDialog;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;

import common.Track;

public class ForeignDrop extends DropTargetAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent e) 
	{
		Transferable tr = e.getTransferable();
	    DataFlavor[] flavors = tr.getTransferDataFlavors();
	    for(DataFlavor flav : flavors)
	    {
	    	if(flav.isFlavorJavaFileListType());
		    {
		    	try 
		    	{
		    		e.acceptDrop(e.getDropAction());
		    		Object raw = tr.getTransferData(flav);
		    		if(raw instanceof List)
		    		{
		    			List data = (List)raw;
		    			for(int i = 0; i < data.size(); i++)
		    			{
		    				if(data.get(i) instanceof File)
		    				{
		    					String filePath = ((File)data.get(i)).getAbsolutePath();
		    					if(filePath.toLowerCase().endsWith(".m3u"))
						        {
						        	new StatusDialog("Lese M3U", null, new gui.settings.tools.AddM3U(filePath));
						        }
						        else
							    {
						        	if(e.getSource() instanceof DropTarget)
						        	{
						        		if(((DropTarget)e.getSource()).getComponent() instanceof PDJList)
						        		{
						        			PDJList list = (PDJList) ((DropTarget)e.getSource()).getComponent();
											ListProvider listProvider = new ListProvider();
											Track added = listProvider.assignTrack(new Track(filePath, true));
											
											switch(list.getListDropMode())
											{
												case NONE:			e.rejectDrop();
																	break;
																		
											}
											if(list.getListModel() instanceof EditableListModel)
											{
												((EditableListModel)list.getListModel()).add(added);
											}
											
						        		}
						        	}
									
							    }
		    				}
		    			}
		    		}
				} 
		    	catch (UnsupportedFlavorException e1) 
		    	{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
		    	catch (IOException e1) 
		    	{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
		    	catch (ListException e1) 
		    	{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
	    }
		
	}

}
