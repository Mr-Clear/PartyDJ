package gui;

import gui.dnd.DragListener;
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
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import common.Track;
import data.IData;
import data.ListAdapter;
import players.IPlayer;
import players.PlayStateAdapter;
import basics.Controller;

/**
 * Zeigt den aktuelen Titel + Zeiten + Fortschrittsbalken an.
 * 
 * @author Eraser
 * @author Sam
 */
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
	protected int fontSize = 26;

	private Controller controller = Controller.getInstance();
	private IPlayer player = controller.getPlayer();
	private IData data = controller.getData();
	private Timer refreshTimer;
	
	public PDJSlider()
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			init();
		}
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){
					@Override
					public void run()
					{
						init();
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
	
	public void setFontSize(int points)
	{
		fontSize = points;
		this.repaint();
	}
	
	public void init()
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		Box vBox = Box.createVerticalBox();
		
		this.setBackground(Color.darkGray);

		titel.setBackground(Color.darkGray);
		titel.setForeground(Color.green);
		titel.setFont(new Font(titel.getFont().getName(), Font.BOLD, fontSize));
		new DragListener(titel);
		
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
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run()
					{
						setPosition(player.getPosition());
					}});
				
			}
		});
		
		refreshTimer.setDelay(40);
		
		player.addPlayStateListener(new PlayStateAdapter(){
					@Override public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
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
					@Override public void playStateChanged(boolean playState)
					{
						if(playState)
							refreshTimer.start();
						else
							refreshTimer.stop();
					}});
		
		data.addListListener(new ListAdapter(){
			@Override public void trackChanged(Track track)
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
	
	public void setDuration(final double duration)
	{
		this.duration = duration;
		slider.setMaximum(duration);
		middle.setText(common.Functions.formatTime(duration));
		end.setText("-" + common.Functions.formatTime(duration - position));		
	}
	
	public void setPosition(final double position)
	{
		this.position = position;
		slider.setValue(position);
		start.setText(common.Functions.formatTime(position));
		end.setText("-" + common.Functions.formatTime(duration - position));
	}
	
	/**Der eigendliche Fortschrittsbalken
	 */
	class Slider extends JComponent implements MouseListener
	{
		private static final long serialVersionUID = -1283733626056623005L;
		double sliderDuration;
		double sliderPosition;
		
		public Slider()
		{
			Slider.this.setMinimumSize(new Dimension(100, 20));
			Slider.this.setPreferredSize(new Dimension(500, 20));
			Slider.this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			Slider.this.addMouseListener(Slider.this);
		}
		
		@Override 
		protected void paintComponent(Graphics g) 
		{ 
			super.paintComponent(g);
			
			g.setColor(new Color(33, 32, 32));
			g.fillRect(0, 0, getSize().width, getSize().height);
			g.setColor(new Color(0, 240 /* TODO 230 */, 0));
			int mid = (int)((getSize().width - 4) / sliderDuration * sliderPosition);
			g.fill3DRect(2, 2, mid, getSize().height - 4, true);
			g.setColor(new Color(0, 128, 0));
			g.fill3DRect(mid - 2, 1, 4, getSize().height - 3, true);
		}
		
		public void setMaximum(double max)
		{
			sliderDuration = max;
		}
		
		public void setValue(final double val)
		{
			sliderPosition = val;
			repaint();
		}
		
		@Override 
		public void mouseClicked(MouseEvent e)
		{
			if(sliderDuration > sliderPosition)
			{
				player.setPosition((e.getX() - 1d) / (getSize().width - 3d) * sliderDuration);
			}			
		}

		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
	}
}
