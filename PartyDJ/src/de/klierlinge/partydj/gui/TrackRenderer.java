package de.klierlinge.partydj.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.TrackListAppearance.EntryState;
import de.klierlinge.partydj.gui.TrackListAppearance.Part;
import de.klierlinge.partydj.gui.TrackListAppearance.TrackState;
import de.klierlinge.partydj.lists.data.DbTrack;

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
	public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
	{
		if(value == null)
			return new JLabel("null"); 
		if(!(value instanceof Track))
			return new JLabel("no Track: " + value);
		
		final Track track = (Track)value;
		if(!(list instanceof PDJList))
			return new JLabel(track.toString());

		final PDJList pdjList = (PDJList)list;

		// Liest Track-Dauer automatisch ein.
		if(track instanceof DbTrack)
		{
			final DbTrack dbTrack = (DbTrack)track;
			if(dbTrack.getDuration() == 0 && dbTrack.getProblem() == DbTrack.Problem.NONE)
				Controller.getInstance().pushTrackToUpdate(dbTrack); 
		}
		
		final TrackRenderer.TrackListCellRendererComponent cell = new TrackListCellRendererComponent(pdjList, track, index, isSelected, cellHasFocus);
		return cell;
	}

	private static class TrackListCellRendererComponent extends JPanel
	{
		private static final long serialVersionUID = -1441760682667191892L;
			
		public TrackListCellRendererComponent(final PDJList list, final Track track, final int index, final boolean isSelected, final boolean cellHasFocus)
		{
			if(SwingUtilities.isEventDispatchThread())
				init(list, track, index, isSelected, cellHasFocus);
			else
			{
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						@Override public void run()
						{
							init(list, track, index, isSelected, cellHasFocus);
						}
					});
				}
				catch (final InterruptedException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Update einer Liste.");
				}
				catch (final InvocationTargetException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Update einer Liste.");
				}
			}
		}
		
		protected void init(final PDJList list, final Track track, @SuppressWarnings("unused") final int index, final boolean isSelected, final boolean cellHasFocus)
		{
			final JLabel titel = new JLabel();
			final JLabel duration = new JLabel();
			
			
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
			
			final GridBagConstraints c = new GridBagConstraints();
			
			titel.setOpaque(true);
			duration.setOpaque(true);
			
			titel.setText(track.toString());
			if(track.getDuration() >= 0)
				duration.setText(de.klierlinge.partydj.common.Functions.formatTime(track.getDuration()));

			final TrackListAppearance appearance = list.getAppearance();
			if(appearance != null)
			{
				TrackListCellRendererComponent.this.setBackground(appearance.getColor(trackState, entryState, Part.Background));
				
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
				
				list.setFixedCellHeight((int)(appearance.getFont().getSize() * 1.3));
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
		}
	}
}
