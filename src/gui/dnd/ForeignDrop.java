package gui.dnd;

import gui.PDJList;
import gui.StatusDialog;
import gui.settings.tools.AddMP3s;
import gui.settings.tools.RemoveMP3s;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import basics.Controller;
import lists.EditableListModel;
import lists.ListException;
import lists.TrackListModel;
import lists.data.DbMasterListModel;
import common.Track;

/**ForeignDrop kümmert sich um alle Drop-Importe.
 * Innerhalb der VM, sowie auch von außerhalb.
 * 
 * @author Sam
 * @date   15.05.09
 */
public class ForeignDrop extends DropTargetAdapter
{
	@Override
	public synchronized void drop(final DropTargetDropEvent e) 
	{
		int mp3s = 0;
		List<?> data = null;
		Transferable tr = e.getTransferable();
	    DataFlavor[] flavors = tr.getTransferDataFlavors();
	    
	    for(DataFlavor flav : flavors)
	    {
	    	if(flav.isFlavorJavaFileListType() && !e.isLocalTransfer())
		    {
		    	try 
		    	{
		    		e.acceptDrop(e.getDropAction());
		    		Object raw = tr.getTransferData(flav);
		    		if(raw instanceof List)
		    		{
		    			data = (List<?>)raw;
		    			for(int i = 0; i < data.size(); i++)
		    			{
		    				if(data.get(i) instanceof File)
		    				{
		    					String filePath = ((File)data.get(i)).getAbsolutePath();
		    					if(((File)data.get(i)).isDirectory())
		    					{
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							TrackListModel elm = ((PDJList) e.getDropTargetContext().getComponent()).getListModel();
		    							if(elm instanceof EditableListModel)
		    								new StatusDialog("Lese Ordner", null, new gui.settings.tools.ReadFolder(filePath, true, (EditableListModel) elm));
		    							else if(elm instanceof DbMasterListModel)
		    								new StatusDialog("Lese Ordner", null, new gui.settings.tools.ReadFolder(filePath, true));
		    							e.dropComplete(true);
		    						}
		    					}
		    					
		    					else if(filePath.toLowerCase().endsWith(".m3u"))
						        {
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							TrackListModel elm = ((PDJList)e.getDropTargetContext().getComponent()).getListModel();
		    							if(elm instanceof EditableListModel)
		    								new StatusDialog("Lese M3U", null, new gui.settings.tools.AddM3U(filePath, (EditableListModel) elm));
		    							else if(elm instanceof DbMasterListModel)
		    								new StatusDialog("Lese M3U", null, new gui.settings.tools.AddM3U(filePath));
							        	e.dropComplete(true);
							        	return;
		    						}
						        }
		    					else if(e.getDropTargetContext().getComponent() instanceof PDJList && filePath.toLowerCase().endsWith(".mp3"))
								{
									mp3s++;
									e.dropComplete(true);
								}
								else
								{
									e.dropComplete(false);
									return;
								}
		    				}
				        	else
				        		e.dropComplete(false);
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
		    }
	    	else if(flav.equals(new DataFlavor(Track.class, "Track flavor")))
	    	{
    			e.acceptDrop(e.getDropAction());
    			Transferable transfer = e.getTransferable();
				Track[] tracks = null;
				try
				{
	    			if(transfer.getTransferData(flav) instanceof Track[])
	    			{
						tracks = (Track[]) transfer.getTransferData(flav);
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

				if(tracks == null)
				{
					e.dropComplete(false);
					return;
				}
				
				if(e.getDropTargetContext().getComponent() instanceof PDJList)
				{
					PDJList list = (PDJList) e.getDropTargetContext().getComponent();
					
					if(list.getListDropMode() == null)
					{
						e.dropComplete(false);
						return;
					}

					switch(list.getListDropMode())
					{
					case NONE:			e.dropComplete(false);
										break;
					case MOVE:			Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, this, null, "MOVE not supported.");
										e.dropComplete(false);
										break;
					case DELETE:		if(DragListener.getList() != null && DragListener.getList().getListModel() instanceof EditableListModel)
										{
											new StatusDialog("Entferne MP3s", null, new RemoveMP3s(DragListener.getList()));
											e.dropComplete(true);
										}
										break;
					case COPY:
					case COPY_OR_MOVE:	if(list.getListModel() instanceof EditableListModel)
										{
											if(!list.equals(DragListener.getList()))
											{
												List<Track> toAdd = new ArrayList<Track>();
												for(Track track : tracks)
													toAdd.add(track);
												
												//FIXME StatusDialog zum laufen bringen.
												new StatusDialog("Füge MP3s ein.", null, new AddMP3s(e, toAdd, toAdd.size()));
												e.dropComplete(true);
											}
											else
											{
												EditableListModel elm = (EditableListModel)list.getListModel();
												try
												{
													int addIndex = e.getLocation().y / list.getFixedCellHeight();
													for(int i = list.getSelectedIndices().length; i > 0; i--)
													{
														if(list.getSelectedIndices()[i-1] < addIndex)
															addIndex--;
														elm.remove(list.getSelectedIndices()[i-1]);
													}
						
													for(int i = tracks.length; i > 0; i--)
													{
														elm.add(addIndex, tracks[i - 1]);
													}
													e.dropComplete(true);
												}
												catch (ListException e1)
												{
													e.dropComplete(false);
													e1.printStackTrace();
												}
											}
										}
										break;
					}
				}
				else if(e.getDropTargetContext().getComponent() instanceof JTextField)
				{
					if(DragListener.getList() == null)
					{
	    				final JTextField txtField = (JTextField) e.getDropTargetContext().getComponent();
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run()
							{
								txtField.setText(Controller.getInstance().getPlayer().getCurrentTrack().getName());
								e.dropComplete(true);
							}});
					}
					else if(DragListener.getList().getSelectedIndices().length == 1)
	    			{
	    				final Track firstTrack = tracks[0];
	    				final JTextField txtField = (JTextField) e.getDropTargetContext().getComponent();
	    				SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run()
							{
								txtField.setText(firstTrack.getName());
								e.dropComplete(true);
							}});
						
	    			}
	    			else
	    				e.dropComplete(false);
				}
	    	}
	    	e.dropComplete(false);
	    }
	    if(mp3s > 0 && data != null)
	    {
	    	new StatusDialog("Füge MP3s ein.", null, new AddMP3s(e, data, mp3s));
			e.dropComplete(true);
	    }
	    if(e.getDropTargetContext().getComponent() instanceof PDJList)
		{
	    	SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					PDJList list = (PDJList) e.getDropTargetContext().getComponent();
					list.ensureIndexIsVisible(e.getLocation().y / list.getFixedCellHeight());
				}});
		}
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		if(dtde.getDropTargetContext().getComponent() instanceof PDJList)
		{
			PDJList dropList = (PDJList) dtde.getDropTargetContext().getComponent(); 
			PDJList dragList = DragListener.getList();
						
			if(dropList.getListDropMode() == null || dropList.getListDropMode() == ListDropMode.NONE)
			{
				dtde.rejectDrag();
				return;
			}
			if(dropList.equals(dragList))
				dtde.acceptDrag(DnDConstants.ACTION_MOVE);
			else
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
		}
		else if(dtde.getDropTargetContext().getComponent() instanceof JTextField)
		{
			JTextField txtField = (JTextField) dtde.getDropTargetContext().getComponent();
			if(txtField.getDropMode() == DropMode.USE_SELECTION)
			{
				dtde.rejectDrag();
				return;
			}
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		}
		else
			dtde.rejectDrag();
	}
}
