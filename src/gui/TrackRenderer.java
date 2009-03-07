package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import basics.Controller;
import common.Track;

public class TrackRenderer implements ListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(value == null)
			return new JLabel("null"); 
		if(!(value instanceof Track))
			return new JLabel("no Track: " + value);
		
		Track track = (Track)value;
		
		/*// Liest Track-Dauer automatisch ein. Deaktiviert da JList so lang mit Update braucht und Player ein Speicherleck hat.
		if(track.duration == 0 && track.problem == Track.Problem.NONE)
			Controller.instance.pushTrackToUpdate(track); //*/
		
		return new TrackListCellRendererComponent(list, track, index, isSelected, cellHasFocus);
	}

	private class TrackListCellRendererComponent extends JPanel
	{
		private static final long serialVersionUID = -1441760682667191892L;
		
		private JLabel titel = new JLabel();
		private JLabel duration = new JLabel();
		
		public TrackListCellRendererComponent(JList list, Track track, int index, boolean isSelected, boolean cellHasFocus)
		{
			this.setBackground(list.getBackground());
			GridBagConstraints c = new GridBagConstraints();
			
			titel.setOpaque(true);
			duration.setOpaque(true);
			
			titel.setText(track.toString());
			duration.setText(common.Functions.formatTime(track.duration));
			
			titel.setFont(list.getFont());
			duration.setFont(list.getFont());
					
			if(isSelected)
			{
				titel.setBackground(list.getSelectionBackground());
				duration.setBackground(list.getSelectionBackground());
				titel.setForeground(list.getSelectionForeground());
				duration.setForeground(list.getSelectionForeground());
			}
			else
			{
				titel.setBackground(list.getBackground());
				duration.setBackground(list.getBackground());
				titel.setForeground(list.getForeground());
				duration.setForeground(list.getForeground());
			}
			
			Track currentTrack = Controller.instance.getCurrentTrack();
			if(currentTrack != null && currentTrack.index == track.index)
			{
				titel.setForeground(new Color(64, 192, 255));
				duration.setForeground(new Color(64, 192, 255));
			}
			if(track.problem != Track.Problem.NONE)
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
			
			if(list.getFixedCellHeight() == -1)
				list.setFixedCellHeight(this.getPreferredSize().height);
			
			/*if(Controller.instance.isLoadFinished() == true)
				System.out.println(track.name);*/
		}
	}
}
