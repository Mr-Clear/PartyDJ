package de.klierlinge.partydj.gui.dnd;

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
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.settings.tools.AddMP3s;
import de.klierlinge.partydj.gui.settings.tools.RemoveMP3s;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.TrackListModel;
import de.klierlinge.partydj.lists.data.DbMasterListModel;

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
		final Transferable tr = e.getTransferable();
	    final DataFlavor[] flavors = tr.getTransferDataFlavors();
	    
	    for(final DataFlavor flav : flavors)
	    {
	    	if(flav.isFlavorJavaFileListType() && !e.isLocalTransfer())
		    {
		    	try 
		    	{
		    		e.acceptDrop(e.getDropAction());
		    		final Object raw = tr.getTransferData(flav);
		    		if(raw instanceof List<?>)
		    		{
		    			data = (List<?>)raw;
		    			for(int i = 0; i < data.size(); i++)
		    			{
		    				if(data.get(i) instanceof File)
		    				{
		    					final String filePath = ((File)data.get(i)).getAbsolutePath();
		    					if(((File)data.get(i)).isDirectory())
		    					{
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							final TrackListModel elm = ((PDJList) e.getDropTargetContext().getComponent()).getListModel();
		    							if(elm instanceof EditableListModel)
		    								new StatusDialog("Lese Ordner", null, new de.klierlinge.partydj.gui.settings.tools.ReadFolder(filePath, true, (EditableListModel) elm));
		    							else if(elm instanceof DbMasterListModel)
		    								new StatusDialog("Lese Ordner", null, new de.klierlinge.partydj.gui.settings.tools.ReadFolder(filePath, true));
		    							e.dropComplete(true);
		    						}
		    					}
		    					
		    					else if(filePath.toLowerCase().endsWith(".m3u"))
						        {
		    						if(e.getDropTargetContext().getComponent() instanceof PDJList)
		    						{
		    							final TrackListModel elm = ((PDJList)e.getDropTargetContext().getComponent()).getListModel();
		    							if(elm instanceof EditableListModel)
		    								new StatusDialog("Lese M3U", null, new de.klierlinge.partydj.gui.settings.tools.AddM3U(filePath, (EditableListModel) elm));
		    							else if(elm instanceof DbMasterListModel)
		    								new StatusDialog("Lese M3U", null, new de.klierlinge.partydj.gui.settings.tools.AddM3U(filePath));
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
				catch (final UnsupportedFlavorException e1)
				{
					Controller.getInstance().logError(Controller.REGULAR_ERROR, this, e1, "Unbekannter Datentyp per DnD eingefügt.");
				}
				catch (final IOException e1)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e1, "Datenzugriff bei DnD fehlgeschlagen.");
				}
		    }
	    	else if(flav.equals(new DataFlavor(Track.class, "Track flavor")))
	    	{
    			e.acceptDrop(e.getDropAction());
    			final Transferable transfer = e.getTransferable();
				Track[] tracks = null;
				try
				{
	    			if(transfer.getTransferData(flav) instanceof Track[])
	    			{
						tracks = (Track[]) transfer.getTransferData(flav);
	    			}
				}
				catch (final UnsupportedFlavorException e1)
				{
					Controller.getInstance().logError(Controller.REGULAR_ERROR, this, e1, "Unbekannter Datentyp per DnD eingefügt.");
				}
				catch (final IOException e1)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e1, "Datenzugriff bei DnD fehlgeschlagen.");
				}

				if(tracks == null)
				{
					e.dropComplete(false);
					return;
				}
				
				if(e.getDropTargetContext().getComponent() instanceof PDJList)
				{
					final PDJList list = (PDJList) e.getDropTargetContext().getComponent();
					
					if(list.getListDropMode() == null)
					{
						e.dropComplete(false);
						return;
					}

					switch(list.getListDropMode())
					{
					case NONE:
						e.dropComplete(false);
						break;
					case MOVE:
						Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, this, null, "MOVE not supported.");
						e.dropComplete(false);
						break;
					case DELETE:
						if(DragListener.getList() != null && DragListener.getList().getListModel() instanceof EditableListModel)
						{
							new StatusDialog("Entferne MP3s", null, new RemoveMP3s(DragListener.getList()));
							e.dropComplete(true);
						}
						break;
					case COPY:
					case COPY_OR_MOVE:
						if(list.getListModel() instanceof EditableListModel)
						{
							if(!list.equals(DragListener.getList()))
							{
								final List<Track> toAdd = new ArrayList<>();
								for(final Track track : tracks)
									toAdd.add(track);
								
								//FIXME StatusDialog zum laufen bringen.
								new StatusDialog("Füge MP3s ein.", null, new AddMP3s(e, toAdd, toAdd.size()));
								e.dropComplete(true);
							}
							else
							{
								final EditableListModel elm = (EditableListModel)list.getListModel();
								try
								{
									int addIndex = e.getLocation().y / list.getFixedCellHeight();
									for(int i = list.getSelectedIndices().length - 1; i >= 0; i--)
									{
										if(list.getSelectedIndices()[i] < addIndex)
											addIndex--;
										elm.remove(list.getSelectedIndices()[i], i > 0);
									}
		
									for(int i = tracks.length - 1; i >= 0; i--)
									{
										elm.add(addIndex, tracks[i], i > 0 );
									}
									e.dropComplete(true);
								}
								catch (final ListException e1)
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
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override public void run()
							{
								txtField.setText(Controller.getInstance().getPlayer().getCurrentTrack().getName());
								e.dropComplete(true);
							}
						});
					}
					else if(DragListener.getList().getSelectedIndices().length == 1)
	    			{
	    				final Track firstTrack = tracks[0];
	    				final JTextField txtField = (JTextField) e.getDropTargetContext().getComponent();
	    				SwingUtilities.invokeLater(new Runnable()
	    				{
							@Override public void run()
							{
								txtField.setText(firstTrack.getName());
								e.dropComplete(true);
							}
						});
						
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
	    	SwingUtilities.invokeLater(new Runnable()
	    	{
				@Override public void run()
				{
					final PDJList list = (PDJList) e.getDropTargetContext().getComponent();
					list.ensureIndexIsVisible(e.getLocation().y / list.getFixedCellHeight());
				}
			});
		}
	}
	
	@Override
	public void dragEnter(final DropTargetDragEvent dtde)
	{
		if(dtde.getDropTargetContext().getComponent() instanceof PDJList)
		{
			final PDJList dropList = (PDJList) dtde.getDropTargetContext().getComponent(); 
			final PDJList dragList = DragListener.getList();
						
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
			final JTextField txtField = (JTextField) dtde.getDropTargetContext().getComponent();
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
