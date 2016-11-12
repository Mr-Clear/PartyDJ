package gui;

import basics.Controller;
import common.Track;
import gui.TrackListAppearance.EntryState;
import gui.TrackListAppearance.Part;
import gui.TrackListAppearance.TrackState;
import gui.dnd.DragDropHandler;
import gui.dnd.DragListener;
import gui.dnd.ForeignDrop;
import gui.dnd.ListDropMode;
import gui.dnd.TrackSelection;
import gui.settings.tools.RemoveMP3s;
import players.PlayStateAdapter;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import lists.TrackListModel;

/**
 * Von JList abgeleitete Liste, die Tracks aufnimmt.
 * <br>Von den Tracks wird der Name und die Dauer angezeigt.
 * <br>Unterstützt Drag and Drop.
 * 
 * @author Sam, Eraser
 * 
 * @see TrackRenderer
 */
public class PDJList extends JList<Track>
{
	protected static final long serialVersionUID = -8653111853374564564L;
	protected ListDropMode listDropMode;
	protected final TrackListModel listModel;
	protected int count = 0;
	protected final TrackRenderer renderer = new TrackRenderer();
	protected boolean scrollToPlayed = true;
	protected TrackListAppearance appearance;
	
	public PDJList(final TrackListModel listModel)
	{
		super(listModel);
		this.listModel = listModel;
		initialise(ListDropMode.NONE, null);
	}
	
	public PDJList(final TrackListModel listModel, final ListDropMode ldMode, final String name)
	{
		super(listModel);
		this.listModel = listModel;
		initialise(ldMode, name);
	}

