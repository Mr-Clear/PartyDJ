package gui;

import gui.TrackListAppearance.EntryState;
import gui.TrackListAppearance.Part;
import gui.TrackListAppearance.TrackState;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lists.data.DbTrack;
import basics.Controller;
import common.Track;

/**
 * Zeichnet einen Track in eine JList.
 * <p>Es wird der Name des Tracks linksbündig, und die Dauer rechtsbündig dargestellt.
 * 
 * @author Eraser, Sam
 * 
 * @see PDJList
 */
public class TrackRenderer extends DefaultListCellRenderer 
{
	private static final long serialVersionUID = 1791058448796268655L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(value == null)
			return new JLabel("null"); 
		if(!(value instanceof Track))
			return new JLabel("no Track: " + value);
		
		Track track = (Track)value;
		if(!(list instanceof PDJList))
			return new JLabel(track.toString());

		PDJList pdjList = (PDJList)list;

		// Liest Track-Dauer automatisch ein.
		if(track instanceof DbTrack)
		{
			DbTrack dbTrack = (DbTrack)track;
			if(dbTrack.getDuration() == 0 && dbTrack.getProblem() == DbTrack.Problem.NONE)
				Controller.getInstance().pushTrackToUpdate(dbTrack); 
		}
		
		TrackRenderer.TrackListCellRendererComponent cell = new TrackListCellRendererComponent(pdjList, track, index, isSelected, cellHasFocus);
		return cell;
	}

	private class TrackListCellRendererComponent extends JPanel
	{
		private static final long serialVersionUID = -1441760682667191892L;
	
		private JPanel me = this;
		
		public TrackListCellRendererComponent(final PDJList list, final Track track, final int index, final boolean isSelected, final boolean cellHasFocus)
		{
			if(SwingUtilities.isEventDispatchThread())
				init(list, track, index, isSelected, cellHasFocus);
			else
			{
				try
				{
					SwingUtilities.invokeAndWait(new Runnable(){
						@Override
						public void run()
						{
							init(list, track, index, isSelected, cellHasFocus);
						}});
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		protected void init(final PDJList list, final Track track, @SuppressWarnings("unused") int index, final boolean isSelected, final boolean cellHasFocus)
		{
			JLabel titel = new JLabel();
			JLabel duration = new JLabel();
			
			TrackListAppearance appearance = list.getAppearance();
			
			TrackState trackState;
			if(track.equals(Controller.getInstance().getPlayer().getCurrentTrack()))
				trackState = TrackState.Playing;
			else if(track.getProblem() != Track.Problem.NONE)
				trackState = TrackState.Problem;
			else
				trackState = TrackState.Normal;
			
			EntryState entryState;
			if(isSelected)
				entryState = EntryState.Selected;
			else
				entryState = EntryState.Normal;
			
			me.setBackground(appearance.getColor(trackState, entryState, Part.Background));
			GridBagConstraints c = new GridBagConstraints();
			
			titel.setOpaque(true);
			duration.setOpaque(true);
			
			titel.setText(track.toString());
			if(track.getDuration() >= 0)
				duration.setText(common.Functions.formatTime(track.getDuration()));
			
			titel.setFont(appearance.getFont());
			duration.setFont(appearance.getFont());
					
			titel.setForeground(appearance.getColor(trackState, entryState, Part.Foreground));
			duration.setForeground(appearance.getColor(trackState, entryState, Part.Foreground));
			titel.setBackground(appearance.getColor(trackState, entryState, Part.Background));
			duration.setBackground(appearance.getColor(trackState, entryState, Part.Background));

			if(cellHasFocus)
			{
				setBorder(appearance.getFocusBorder());
			}
			
			setLayout(new GridBagLayout());
			
			c.ipadx = -100000;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			
			add(titel, c);
			
			if(list.getSize().width > duration.getPreferredSize().width * 8)
			{
				c.ipadx = 0;
				c.weightx = 0.0;
				add(duration, c);
			}	

			//if(list.getFixedCellHeight() == -1)
			//TODO Größe abhängig von fontSize
			list.setFixedCellHeight((int)(appearance.getFont().getSize() * 1.3));
		}
	}
}
