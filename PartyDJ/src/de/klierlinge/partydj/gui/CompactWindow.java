package de.klierlinge.partydj.gui;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.players.PlayStateListener;

public class CompactWindow extends javax.swing.JFrame implements PlayStateListener, ActionListener
{
	private static final long serialVersionUID = 2756647672634036102L;
	
	protected JButton btnBack;
	protected JButton btnPlay;
	protected JButton btnForward;
	protected JPanel timePanel;
	protected JLabel lblTrack;
	protected JLabel lblTimeTotal;
	protected JLabel lblTimeRemaining;
	protected JLabel lblTimeLeft;
	protected JProgressBar progressBar;
	protected Icon buttonImageBackward;
	protected Icon buttonImageBackward2;
	protected Icon buttonImagePause;
	protected Icon buttonImagePause2;
	protected Icon buttonImagePlay;
	protected Icon buttonImagePlay2;
	protected Icon buttonImageForward;
	protected Icon buttonImageForward2;
	protected ButtonSwitcher btnBackSwitcher;
	protected ButtonSwitcher btnPlaySwitcher;
	protected ButtonSwitcher btnForwardSwitcher;
	protected static final Controller controller = Controller.getInstance();
	protected static final IPlayer player = controller.getPlayer();
	protected final Timer timer = new Timer(40, this);
	protected Track currentTrack;
	protected boolean currentPlayState;
	
