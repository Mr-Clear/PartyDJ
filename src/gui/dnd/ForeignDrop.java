package gui.dnd;

import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import basics.Controller;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;
import lists.TrackListModel;

import common.Track;

/**ForeignDrop kümmert sich um alle Drop-Importe.
 * Innerhalb der VM, sowie auch von außerhalb.
 * 
 * @author Sam
 * @date   15.05.09
 */
@SuppressWarnings("unused")
public class ForeignDrop extends DropTargetAdapter
{
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void drop(final DropTargetDropEvent e) 
	{
		int mp3s = 0;
		List data = null;
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
		    			data = (List)raw;
		    			for(int i = 0; i < data.size(); i++)
		    			{
		    				if(data.get(i) instanceof File)
		    				{
		    					String filePath = ((File)data.get(i)).getAbsolutePath();
		    					if(((File)data.get(i)).isDirectory())
		    					{
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							TrackListModel tlm = ((PDJList) e.getDropTargetContext().getComponent()).getListModel();
		    							if(tlm instanceof EditableListModel)
		    								new StatusDialog("Lese Ordner", null, new gui.settings.tools.ReadFolder(filePath, true, (EditableListModel) tlm));
		    							else if(tlm instanceof DbMasterListModel)
		    								new StatusDialog("Lese Ordner", null, new gui.settings.tools.ReadFolder(filePath, true));
		    							e.dropComplete(true);
		    							return;
		    						}
		    					}
		    			    	
		    					if(filePath.toLowerCase().endsWith(".m3u"))
						        {
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							TrackListModel tlm = ((PDJList) e.getDropTargetContext().getComponent()).getListModel();
		    							if(tlm instanceof EditableListModel)
		    								new StatusDialog("Lese M3U", null, new gui.settings.tools.AddM3U(filePath, (EditableListModel) tlm));
		    							else if(tlm instanceof DbMasterListModel)
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
										break;
					case DELETE:		if(DragListener.getList().getListModel() instanceof EditableListModel)
										{
											try
											{
												EditableListModel elm = (EditableListModel)DragListener.getList().getListModel();
												if(tracks.length == 1)
													elm.remove(DragListener.getList().getSelectedIndex());
												else
												{
													for(int i = 0; i < tracks.length; i++)
													{
														elm.remove(DragListener.getList().getSelectedIndices()[0]);
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
					case COPY:
					case COPY_OR_MOVE:	if(list.getListModel() instanceof EditableListModel)
										{
											if(!list.equals(DragListener.getList()))
											{
												int addIndex = e.getLocation().y / list.getFixedCellHeight() + list.getFirstVisibleIndex();
												EditableListModel elm = (EditableListModel)list.getListModel();
												TrackListModel dragLM = DragListener.getList().getListModel();
												try
												{
													if(tracks.length == 1)
													{
														elm.add(addIndex, dragLM.getElementAt(DragListener.getList().getSelectedIndex()));
													}
													else
													{
														for(int i = 0; i < tracks.length; i++)
														{
															elm.add(addIndex, dragLM.getElementAt(DragListener.getList().getSelectedIndices()[i]));
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
	    			if(DragListener.getList().getSelectedIndices().length == 1)
	    			{
	    				final Track firstTrack = tracks[0];
	    				final JTextField txtField = (JTextField) e.getDropTargetContext().getComponent();
	    				SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run()
							{
								txtField.setText(firstTrack.name);
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
	    	new StatusDialog("Füge MP3s ein.", null, new addMP3s(e, data, mp3s));
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
	
	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
		/*if(dtde.getDropTargetContext().getComponent() instanceof PDJList)
		{
			count++;
			PDJList list = (PDJList) dtde.getDropTargetContext().getComponent();
			Graphics g = list.getGraphics();
			int loc = dtde.getLocation().y;
			int index = loc / list.getFixedCellHeight();
			int delta = loc - (index * list.getFixedCellHeight());
			if(delta > 21 || delta < 9 && index > list.getListModel().getSize())
			{
				g.setColor(Color.RED);
				g.fillRect(0, loc, list.getWidth(), 3);
				if(count > 6)
				{
					count = 0;
					list.repaint();
				}
			}
			else if(count > 6)
			{
				count = 0;
				list.repaint();
			}
		}*/
	}
	
	protected class addMP3s implements StatusSupportedFunction
	{
		protected final DropTargetDropEvent  dtde;
		protected int j;
		@SuppressWarnings("unchecked")
		protected List data;
		protected boolean goOn = true;
		
		@SuppressWarnings("unchecked")
		public addMP3s(DropTargetDropEvent dropTargetDropEvent, List mp3s, int toAdd)
		{
			dtde = dropTargetDropEvent;
			data = mp3s;
			j = toAdd;
		}
		@Override
		public void runFunction(StatusDialog sd)
		{
			int count = 0;
			sd.setBarMaximum(j);
			try
			{
				for(int i = 0; i < data.size() && goOn; i++)
				{
					if(data.get(i) instanceof File && !((File)data.get(i)).isDirectory())
					{
						String filePath = ((File)data.get(i)).getAbsolutePath();
						
						if(!filePath.toLowerCase().endsWith(".mp3"))
							break;
						
						PDJList list = (PDJList) dtde.getDropTargetContext().getComponent();
						ListProvider listProvider = new ListProvider();
						
						if(list.getListDropMode() == null)
						{
							dtde.dropComplete(false);
							return;
						}
						
						if(list.getListModel() instanceof EditableListModel)
						{
							((EditableListModel)list.getListModel()).add(listProvider.assignTrack(new Track(filePath, false)));
							count++;
						}
						else if(list.getListModel() instanceof DbMasterListModel)
							if(Controller.getInstance().getData().addTrack(new Track(filePath, false)) == -1)
								count++;

						sd.setLabel(count + ": " + filePath);
						sd.setBarPosition(count);
					}
				}
			}
			catch(ListException le)
			{
				le.printStackTrace();
			}
			sd.stopTimer();
			JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
		}

		@Override
		public void stopTask()
		{
			goOn = false;
		}
	}

}
