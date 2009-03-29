package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import common.Track;
import data.IData;
import data.ListAdapter;
import players.IPlayer;
import players.PlayStateAdapter;
import basics.Controller;

public class PDJSlider extends JPanel
{
	private static final long serialVersionUID = -4711501280677705114L;
	
	private JLabel titel = new JLabel("Titel");
	private JLabel start = new JLabel(" ");
	private JLabel end = new JLabel(" ");
	private JLabel middle = new JLabel(" ");
	private Slider slider = new Slider();
	
	private Track currentTrack;
	private double duration = 0;
	private double position = 0;

	private Controller controller = Controller.getInstance();
	private IPlayer player = controller.getPlayer();
	private IData data = controller.getData();
	private Timer refreshTimer;
	
	public PDJSlider()
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		Box vBox = Box.createVerticalBox();
		
		this.setBackground(Color.darkGray);

		titel.setBackground(Color.darkGray);
		titel.setForeground(Color.green);
		titel.setFont(new Font(titel.getFont().getName(), Font.BOLD, 18));
		
		slider.setBackground(Color.darkGray);
		
		start.setForeground(Color.green);
		middle.setForeground(Color.green);
		end.setForeground(Color.green);
		start.setFont(new Font(start.getFont().getName(), Font.BOLD, 12)); 
		middle.setFont(new Font(middle.getFont().getName(), Font.BOLD, 12)); 
		end.setFont(new Font(end.getFont().getName(), Font.BOLD, 12)); 
		
		middle.setVisible(true);
		end.setVisible(true);
		start.setVisible(true);
		
		Box hBox = Box.createHorizontalBox();
		hBox.add(titel);
		hBox.add(Box.createHorizontalGlue());
		vBox.add(hBox);
		vBox.add(Box.createVerticalStrut(8));
		vBox.add(slider);
		vBox.add(Box.createVerticalStrut(4));
		hBox = Box.createHorizontalBox();
		hBox.add(start);
		hBox.add(Box.createHorizontalGlue());
		hBox.add(middle);
		hBox.add(Box.createHorizontalGlue());
		hBox.add(end);
		vBox.add(hBox);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		this.add(vBox, c);
		

		refreshTimer = new Timer(0, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setPosition(player.getPosition());
			}
		});
		
		refreshTimer.setDelay(40);
		
		player.addPlayStateListener(new PlayStateAdapter(){
					public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
					{
						if(playingCurrent != null)
						{
							if(currentTrack != playingCurrent)
							{
								currentTrack = playingCurrent;
								titel.setText(playingCurrent.name);
								setDuration(playingCurrent.duration);
							}
						}
						else
						{
							currentTrack = null;
							titel.setText("Party DJ");
							setPosition(0);
							setDuration(0);
						}
					}
					public void playStateChanged(boolean playState)
					{
						if(playState)
							refreshTimer.start();
						else
							refreshTimer.stop();
					}});
		
		data.addListListener(new ListAdapter(){
			public void trackChanged(Track track)
			{
				if(track == currentTrack)
				{
					if(duration != track.duration)
					{
						setDuration(track.duration);
					}
					titel.setText(track.name);
				}			
			}});
		
		currentTrack = player.getCurrentTrack();
		if(currentTrack != null)
			titel.setText(currentTrack.name);
		setDuration(player.getDuration());
		setPosition(player.getPosition());
	}
	
	public void setDuration(double duration)
	{
		this.duration = duration;
		slider.setMaximum(duration);
		middle.setText(common.Functions.formatTime(duration));
		end.setText("-" + common.Functions.formatTime(duration - position));
	}
	
	public void setPosition(double position)
	{
		this.position = position;
		slider.setValue(position);
		start.setText(common.Functions.formatTime(position));
		end.setText("-" + common.Functions.formatTime(duration - position));
	}
	
	class Slider extends JComponent implements MouseListener
	{
		private static final long serialVersionUID = -1283733626056623005L;
		double duration;
		double position;
		
		public Slider()
		{
			super();
			this.setMinimumSize(new Dimension(100, 20));
			this.setPreferredSize(new Dimension(500, 20));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			this.addMouseListener(this);
		}
		
		@Override 
		protected void paintComponent( Graphics g ) 
		{ 
			super.paintComponent(g);
			g.fillRect(1, 1, (int) ((getSize().width - 3) / duration * position), getSize().height - 3);
		}
		
		public void setMaximum(double max)
		{
			duration = max;
		}
		
		public void setValue(double val)
		{
			position = val;
			repaint();
		}

		public void mouseClicked(MouseEvent e)
		{
			if(duration > position)
			{
				player.setPosition((e.getX() - 1d) / (getSize().width - 3d) * duration);
			}			
		}

		public void mouseEntered(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