	public CompactWindow()
	{
		super();
		initGUI();
		addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(final WindowEvent arg0)
			{
				player.removePlayStateListener(CompactWindow.this);
			}
		});
		player.addPlayStateListener(this);
		currentTrackChanged(null, player.getCurrentTrack(), null);
		playStateChanged(player.getPlayState());
		actionPerformed(null);
		setVisible(true);
	}
	
	private void initGUI()
	{
		final GroupLayout thisLayout = new GroupLayout(getContentPane());
		getContentPane().setLayout(thisLayout);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		buttonImageBackward = new ImageIcon(new ImageIcon("Resources/SamButtons/SkipBwd.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImageBackward2 = new ImageIcon(new ImageIcon("Resources/SamButtons/SkipBwd2.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImagePause = new ImageIcon(new ImageIcon("Resources/SamButtons/Pause.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImagePause2 = new ImageIcon(new ImageIcon("Resources/SamButtons/Pause2.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImagePlay = new ImageIcon(new ImageIcon("Resources/SamButtons/Play.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImagePlay2 = new ImageIcon(new ImageIcon("Resources/SamButtons/Play2.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImageForward = new ImageIcon(new ImageIcon("Resources/SamButtons/SkipFwd.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
		buttonImageForward2 = new ImageIcon(new ImageIcon("Resources/SamButtons/SkipFwd2.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));

		
		{
			btnBack = new JButton(buttonImageBackward);
			btnBack.setBorderPainted(false);
			btnBack.setContentAreaFilled(false);
			btnBack.setFocusPainted(false);
			btnBack.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					player.playPrevious();
				}
			});
			
			btnBackSwitcher = new ButtonSwitcher(btnBack, buttonImageBackward, buttonImageBackward2);
		}
		{
			btnPlay = new JButton(buttonImagePlay);
			btnPlay.setBorderPainted(false);
			btnPlay.setContentAreaFilled(false);
			btnPlay.setFocusPainted(false);
			btnPlay.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					player.playPause();
				}
			});
			
			if(currentPlayState)
				btnBackSwitcher = new ButtonSwitcher(btnPlay, buttonImagePause, buttonImagePause2);
			else
				btnBackSwitcher = new ButtonSwitcher(btnPlay, buttonImagePlay, buttonImagePlay2);
		}
		{
			btnForward = new JButton(buttonImageForward);
			btnForward.setBorderPainted(false);
			btnForward.setContentAreaFilled(false);
			btnForward.setFocusPainted(false);
			btnForward.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(final ActionEvent e)
				{
					player.playNext();
				}
			});

			btnBackSwitcher = new ButtonSwitcher(btnForward, buttonImageForward, buttonImageForward2);
		}
		{
			lblTrack = new JLabel();
			lblTrack.setText("Track");
			lblTrack.setFont(new java.awt.Font("Segoe UI",1,36));
		}
		{
			timePanel = new JPanel();
			final BoxLayout jPanel1Layout = new BoxLayout(timePanel, javax.swing.BoxLayout.X_AXIS);
			timePanel.setLayout(jPanel1Layout);
			{
				lblTimeLeft = new JLabel();
				timePanel.add(lblTimeLeft);
				lblTimeLeft.setText("00:00");
				lblTimeLeft.setFont(new java.awt.Font("Segoe UI",1,24));
			}
			timePanel.add(Box.createHorizontalGlue());
			{
				lblTimeTotal = new JLabel();
				timePanel.add(lblTimeTotal);
				lblTimeTotal.setText("00:00");
				lblTimeTotal.setFont(new java.awt.Font("Segoe UI",1,24));
			}
			timePanel.add(Box.createHorizontalGlue());
			{
				lblTimeRemaining = new JLabel();
				timePanel.add(lblTimeRemaining);
				lblTimeRemaining.setText("00:00");
				lblTimeRemaining.setFont(new java.awt.Font("Segoe UI",1,24));
			}
		}
		{
			progressBar = new JProgressBar();
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblTrack, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
			    .addGroup(thisLayout.createSequentialGroup()
			        .addComponent(btnForward, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
			    .addGroup(thisLayout.createSequentialGroup()
			        .addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
			    .addGroup(thisLayout.createSequentialGroup()
			        .addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
			.addContainerGap(12, Short.MAX_VALUE));
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			        .addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			        .addComponent(btnForward, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addGroup(thisLayout.createParallelGroup()
			            .addComponent(progressBar, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
			            .addComponent(timePanel, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)))
			    .addComponent(lblTrack, GroupLayout.Alignment.LEADING, 0, 730, Short.MAX_VALUE))
			.addContainerGap());
		thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {btnForward, btnPlay});
		thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnForward, btnPlay});
		pack();
	}

	@Override
	public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
	{
		currentTrack = playingCurrent;
		if(currentTrack != null)
		{
			final Runnable r = new Runnable()
			{
				@Override public void run()
				{
					lblTrack.setText(currentTrack.getName());
					setTitle(currentTrack.getName());
					progressBar.setMaximum((int)(currentTrack.getDuration() * 100));
					lblTimeTotal.setText(de.klierlinge.utils.Functions.formatTime(currentTrack.getDuration()));
				}
			};
			if(SwingUtilities.isEventDispatchThread())
				r.run();
			else
				SwingUtilities.invokeLater(r);
		}
	}

	@Override
	public void playStateChanged(final boolean playState)
	{
		currentPlayState = playState;
		if(currentPlayState)
		{
			if(btnPlaySwitcher != null)
				btnPlaySwitcher.setIcons(buttonImagePause, buttonImagePause2);
			timer.start();
		}
		else
		{
			if(btnPlaySwitcher != null)
				btnPlaySwitcher.setIcons(buttonImagePlay, buttonImagePlay2);
			timer.stop();
		}
	}

	@Override
	public void volumeChanged(final int volume)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final double position = player.getPosition();
		progressBar.setValue((int)(position * 100));

		lblTimeLeft.setText(de.klierlinge.utils.Functions.formatTime(position));
		
		if(currentTrack != null)
			lblTimeRemaining.setText("-" + de.klierlinge.utils.Functions.formatTime(currentTrack.getDuration() - position));
	}
	
	/**
	 * Bekommt einen JButton und zwei Icons. Sorgt dafür dass pressedIcon angezeigt wird, 
	 * wenn sich die Maus auf dem Button befindet und die linke Maustaste gedrückt wird.
	 * 
	 * @author Eraser
	 */
	protected static class ButtonSwitcher implements MouseListener
	{
		protected final JButton button;
		protected Icon normalIcon;
		protected Icon pressedIcon;
		protected boolean showPressed;
		protected boolean pressed;
		protected boolean active;
		
		public ButtonSwitcher(final JButton button, final Icon normal, final Icon pressed)
		{
			this.button = button;
			normalIcon = normal;
			pressedIcon = pressed;
			button.addMouseListener(this);
		}
		
		public void setIcons(final Icon normal, final Icon pressed)
		{
			normalIcon = normal;
			pressedIcon = pressed;
			update();
		}
		
		public void setNormalIcon(final Icon normalIcon)
		{
			this.normalIcon = normalIcon;
			if(!showPressed)
				update();
		}
		public Icon getNormalIcon()
		{
			return normalIcon;
		}

		public void setPressedIcon(final Icon pressedIcon)
		{
			this.pressedIcon = pressedIcon;
			if(showPressed)
				update();
		}
		public Icon getPressedIcon()
		{
			return pressedIcon;
		}

		public boolean isPressed()
		{
			return showPressed;
		}
		
		public boolean isActive()
		{
			return active;
		}

		public void setActive(final boolean active)
		{
			if(this.active != active)
			{
				if(active)
					button.addMouseListener(this);
				else
					button.removeMouseListener(this);
			}
			this.active = active;
		}

		protected void update()
		{
			final Runnable r = new Runnable()
			{
				@Override public void run()
				{
					if(showPressed)
						button.setIcon(pressedIcon);
					else
						button.setIcon(normalIcon);
				}
			};
			
			if(SwingUtilities.isEventDispatchThread())
				r.run();
			else
				SwingUtilities.invokeLater(r);
		}

		@Override
		public void mouseClicked(final MouseEvent e)
		{ /* not to implement */ }

		@Override
		public void mouseEntered(final MouseEvent e)
		{
			if(pressed)
			{
				showPressed = true;
				update();
			}
		}

		@Override
		public void mouseExited(final MouseEvent e)
		{
			if(pressed)
			{
				showPressed = false;
				update();
			}
		}

		@Override
		public void mousePressed(final MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				pressed = true;
				showPressed = true;
				update();
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				pressed = false;
				showPressed = false;
				update();
			}
		}
	}
}
