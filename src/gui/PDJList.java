package gui;

import gui.dnd.DragDropHandler;
import gui.dnd.DragListener;
import gui.dnd.ForeignDrop;
import gui.dnd.ListDropMode;
import gui.dnd.TrackSelection;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import players.PlayStateAdapter;
import players.PlayerException;
import basics.Controller;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;
import lists.TrackListModel;
import common.Track;

/**
 * Von JList abgeleitete Liste, die Tracks aufnimmt.
 * <br>Von den Tracks wird der Name und die Dauer angezeigt.
 * <br>Unterstützt Drag and Drop.
 * 
 * @author Sam, Eraser
 * 
 * @see TrackRenderer
 */
public class PDJList extends JList
{
	protected static final long serialVersionUID = -8653111853374564564L;
	protected ListDropMode listDropMode;
	protected final TrackListModel listModel;
	protected int count = 0;
	protected PDJList list = this;
	protected final TrackRenderer renderer = new TrackRenderer();
	protected boolean scrollToPlayed = true;
	
	public PDJList(TrackListModel listModel)
	{
		super(listModel);
		this.listModel = listModel;
		initialise(listDropMode, null);
	}
	
	public PDJList(TrackListModel listModel, final ListDropMode ldMode, final String name)
	{
		super(listModel);
		this.listModel = listModel;
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run()
			{
				initialise(ldMode, name);
			}});
		
	}
	
	private void initialise(ListDropMode ldMode, String name)
	{
		final DragDropHandler handler = new DragDropHandler();
		
		this.setName(name);
		this.setListDropMode(ldMode);
		this.setTransferHandler(handler);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setDragEnabled(true);
		this.addMouseMotionListener(new DragMotionListener());
		this.addMouseListener(new ClickListener());
		Controller.getInstance().getPlayer().addPlayStateListener(new PlayerListenerForLists());

		
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"),TransferHandler.getCutAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"),TransferHandler.getCopyAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"),TransferHandler.getPasteAction().getValue(Action.NAME));
		
		this.getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction(){
																	private static final long serialVersionUID = -3363943247433508388L;
																	@Override
																	public void actionPerformed(ActionEvent e)
																	{
																		TrackTransfer transfer = new TrackTransfer();
																		transfer.setClipboardContents(list.getSelectedValues());
																		
																		new DragDropHandler().exportDone(list, new TrackSelection(list.getSelectedValues()),  javax.swing.TransferHandler.MOVE );
																	}});
		this.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
		this.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());

		this.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "Delete");
		this.getActionMap().put("Delete", new AbstractAction("Delete") 
											{
												private static final long serialVersionUID = -2342506838333821595L;

												public void actionPerformed(ActionEvent evt) 
            									{
            										PDJList pdjList = (PDJList)evt.getSource();
													if(evt.getSource() instanceof PDJList)
													{
														if(pdjList.getModel() instanceof EditableListModel)
														{
		            								        EditableListModel elm = (EditableListModel)pdjList.getModel();
		            								        int[] indices = pdjList.getSelectedIndices();
		            								 
		            								        	for(int i = indices.length; i > 0; i--)
		            											{
		            												try
		            												{
		            													elm.remove(indices[i-1]);
		            												}
		            												catch (ListException e){}
		            											}
														}
														
														else if(pdjList.getModel() instanceof DbMasterListModel)
														{
															int[] indices = pdjList.getSelectedIndices();
															for(int i = indices.length; i > 0; i--)
	            											{
	            												try
	            												{
	            													Controller.getInstance().getData().deleteTrack(listModel.getElementAt(indices[i-1]));
	            												}
	            												catch (ListException e){}
	            											}
															
														}
													}
            									}
											});
		
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Return");
		this.getActionMap().put("Return", new AbstractAction("Return") 
								{
									private static final long serialVersionUID = -2342506838333821595L;
									public void actionPerformed(ActionEvent evt) 
									{
										Track track = ((Track)((PDJList)evt.getSource()).getSelectedValue());
										if(track != null)
										{
											try
											{
												Controller.getInstance().getPlayer().start(track);
											}
											catch (PlayerException e)
											{
												e.printStackTrace();
												JOptionPane.showMessageDialog(null, "Track kann nicht wiedergegeben werden:\n" + track, "PartyDJ", JOptionPane.ERROR_MESSAGE);
											}
										}
									}
								});
		
		this.setCellRenderer(renderer);
		this.setPrototypeCellValue("123-45-6789");
		
		DragListener dgl = new DragListener();

		new DropTarget(this, new ForeignDrop());
		DragSource dragSource = new DragSource();
		dragSource.addDragSourceListener(dgl);
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dgl);
		
		scrollToPlayed(Controller.getInstance().getPlayer().getCurrentTrack());
	}
	
	public void setScrollToPlayedEnabled(boolean b)
	{
		scrollToPlayed = b;
	}

	public void setListDropMode(ListDropMode ldMode)
	{
		this.listDropMode = ldMode;
	}

	public ListDropMode getListDropMode()
	{
		return listDropMode;
	}

	public TrackListModel getListModel()
	{
		return listModel;
	}
	
	public void setFontSize(int point)
	{
		renderer.setFontSize(point);
	}
	
	public Track getLastTrack()
	{
		return listModel.getElementAt(listModel.getSize());
	}
	
    @Override
	public Track[] getSelectedValues()
    {    	
        ListSelectionModel sm = getSelectionModel();

        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0))
        {
            return new Track[0];
        }

        Track[] rvTmp = new Track[1 + (iMax - iMin)];
        int n = 0;
        for(int i = iMin; i <= iMax; i++)
        {
            if (sm.isSelectedIndex(i))
            {
                rvTmp[n++] = listModel.getElementAt(i);
            }
        }
        Track[] rv = new Track[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }
    
    protected void scrollToPlayed(final Track playingCurrent)
    {
    	if(!scrollToPlayed)
    		return;
    	
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				int index = listModel.getIndex(playingCurrent);
				if(index != -1)
				{
					int span = list.getLastVisibleIndex() - list.getFirstVisibleIndex();
					final Rectangle cellBound = getCellBounds(Math.max(index - span / 2, 0), Math.min(index + span / 2, listModel.getSize() - 1));
			        if (cellBound != null)
			        	scrollRectToVisible(cellBound);
				}
			}});
    }
    
    protected class PlayerListenerForLists extends PlayStateAdapter
	{
		@Override
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
		{
			if(reason == Reason.RECEIVED_NEW_TRACK || reason == Reason.TRACK_LOADED && scrollToPlayed)
				scrollToPlayed(playingCurrent);
		}
	}
    	
	private class DragMotionListener extends MouseMotionAdapter
	{
		private int startIndex;
		private int listSize;
		private int index;
		
		@Override
		public void mouseDragged(final MouseEvent dge)
		{	
			if(SwingUtilities.isMiddleMouseButton(dge))
			{
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run()
					{
						if(dge.getComponent() instanceof PDJList)
						{
							PDJList pdjList = ((PDJList)dge.getComponent());
							listSize = pdjList.getModel().getSize();
							index = dge.getY() / pdjList.getFixedCellHeight();
							
							if(index > pdjList.getLastVisibleIndex())
							{
								index = pdjList.getLastVisibleIndex();
							}
							if(index < 0)
							{
								index = 0;
							}
							if(index >= 0)
							{
								if(count == 0)
								{
									if(index > listSize)
										startIndex = listSize - 1;
									
									else
										startIndex = index;
								}
								
								pdjList.ensureIndexIsVisible(index);
								
								if(index == startIndex)
									pdjList.setSelectedIndex(index);
								
								if(startIndex < index)
								{
									pdjList.setSelectionInterval(index, startIndex);
									pdjList.ensureIndexIsVisible(index + 2);
								}
								
								if(index < startIndex)
								{
									pdjList.setSelectionInterval(startIndex, index);
									pdjList.ensureIndexIsVisible(index - 2);
								}
							}
							
							if(index < 0)
							{
								if(count == 0)
								{
									startIndex = 0;
								}
								
								if(startIndex > index)
								{
									pdjList.setSelectionInterval(startIndex, 0);
									pdjList.ensureIndexIsVisible(0);
								}
							}
						}	
						count++;
					}});
			}
		}
	}
	
	private class ClickListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(final MouseEvent e)
		{
			final PDJList clickedList;
			if(!(e.getSource() instanceof PDJList))
				return;
			clickedList = (PDJList)e.getSource();
			
			if(SwingUtilities.isRightMouseButton(e))
			{
				//Alle Fenster eintragen, die rechten Mausklick unterstützen sollen.
				if(((PDJList)e.getSource()).getTopLevelAncestor() instanceof ClassicWindow)
				{
					synchronized(clickedList)
					{
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run()
							{
								try
								{
									if(e.getY() / clickedList.getFixedCellHeight() <= clickedList.getLastVisibleIndex())
									{
										if(clickedList.getSelectedIndex() == -1)
										{
											clickedList.setSelectedIndex(e.getY() / clickedList.getFixedCellHeight());
										}

										for(int i = clickedList.getSelectedIndices().length; i > 0; i--)
										{
											if(e.getY() / clickedList.getFixedCellHeight() == clickedList.getSelectedIndices()[i-1])
											{
												if(clickedList.getSelectedValue() != null)
												{
													PopupMenuGenerator.listPopupMenu(clickedList, (Track)clickedList.getSelectedValue()).show(clickedList, e.getX(), e.getY());
													return;
												}
											}
										}
										clickedList.setSelectedIndex(e.getY() / clickedList.getFixedCellHeight());
										PopupMenuGenerator.listPopupMenu(clickedList, (Track)clickedList.getSelectedValue()).show(clickedList, e.getX(), e.getY());
									}
									else
										PopupMenuGenerator.listPopupMenu(clickedList, null).show(clickedList, e.getX(), e.getY());
								}
								catch (IndexOutOfBoundsException ex)
								{
									return;
								}
							}});
					}
				}
				
			}

			if(SwingUtilities.isLeftMouseButton(e))
			{
				if(e.getClickCount() == 2)
				{
					int clickIndex = e.getY() / clickedList.getFixedCellHeight();
					
					if(clickedList.getSelectedIndex() != clickIndex)
						return;
					if(!(clickedList.getSelectedValue() instanceof Track))
						return;
					
					Track track = (Track) clickedList.getSelectedValue();
					try
					{
						Controller.getInstance().getPlayer().start(track);
					}
					catch (PlayerException e1)
					{
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Track kann nicht wiedergegeben werden:\n" + track, "PartyDJ", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			count = 0;
		}	
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if(e.getComponent() instanceof PDJList)
			{
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run()
					{
						PDJList clickedList = (PDJList) e.getComponent();
						if(clickedList.getSelectedIndices().length == 0)
							clickedList.setSelectedIndex(e.getY() / clickedList.getFixedCellHeight());
					}});
				
			}
		}
	}
}

