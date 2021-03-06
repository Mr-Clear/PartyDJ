package de.klierlinge.partydj.gui;

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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import org.apache.logging.log4j.core.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.basics.ErrorListener;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.ListAdapter;
import de.klierlinge.partydj.gui.dnd.DragDropHandler;
import de.klierlinge.partydj.gui.dnd.DragListener;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.players.PlayStateAdapter;

/**
 * Zeigt den aktuelen Titel + Zeiten + Fortschrittsbalken an.
 * 
 * @author Eraser
 * @author Sam
 */
public class PDJSlider extends JPanel
{
	private static final long serialVersionUID = -4711501280677705114L;
	private static final Logger log = LoggerFactory.getLogger(PDJSlider.class);
	
	protected JLabel titel = new JLabel("Titel");
	protected JLabel start = new JLabel(" ");
	protected JLabel end = new JLabel(" ");
	protected JLabel middle = new JLabel(" ");
	protected Slider slider = new Slider();
	
	protected Track currentTrack;
	protected double duration = 0;
	protected double position = 0;
	protected int fontSize = 26;

	protected final transient Controller controller = Controller.getInstance();
	protected IPlayer player = controller.getPlayer();
	protected IData data = controller.getData();
	protected Timer refreshTimer;
	
	protected boolean errorShown;
	
	protected Thread errorShowThread;
	
	protected final JFrame ownerFrame;
	
	public PDJSlider(final JFrame ownerFrame)
	{
		this.ownerFrame = ownerFrame;
		
		if(SwingUtilities.isEventDispatchThread())
		{
			init();
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override public void run()
					{
						init();
					}
				});
			}
			catch (final InterruptedException | InvocationTargetException e)
			{
				log.error("Fehler bei Erstellen vom Slider.", e);
			}
		}
		
		controller.addErrorListener(new ErrorListener()
		{
			@Override public void errorOccurred(LogEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override public void run()
					{
						if(errorShowThread != null)
							errorShowThread.interrupt();
						
						errorShown = true;
						titel.setForeground(Color.red);
						titel.setText(event.getMessage().getFormattedMessage());
						
						errorShowThread = new Thread(){
							@Override public void run()
							{
								try
								{
									Thread.sleep(event.getLevel().intLevel() * 10);
								}
								catch (final InterruptedException ignored)
								{
									return;
								}
								SwingUtilities.invokeLater(new Runnable(){
									@Override
									public void run()
									{
										errorShown = false;
										titel.setForeground(Color.green);
										if(currentTrack != null)
											titel.setText(currentTrack.toString());
									}
								});
								errorShowThread = null;
							}
						};
						errorShowThread.setDaemon(true);
						errorShowThread.setName("Show Error Thread");
						errorShowThread.start();
					}
				});
			}
		});
	}
	
	public void setFontSize(final int points)
	{
		fontSize = points;
		this.repaint();
	}
	
	public void init()
	{
		final GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		final Box vBox = Box.createVerticalBox();
		
		this.setBackground(Color.darkGray);

		titel.setBackground(Color.darkGray);
		titel.setForeground(Color.green);
		titel.setFont(new Font(titel.getFont().getName(), Font.BOLD, fontSize));
		new DragListener(titel);
		this.setTransferHandler(new DragDropHandler());
		
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
			@Override
			public void actionPerformed(final ActionEvent evt)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override public void run()
					{
						setPosition(player.getPosition());
					}
				});
			}
		});
		
		refreshTimer.setDelay(40);
		
		player.addPlayStateListener(new PlayStateAdapter()
		{
			@Override public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
			{
				if(playingCurrent != null)
				{
					if(currentTrack != playingCurrent)
					{
						currentTrack = playingCurrent;
						if(!errorShown)
							titel.setText(playingCurrent.getName());
						setDuration(playingCurrent.getDuration());
					}
				}
				else
				{
					currentTrack = null;
					if(!errorShown)
						titel.setText("Party DJ");
					setPosition(0);
					setDuration(0);
				}
			}
			@Override public void playStateChanged(final boolean playState)
			{
				if(playState)
					refreshTimer.start();
				else
					refreshTimer.stop();
			}
		});
		
		data.addListListener(new ListAdapter()
		{
			@Override public void trackChanged(final DbTrack newTrack, final Track oldTrack, final boolean eventsFollowing)
			{
				if(newTrack.equals(currentTrack))
				{
					if(duration != newTrack.getDuration())
					{
						setDuration(newTrack.getDuration());
					}
					titel.setText(newTrack.getName());
				}			
			}
		});
		
		currentTrack = player.getCurrentTrack();
		if(currentTrack != null && !errorShown)
			titel.setText(currentTrack.getName());
		setDuration(player.getDuration());
		setPosition(player.getPosition());
	}
	
	public void setDuration(final double duration)
	{
		this.duration = duration;
		slider.setMaximum(duration);
		middle.setText(de.klierlinge.utils.Functions.formatTime(duration));
		end.setText("-" + de.klierlinge.utils.Functions.formatTime(duration - position));		
	}
	
	public void setPosition(final double position)
	{
		this.position = position;
		slider.setValue(position);
		start.setText(de.klierlinge.utils.Functions.formatTime(position));
		end.setText("-" + de.klierlinge.utils.Functions.formatTime(duration - position));
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
		protected void paintComponent(final Graphics g) 
		{ 
			super.paintComponent(g);
			
			g.setColor(new Color(33, 32, 32));
			g.fillRect(0, 0, getSize().width, getSize().height);
			g.setColor(new Color(0, 240, 0));
			final int mid = (int)((getSize().width - 4) / sliderDuration * sliderPosition);
			g.fill3DRect(2, 2, mid, getSize().height - 4, true);
			g.setColor(new Color(0, 128, 0));
			g.fill3DRect(mid - 2, 1, 4, getSize().height - 3, true);
		}
		
		public void setMaximum(final double max)
		{
			sliderDuration = max;
		}
		
		public void setValue(final double val)
		{
			sliderPosition = val;
			repaint();
		}
		
		@Override 
		public void mouseClicked(final MouseEvent e)
		{
			if(sliderDuration > sliderPosition)
			{
				player.setPosition((e.getX() - 1d) / (getSize().width - 3d) * sliderDuration);
			}			
		}
		@Override
		public void mouseEntered(final MouseEvent e) { /* not to implement */ }
		@Override
		public void mouseExited(final MouseEvent e) { /* not to implement */ }
		@Override
		public void mousePressed(final MouseEvent e) { /* not to implement */ }
		@Override
		public void mouseReleased(final MouseEvent e) { /* not to implement */ }
	}
}
