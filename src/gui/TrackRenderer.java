package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import basics.Controller;
import common.DbTrack;
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
	private int fontSize = 22;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(value == null)
			return new JLabel("null"); 
		if(!(value instanceof Track))
			return new JLabel("no Track: " + value);

		Track track = (Track)value;

		// Liest Track-Dauer automatisch ein.
		if(track instanceof DbTrack)
		{
			DbTrack dbTrack = (DbTrack)track;
			if(dbTrack.getDuration() == 0 && dbTrack.getProblem() == DbTrack.Problem.NONE)
				Controller.getInstance().pushTrackToUpdate(dbTrack); 
		}
		
		TrackRenderer.TrackListCellRendererComponent cell = new TrackListCellRendererComponent(list, track, index, isSelected, cellHasFocus);
		return cell;
	}
	
	public void setFontSize(int point)
	{
		fontSize = point;
	}

	private class TrackListCellRendererComponent extends JPanel
	{
		private static final long serialVersionUID = -1441760682667191892L;
	
		private JPanel me = this;
		
		public TrackListCellRendererComponent(final JList list, final Track track, final int index, final boolean isSelected, final boolean cellHasFocus)
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
		
		protected void init(final JList list, final Track track, @SuppressWarnings("unused") int index, final boolean isSelected, final boolean cellHasFocus)
		{
			JLabel titel = new JLabel();
			JLabel duration = new JLabel();
			
			me.setBackground(list.getBackground());
			GridBagConstraints c = new GridBagConstraints();
			
			titel.setOpaque(true);
			duration.setOpaque(true);
			
			titel.setText(track.toString());
			if(track.getDuration() >= 0)
				duration.setText(common.Functions.formatTime(track.getDuration()));
			
			titel.setFont(new Font(list.getFont().getFontName(), Font.PLAIN, fontSize));
			duration.setFont(new Font(list.getFont().getFontName(), Font.PLAIN, fontSize));
					
			if(isSelected)
			{
				titel.setForeground(list.getForeground());
				duration.setForeground(list.getForeground());
				titel.setBackground(Color.BLUE.darker().darker());
				duration.setBackground(Color.BLUE.darker().darker());
			}
			else
			{
				titel.setBackground(list.getBackground());
				duration.setBackground(list.getBackground());
				titel.setForeground(list.getForeground());
				duration.setForeground(list.getForeground());
			}
			
			Track currentTrack = Controller.getInstance().getPlayer().getCurrentTrack();
			if(currentTrack != null && currentTrack.equals(track))
			{
				titel.setForeground(new Color(64, 192, 255));
				duration.setForeground(new Color(64, 192, 255));
			}
			
			if(track.getProblem() != Track.Problem.NONE)
			{
				titel.setForeground(Color.GRAY);
				duration.setForeground(Color.GRAY);
			}
			
			if(cellHasFocus)
			{
				setBorder(BorderFactory.createLineBorder(Color.BLUE));
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
			list.setFixedCellHeight((int)(fontSize * 1.3));
		}
	}
}
