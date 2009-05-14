package gui.dnd;

import gui.DragGestureList;
import gui.PDJList;
import gui.StatusDialog;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JTextField;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;

import common.Track;

public class ForeignDrop extends DropTargetAdapter 
{
	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent e) 
	{
		Transferable tr = e.getTransferable();
	    DataFlavor[] flavors = tr.getTransferDataFlavors();
	    for(DataFlavor flav : flavors)
	    {
	    	if(flav.isFlavorJavaFileListType() && DragGestureList.getList() == null)
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
		    						//TODO Welche Liste? readM3u statt add
						        	new StatusDialog("Lese M3U", null, new gui.settings.tools.AddM3U(filePath));
						        	e.dropComplete(true);
						        	return;
						        }
						        else
							    {
						        	if(e.getSource() instanceof DropTarget)
						        	{
						        		if(e.getDropTargetContext().getComponent() instanceof PDJList)
						        		{
						        			PDJList list = (PDJList) e.getDropTargetContext().getComponent();
											ListProvider listProvider = new ListProvider();
											Track added = listProvider.assignTrack(new Track(filePath, true));
											
											if(list.getListDropMode() == null)
											{
												e.dropComplete(false);
												return;
											}
											
											switch(list.getListDropMode())
											{
												case NONE:			e.rejectDrop();
																	break;
																		
											}
											if(list.getListModel() instanceof EditableListModel)
											{
												((EditableListModel)list.getListModel()).add(added);
											}
											e.dropComplete(true);
						        		}
						        	}
						        	else
						        	{
						        		e.dropComplete(false);
						        		return;
						        	}
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
		    	catch (ListException e1) 
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
					return;
				
				if(e.getDropTargetContext().getComponent() instanceof PDJList)
				{
					PDJList list = (PDJList) e.getDropTargetContext().getComponent();
					
					if(list.getListDropMode() == null)
						return;
					
					switch(list.getListDropMode())
					{
					case NONE:			break;
					case MOVE:			System.out.println("MOVE not supported!"); //TODO Dialog
					case DELETE:		if(DragGestureList.getList().getListModel() instanceof EditableListModel)
										{
											try
											{
												EditableListModel elm = (EditableListModel)DragGestureList.getList().getListModel();
												if(tracks.length == 1)
													elm.remove(DragGestureList.getList().getSelectedIndex());
												else
												{
													for(int i = 0; i < tracks.length; i++)
													{
														elm.remove(DragGestureList.getList().getSelectedIndices()[0]);
													}
												}
												e.dropComplete(true);
											}
											catch (ListException e1)
											{
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
										}
										break;
					case COPY_OR_MOVE:	if(list.getListModel() instanceof EditableListModel)
										{
											if(!list.equals(DragGestureList.getList()))
											{
												EditableListModel elm = (EditableListModel)list.getListModel();
												try
												{
													if(tracks.length == 1)
													{
														elm.add(DragGestureList.getList().getListModel().getElementAt(DragGestureList.getList().getSelectedIndex()));
													}
													else
													{
														for(int i = 0; i < tracks.length; i++)
														{
															elm.add(DragGestureList.getList().getListModel().getElementAt(DragGestureList.getList().getSelectedIndices()[i]));
														}
													}
													e.dropComplete(true);
												}
												catch (ListException e1)
												{
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
											}
											else
											{
												EditableListModel elm = (EditableListModel)list.getListModel();
												try
												{
													int addIndex = e.getLocation().y / list.getFixedCellHeight() + list.getFirstVisibleIndex();
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
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
											}
											
										}
										break;
					}
				}
				else if(e.getDropTargetContext().getComponent() instanceof JTextField)
				{
	    			if(DragGestureList.getList().getSelectedIndices().length == 1)
	    			{
	    				JTextField txtField = (JTextField) e.getDropTargetContext().getComponent();
	    			
						txtField.setText(tracks[0].name);
						e.dropComplete(true);
	    			}
	    			else
	    				e.dropComplete(false);
				}
	    	}
	    	e.dropComplete(false);
	    }
	}
	
	public void dragEnter(DropTargetDragEvent dtde)
	{
		if(dtde.getDropTargetContext().getComponent() instanceof PDJList)
		{
			PDJList dropList = (PDJList) dtde.getDropTargetContext().getComponent(); 
			PDJList dragList = DragGestureList.getList();
				
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
			else
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
		}
		else
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
	}

}
