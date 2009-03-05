package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
		return new ListCellRendererComponent(list, (Track)value, index, isSelected, cellHasFocus);
	}

	private class ListCellRendererComponent extends JPanel
	{
		private static final long serialVersionUID = -1441760682667191892L;
		
		private JLabel titel = new JLabel();
		private JLabel duration = new JLabel();
		
		public ListCellRendererComponent(JList list, Track track, int index, boolean isSelected, boolean cellHasFocus)
		{
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
				titel.setForeground(Color.RED);
				duration.setForeground(Color.RED);
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
			
			titel.setPreferredSize(new Dimension(list.getSize().width - duration.getPreferredSize().width, titel.getMaximumSize().height));

			setLayout(new BorderLayout());
			add(titel, BorderLayout.CENTER);
			if(list.getSize().width > duration.getPreferredSize().width * 4)
				add(duration, BorderLayout.EAST);
			
			if(list.getFixedCellHeight() == -1)
				list.setFixedCellHeight(this.getPreferredSize().height);
			
			/*if(Controller.instance.isLoadFinished() == true)
				System.out.println(track.name);*/
		}
	}
}
