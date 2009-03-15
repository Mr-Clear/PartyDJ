package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import common.Track;
import data.IData;
import data.MasterListAdapter;
import players.IPlayer;
import players.PlayStateAdapter;
import basics.Controller;

public class PDJSlider extends JPanel
{
	private static final long serialVersionUID = -4711501280677705114L;
	
	private JLabel start = new JLabel(" ");
	private JLabel end = new JLabel(" ");
	private JLabel middle = new JLabel(" ");
	private JSlider slider = new JSlider();
	
	private Track currentTrack;
	private double duration;
	private double position;

	private Controller controller = Controller.getInstance();
	private IPlayer player = controller.getPlayer();
	private IData data = controller.getData();
	private Timer refreshTimer;
	
	public PDJSlider()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		this.setBackground(Color.darkGray);
		
		slider.setBackground(Color.darkGray);
		slider.setPaintTicks(true);
		slider.setValue(0);
		
		start.setForeground(Color.green);
		middle.setForeground(Color.green);
		end.setForeground(Color.green);
		start.setFont(new Font(start.getFont().getName(), Font.BOLD, 12)); 
		middle.setFont(new Font(middle.getFont().getName(), Font.BOLD, 12)); 
		end.setFont(new Font(end.getFont().getName(), Font.BOLD, 12)); 
		
		middle.setVisible(true);
		end.setVisible(true);
		start.setVisible(true);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.ipadx = 800;
		c.gridx = 0;
		c.gridy = 0;
		this.add(slider, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		this.add(start, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.insets = new Insets(0, 367, 0, 0);
		this.add(middle, c);
		
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(end, c);
		
		refreshTimer = new Timer(0, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setPosition(player.getPosition());
			}
		});
		
		refreshTimer.setDelay(40);
		
		player.addPlayStateListener(new PlayStateAdapter(){
					public void currentTrackChanged(Track playedLast, Track playingCurrent)
					{
						if(currentTrack != playingCurrent)
						{
							currentTrack = playingCurrent;
							setDuration(playingCurrent.duration);
						}
					}
					public void playStateChanged(boolean playState)
					{
						if(playState)
							refreshTimer.start();
						else
							refreshTimer.stop();
					}});
		
		data.addMasterListListener(new MasterListAdapter(){
			public void trackChanged(Track track)
			{
				if(track == currentTrack)
				{
					if(duration != track.duration)
					{
						setDuration(track.duration);
					}
				}			
			}});
		
		this.setVisible(true);
	}
	
	public void setDuration(double duration)
	{
		this.duration = duration;
		slider.setMaximum((int)(duration * 10000));
		slider.setMajorTickSpacing((int)(duration * 2500));
		slider.setMinorTickSpacing((int)(duration * 1250));
		middle.setText(common.Functions.formatTime(duration));
		end.setText("-" + common.Functions.formatTime(duration - position));
	}
	
	public void setPosition(double position)
	{
		this.position = position;
		if(position > duration)
			setDuration(position);
		slider.setValue((int)(position * 10000));
		start.setText(common.Functions.formatTime(player.getPosition()));
		end.setText("-" + common.Functions.formatTime(duration - position));
	}
}