	private void initialise(final ListDropMode ldMode, final String name)
	{
		final Controller controller = Controller.getInstance();
		
		this.setName(name);
		this.setListDropMode(ldMode);
		this.setTransferHandler(new DragDropHandler());
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setDragEnabled(true);
		this.addMouseMotionListener(new DragMotionListener());
		this.addMouseListener(new ClickListener());
		controller.getPlayer().addPlayStateListener(new PlayerListenerForLists());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler.getCutAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));
		
		this.getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction()
		{
			private static final long serialVersionUID = -3363943247433508388L;
			@Override public void actionPerformed(final ActionEvent e)
			{
				final TrackTransfer transfer = new TrackTransfer();
				transfer.setClipboardContents(PDJList.this.getSelectedValuesList());
				
				new DragDropHandler().exportDone(PDJList.this, new TrackSelection(PDJList.this.getSelectedValuesList()),  javax.swing.TransferHandler.MOVE);
			}
		});
		this.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
		this.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());

		this.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "Delete");
		this.getActionMap().put("Delete", new AbstractAction("Delete") 
		{
			private static final long serialVersionUID = -2342506838333821595L;
			@Override public void actionPerformed(final ActionEvent evt)
			{
				if(evt.getSource() instanceof PDJList)
				{
					final PDJList pdjList = (PDJList)evt.getSource();
					new StatusDialog("Entferne MP3s", null, new RemoveMP3s(pdjList));
				}
			}
		});
		
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Return");
		this.getActionMap().put("Return", new AbstractAction("Return") 
		{
			private static final long serialVersionUID = -2342506838333821595L;
			@Override public void actionPerformed(final ActionEvent evt)
			{
				final Track track = ((PDJList)evt.getSource()).getSelectedValue();
				if(track != null)
				{
					track.play();
				}
			}
		});
		
		this.setCellRenderer(renderer);
		this.setPrototypeCellValue(new Track("Test", false));
		
		final DragListener dgl = new DragListener();

		new DropTarget(this, new ForeignDrop());
		final DragSource dragSource = new DragSource();
		dragSource.addDragSourceListener(dgl);
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dgl);
		
		setAppearance(TrackListAppearance.getSimpleAppearance());

		scrollToPlayed(controller.getPlayer().getCurrentTrack());
	}
	
	public void setScrollToPlayedEnabled(final boolean b)
	{
		scrollToPlayed = b;
	}

	public void setListDropMode(final ListDropMode ldMode)
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
	
	public Track getLastTrack()
	{
		return listModel.getElementAt(listModel.getSize());
	}
	

	@Override
	public List<Track> getSelectedValuesList()
    {    	
        final ListSelectionModel sm = getSelectionModel();

        final int iMin = sm.getMinSelectionIndex();
        final int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0))
        {
            return new ArrayList<>(0);
        }

        final ArrayList<Track> ret = new ArrayList<>();
        for(int i = iMin; i <= iMax; i++)
        {
            if (sm.isSelectedIndex(i))
            {
                ret.add(listModel.getElementAt(i));
            }
        }
        return ret;
    }
    
    public void setAppearance(final TrackListAppearance appearance)
    {
    	this.appearance = appearance;
    	setForeground(appearance.getColor(TrackState.Normal, EntryState.Normal, Part.Foreground));
    	setBackground(appearance.getColor(TrackState.Normal, EntryState.Normal, Part.Background));
    	setSelectionForeground(appearance.getColor(TrackState.Normal, EntryState.Selected, Part.Foreground));
    	setSelectionBackground(appearance.getColor(TrackState.Normal, EntryState.Selected, Part.Background));
    }
    
    public TrackListAppearance getAppearance()
    {
    	return appearance;
    }
    
    protected void scrollToPlayed(final Track playingCurrent)
    {
    	if(!scrollToPlayed)
    		return;
    	
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override public void run()
			{
				final int index = listModel.getIndex(playingCurrent);
				if(index != -1)
				{
					final int span = PDJList.this.getLastVisibleIndex() - PDJList.this.getFirstVisibleIndex();
					final Rectangle cellBound = getCellBounds(Math.max(index - span / 2, 0), Math.min(index + span / 2, listModel.getSize() - 1));
			        if (cellBound != null)
			        	scrollRectToVisible(cellBound);
				}
			}
		});
    }
    
    protected class PlayerListenerForLists extends PlayStateAdapter
	{
		@Override
		public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
		{
			if(reason == Reason.RECEIVED_NEW_TRACK || reason == Reason.TRACK_LOADED && scrollToPlayed)
			{
				scrollToPlayed(playingCurrent);
				PDJList.this.repaint();
			}
		}
	}
    	
    protected class DragMotionListener extends MouseMotionAdapter
	{
		private int startIndex;
		private int listSize;
		private int index;
		
		@Override
		public void mouseDragged(final MouseEvent dge)
		{	
			if(SwingUtilities.isMiddleMouseButton(dge))
			{
				if(dge.getComponent() instanceof PDJList)
				{
					final PDJList pdjList = ((PDJList)dge.getComponent());
					listSize = pdjList.getModel().getSize();
					index = dge.getY() / pdjList.getFixedCellHeight();

					if(index > pdjList.getLastVisibleIndex())
					{
						index = pdjList.getLastVisibleIndex();
					}
					if(index < 0)
						index = 0;
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
					else if(index < startIndex && index != pdjList.getLastVisibleIndex())
					{
						pdjList.setSelectionInterval(startIndex, index);
						pdjList.ensureIndexIsVisible(index - 2);
					}
					else if(index == pdjList.getLastVisibleIndex())
					{
						pdjList.setSelectionInterval(startIndex, index);
						pdjList.ensureIndexIsVisible(index + 2);
					}
				}	
				count++;
			}
		}
	}
	
    protected class ClickListener extends MouseAdapter
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
				synchronized(clickedList)
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
								if(e.getY() / clickedList.getFixedCellHeight() == clickedList.getSelectedIndices()[i - 1])
								{
									if(clickedList.getSelectedValue() != null)
									{
										PopupMenuGenerator.listPopupMenu(clickedList, clickedList.getSelectedValue()).show(clickedList, e.getX(), e.getY());
										return;
									}
								}
							}
							clickedList.setSelectedIndex(e.getY() / clickedList.getFixedCellHeight());
							PopupMenuGenerator.listPopupMenu(clickedList, clickedList.getSelectedValue()).show(clickedList, e.getX(), e.getY());
						}
						else
							PopupMenuGenerator.listPopupMenu(clickedList, null).show(clickedList, e.getX(), e.getY());
					}
					catch (final IndexOutOfBoundsException ex)
					{
						return;
					}
				}
			}

			if(SwingUtilities.isLeftMouseButton(e))
			{
				if(e.getClickCount() == 2)
				{
					final int clickIndex = e.getY() / clickedList.getFixedCellHeight();
					
					if(clickedList.getSelectedIndex() != clickIndex)
						return;
					
					clickedList.getSelectedValue().play();
				}
			}
		}
		
		@Override
		public void mouseReleased(final MouseEvent e)
		{
			count = 0;
		}	
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if(e.getComponent() instanceof PDJList)
			{
				final PDJList clickedList = (PDJList) e.getComponent();
				if(clickedList.getSelectedIndices().length == 0)
					clickedList.setSelectedIndex(e.getY() / clickedList.getFixedCellHeight());
			}
		}
	}
}

