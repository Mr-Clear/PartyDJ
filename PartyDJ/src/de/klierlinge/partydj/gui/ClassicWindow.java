package de.klierlinge.partydj.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.SettingException;
import de.klierlinge.partydj.gui.dnd.ForeignDrop;
import de.klierlinge.partydj.gui.dnd.ListDropMode;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.TrackListModel;
import de.klierlinge.partydj.lists.data.DbClientListModel;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.lists.data.ListProvider;
import de.klierlinge.partydj.lists.data.SearchListModel;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.players.PlayStateAdapter;
import de.klierlinge.partydj.players.PlayStateListener;

/**
 * Grafische Benutzeroberfläche für Party DJ.
 * 
 * @author Sam
 */


public class ClassicWindow extends JFrame
{	
	private final static Logger log = LoggerFactory.getLogger(ClassicWindow.class);
	protected static final long serialVersionUID = 5672123337960808686L;
	protected final transient Controller controller = Controller.getInstance();
	protected final IPlayer player = controller.getPlayer();
	protected final ListProvider listProvider = controller.getListProvider();
	protected final IData data = controller.getData();
	protected JSlider volume;
	protected JComponent main;
	protected JButton buttonPause;
	protected static ClassicWindow instance;
	
	public ClassicWindow()
	{
		super("PartyDJ");
		instance = this;
		
		if(SwingUtilities.isEventDispatchThread())
		{
			initGUI();
		}
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override public void run()
					{
						initGUI();
					}
				});
			}
			catch (final InterruptedException | InvocationTargetException e)
			{
				log.error("Fehler bei Laden von ClassicWindow.", e);
			}
	}

	protected void initGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/p32.gif"));
		final TrayListener tl = new TrayListener();
		addWindowListener(tl);
		player.addPlayStateListener(tl);
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		player.addPlayStateListener(new PlayState());

		final GridBagConstraints con = new GridBagConstraints();
		final GridBagLayout layout = new GridBagLayout();
		
		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		manageSize();
		
		getContentPane().setBackground(Color.darkGray);
		
		con.gridx = 0;
		con.gridy = 0;
		con.weightx = 0.0;
		con.weighty = 0.0;
		con.ipadx = GridBagConstraints.REMAINDER;
		con.ipady = 22;
		add(control(), con);

		con.gridx = 0;
		con.gridy = 1;
		con.weightx = 1.0;
		con.weighty = 1.0;
		main = mainPart();
		add(main, con);
		
		if(!Boolean.parseBoolean(Controller.getInstance().getData().readSetting("MASTERLIST", "true")))
			removeListFromGui("MASTERLIST", true);
		if(!Boolean.parseBoolean(Controller.getInstance().getData().readSetting("PLAYLIST", "true")))
			removeListFromGui("PLAYLIST", true);
		
		setVisible(true);
	}
	
	public static ClassicWindow getInstance()
	{
		return instance;
	}
	
	/**
	 * Lautstärkeregler, Fortschrittsbalken und Buttons werden zu einer Kontrolleinheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Steuerungen enthält.
	 */
	protected JPanel control()
	{
		final JPanel control = new JPanel(new GridBagLayout());
		
		final GridBagConstraints c = new GridBagConstraints();
				
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		control.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		control.add(buttons(), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipady = -200;
		c.ipadx = 50;
		c.gridx = 1;
		control.add(volume(), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.ipadx = 500;
		c.gridx = 2;
		control.add(slider(), c);
				
		return control;
	}
	
	/**
	 * Alle Listen und die Suche werden zu einer Einheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Listen und die Suche enthält.
	 */
	protected JPanel mainPart()
	{
		final GridBagConstraints c = new GridBagConstraints();
		final JPanel mainPart = new JPanel(new GridBagLayout());	
			
		c.insets = new Insets(0, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		

		c.gridx = 0;
		c.gridy = 0;
		
		mainPart.setBackground(Color.darkGray);
		mainPart.add(list("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);
		
		c.gridy = 1;
		try
		{
			mainPart.add(list("Playlist", listProvider.getDbList("Playlist"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (final ListException e)
		{
			log.error("Fehler bei Zeichnen von ClassicWindow.", e);
		}
		
		c.gridx = 1;
		c.gridy = 0;
		try
		{
			mainPart.add(list("Wunschliste", listProvider.getDbList("Wunschliste"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (final ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Wunschliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		c.gridy = 1;
		mainPart.add(search(), c);
		
		return mainPart;
	}
	
	/**
	 * Erzeugt die Buttons und ordnet sie mit einem GridBagLayout an.
	 * @return JPanel mit GridBagLayout, welches alle Buttons enthält.
	 */
	protected JPanel buttons()
	{
		final GridBagConstraints c = new GridBagConstraints();
		
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.darkGray);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.0;
		c.weighty = 0.0;

		final JButton play = customizeButton("Resources/Play.png");
		final JButton pause = customizeButton("Resources/Pause.png");
		final JButton stop = customizeButton("Resources/Stop.png");
		final JButton fwd = customizeButton("Resources/Vorspulen.png");
		final JButton bwd = customizeButton("Resources/Zurueckspulen.png");
		final JButton skipFWD = customizeButton("Resources/Vor.png");
		final JButton skipBWD = customizeButton("Resources/Zurueck.png");
		final JButton setting = customizeButton("Resources/Einstellungen.png");
			
		play.addMouseListener(new MouseAdapter()
		{
			@Override public void mouseClicked(final MouseEvent me) 
			{
				player.start();
			}
		});
		
		pause.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					player.fadeInOut();
				}
			});

		stop.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					player.stop();
				}
			});
		
		fwd.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					player.setPosition(player.getPosition() + 5);
				}
			});
		
		bwd.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(final MouseEvent me) 
				{
					player.setPosition(player.getPosition() - 5);
				}
			});
		
		skipFWD.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					player.playNext();
				}
			});
		
		skipBWD.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					player.playPrevious();
				}
			});
		
		setting.addMouseListener(new MouseAdapter()
			{
				@Override public void mouseClicked(final MouseEvent me) 
				{
					controller.registerWindow(new de.klierlinge.partydj.gui.settings.SettingWindow());
				}
			});
		setting.setToolTipText("Einstellungen");
				
		c.insets = new Insets(1, 1, 1, 1);
		
		panel.add(play, c);
		panel.add(pause, c);
		panel.add(stop, c);
		panel.add(setting, c);
		
		c.gridy = 1;
		
		panel.add(skipBWD, c);
		panel.add(bwd, c);
		panel.add(fwd, c);
		panel.add(skipFWD, c);
		
		buttonPause = pause;
				
		return panel;
		
	}

	/**
	 * Erzeugt eine Liste mit Titel.
	 * @param title der Liste, Liste, DropMode
	 * @return JPanel mit GridBagLayout, welches die Liste und Titel enthält.
	 */
	protected JPanel list(final String title, final TrackListModel l, final ListDropMode ldMode)
	{
		final GridBagConstraints c = new GridBagConstraints();
		
		final PDJList list = new PDJList(l, ldMode, title);
		final JScrollPane scrollPane = new JScrollPane(list);
		final JLabel label = new JLabel(title);
		
		final JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(scrollPane);
		
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		scrollPane.setBackground(Color.black);
		
		list.setAppearance(TrackListAppearance.getGreenAppearance(22));
		
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(label, c);
		
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		panel.add(scrollPane, c);
		
		return panel;
	}
	
	/**
	 * Erzeugt eine Suchfunktion mit Ergebnisausgabe.
	 * @return	JPanel mit GridBagLayout, welches das Suchfeld und die Ergebnisliste beinhaltet.
	 */
	protected static JPanel search()
	{
		final JTextField textField = new JTextField();												// final damit die innere Klasse
		final PDJList searchList = new PDJList(new SearchListModel(), ListDropMode.NONE, "Search");	// darauf zugreifen kann.
		final JScrollPane scrollPane = new JScrollPane(searchList);
		final JPanel panel = new JPanel(new GridBagLayout());
		final JLabel label = new JLabel("Suche");
		
		textField.addMouseListener(new MouseAdapter()
		{
			protected boolean markEverything;
			@Override public void mouseClicked(final MouseEvent e)
			{
				if(!markEverything)
				{
					textField.setSelectionStart(0);
					textField.setSelectionEnd(textField.getText().length());
				}
			}
			@Override public void mousePressed(final MouseEvent e)
			{
				markEverything = textField.hasFocus();
			}
		});
		
		//DnD
		textField.setDropMode(DropMode.INSERT);
		new DropTarget(textField, new ForeignDrop());
		
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		textField.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setAutoscrolls(false);
		
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		textField.setBackground(Color.black);
		textField.setForeground(Color.green);
		textField.setCaretColor(Color.WHITE);
		
		searchList.setAppearance(TrackListAppearance.getGreenAppearance(22));
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(label, c);
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy = 2;
		panel.add(scrollPane, c);
		
		c.weighty = 0.0;
		c.ipady = 8;
		c.gridy = 1;
		panel.add(textField, c);
		
		textField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(final ActionEvent arg0)
			{
				String text = textField.getText();
				if(text == null)
					text = "";

				final Color bgColor = textField.getBackground();
				try
				{
					textField.setBackground(Color.GRAY);
					//TODO textField neu zeichnen
					((SearchListModel)searchList.getListModel()).search(text);
				}
				catch (final ListException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Suche fehlgeschlagen.\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					textField.setBackground(bgColor);
				}
			}
		});
		
		return panel;
	}
	
	/**Erzeugt den Fortschrittsbalken.
	 * @return JPanel mit GridBagLayout, welches den Titel und Slider beinhaltet.
	 */
	protected JPanel slider()
	{
		PDJSlider slider = null;
		final JPanel panel = new JPanel(new GridBagLayout());
		try
		{
			slider = new PDJSlider(this);
		}
		catch(final Throwable e)
		{
			e.printStackTrace();
			return panel;
		}
		panel.setBackground(Color.darkGray);
		slider.setBackground(Color.darkGray);
		slider.setForeground(Color.green);
		final GridBagConstraints c = new GridBagConstraints();	
			
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		panel.add(slider, c);
		
		return panel;
	}
	
	/**
	 * Erzeugt den Lautstärkeregler.
	 * @return	JPanel mit GridBagLayout, welches den Lautstärkeregler beinhaltet.
	 */
	protected JPanel volume()
	{
		volume = new JSlider(SwingConstants.VERTICAL, 0, 100, player.getVolume());
		final JPanel panel = new JPanel(new GridBagLayout());
		
		final GridBagConstraints c = new GridBagConstraints();
		
		volume.addChangeListener(new VolumeListener());
		
		panel.setBackground(Color.darkGray);
		volume.setBackground(Color.darkGray);
		
		volume.setMinorTickSpacing(10);
		volume.setMajorTickSpacing(20);
		volume.setPaintTicks(true);
		
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.0;
		panel.add(volume, c);
		
		return panel;
	}
	
	protected static JButton customizeButton(final String iconPath)
	{
		final JButton button = new JButton(new ImageIcon(iconPath));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusPainted(true);
		button.setBackground(Color.darkGray);
		final Border buttonBorder = new javax.swing.border.EtchedBorder(0, Color.gray, Color.black); //new javax.swing.plaf.basic.BasicBorders.ButtonBorder(Color.blue, Color.red, Color.red, Color.blue);
		button.setBorder(buttonBorder);
		return button;
	}
	
	protected void manageSize()
	{
		try
		{
			setSize(Integer.parseInt(data.readSetting("ClassicWindowWidth", "800")), Integer.parseInt(data.readSetting("ClassicWindowHeight", "600")));
		}
		catch (final NumberFormatException e1)
		{
			setSize(800, 600);
		}
		catch (final SettingException e1)
		{
			setSize(800, 600);
		}
		
		try
		{
			setExtendedState(Integer.parseInt(data.readSetting("ClassicWindowState", Integer.toString(MAXIMIZED_BOTH))));
		}
		catch (final NumberFormatException e)
		{
			setExtendedState(MAXIMIZED_BOTH);
		}
		catch (final SettingException e)
		{
			setExtendedState(MAXIMIZED_BOTH);
		}
		
		this.setLocationRelativeTo(null); //Macht dass das Fenster in Bildschirmmitte steht
		
		addWindowStateListener(new WindowStateListener()
		{
			@Override
			public void windowStateChanged(final WindowEvent evt)
	        {
	            resize();
	        }
		});
		
		getContentPane().addComponentListener(new ComponentAdapter()
		{  
	        @Override public void componentResized(final ComponentEvent evt) 
	        {
	            resize();
	        }
	    });
	
		resize();
	}
	
	/** Wird aufgerufen wenn sich die Größe des Fensters ändert.*/
	protected void resize()
	{
        try
		{
        	if((getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) // Wenn nicht maximiert
        	{
				data.writeSetting("ClassicWindowWidth", Integer.toString(getSize().width));
				data.writeSetting("ClassicWindowHeight", Integer.toString(getSize().height));
        	}
			data.writeSetting("ClassicWindowState", Integer.toString(getExtendedState() & MAXIMIZED_BOTH)); // & MAXIMIZED_BOTH damit MINIMIZED nicht gespeichert wird.
		}
		catch (final SettingException e)
		{
			e.printStackTrace();
		}
	}

	public void removeListFromGui(final String list, final boolean init)
	{	
		main.removeAll();
		final GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(0, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 2;

		if(!list.equalsIgnoreCase("MASTERLIST"))
			main.add(list("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);
		else if(!list.equalsIgnoreCase("PLAYLIST"))
		{
			try
			{
				main.add(list("Playlist", listProvider.getDbList("Playlist"), ListDropMode.COPY_OR_MOVE), c);
			}
			catch (final ListException e)
			{
				log.error("Fehler beim Entfernen einer Liste vom ClassicWindow.", e);
			}
		}
		
		c.gridx = 1;
		c.gridy = 0;
		c.ipady = init ? (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) : 0;
		c.gridheight = 1;
		try
		{
			main.add(list("Wunschliste", listProvider.getDbList("Wunschliste"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (final ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Wunschliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		c.gridy = 1;
		c.ipady = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		main.add(search(), c);
		
		SwingUtilities.updateComponentTreeUI(main);
	}
	
	public void restoreDefaultGUI()
	{
		main.removeAll();
		
		final GridBagConstraints c = new GridBagConstraints();	
			
		c.insets = new Insets(0, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		main.setBackground(Color.darkGray);

		main.add(list("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);

		
		c.gridy = 1;
		try
		{
			main.add(list("Playlist", listProvider.getDbList("Playlist"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (final ListException e)
		{
			log.error("Fehler bei Zeichnen von ClassicWindow.", e);
		}
		
		c.gridx = 1;
		c.gridy = 0;
		try
		{
			main.add(list("Wunschliste", listProvider.getDbList("Wunschliste"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (final ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Wunschliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		c.gridy = 1;
		main.add(search(), c);
		
		SwingUtilities.updateComponentTreeUI(main);
	}
	
	protected class PlayState extends PlayStateAdapter
	{
		protected int duration;
		
		@Override
		public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					if(playingCurrent != null)
						ClassicWindow.this.setTitle(playingCurrent.getName() + "   -   PartyDJ");
					else
						ClassicWindow.this.setTitle("PartyDJ");
				}
			});
			
		}

		@Override
		public void playStateChanged(final boolean playState)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					if(playState)
						buttonPause.setIcon(new ImageIcon("Resources/Pause.png"));
					else
						buttonPause.setIcon(new ImageIcon("Resources/Resume.png"));
				}
			});
			
		}

		@Override
		public void volumeChanged(final int vol)
		{
			volume.setValue(vol);
		}
	}
	
	protected class VolumeListener implements ChangeListener, PlayStateListener
	{
		@Override
		public void stateChanged(final ChangeEvent e)
		{
			if(e.getSource() instanceof JSlider)
				player.setVolume(((JSlider)e.getSource()).getValue());
		}

		@Override
		public void volumeChanged(final int newVolume)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					volume.setValue(newVolume);
				}
			});
			
		}

		@Override public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason) { /* ignore */ }
		@Override public void playStateChanged(final boolean playState) { /* ignore */ }
	}
	
	protected class TrayListener extends WindowAdapter implements MouseListener, PlayStateListener
	{
		protected final SystemTray tray;
		protected TrayIcon trayIcon;
		protected String info;
		
		public TrayListener()
		{
			if (SystemTray.isSupported()) 
			{
	            tray = SystemTray.getSystemTray();
	            final Image icon = Toolkit.getDefaultToolkit().getImage("Resources/p32.gif");
	            trayIcon = new TrayIcon(icon, info == null ? "PartyDJ" : info, null);
	            trayIcon.addMouseListener(this);
	            trayIcon.setImageAutoSize(true);
				init();
			}
			else
			{
				tray = null;
				trayIcon = null;
			}
		}
		
		public void init()
		{
			if(!Boolean.parseBoolean(data.readSetting("SYSTEM_TRAY", "true")))
				return;
			if(player.getCurrentTrack() != null)
				info = player.getCurrentTrack().getName();
            trayIcon.setToolTip(info == null ? "PartyDJ" : info);
			initPopUp();
		}
		
		public void initPopUp()
		{
	       	final PopupMenu popup = new PopupMenu();
	        
	       	final MenuItem name = new MenuItem(info == null ? "PartyDJ" : info);
	        name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
	        name.setEnabled(false);
	        popup.add(name);
	        popup.addSeparator();

	        try
	        {
	            if(player.getCurrentTrack() != null && player.getCurrentTrack() instanceof DbTrack)
	            {
	            	final DbTrack dbTrack = (DbTrack)player.getCurrentTrack();
	            	
					final DbClientListModel playlist = listProvider.getDbList("Playlist");
					final MenuItem wish = new MenuItem();
	
					if(playlist.getIndex(dbTrack) < 0)
						wish.setLabel("Lied auf Playlist setzen");
					else
						wish.setLabel("Lied von Playlist entfernen");
	            	
	            	wish.addActionListener(new ActionListener()
	            	{
						@Override
						public void actionPerformed(final ActionEvent e)
						{
							try
							{
								if(playlist.getIndex(dbTrack) < 0)
									playlist.add(dbTrack, false);
								else
									playlist.remove(playlist.getIndex(dbTrack), false);
								init();
							}
							catch (final ListException e1){e1.printStackTrace();}
						}
					});
		            popup.add(wish);
	            }
	        }
	        catch(final ListException e)
	        {
	        	log.error("Failed to add determine if current track is in playlist.", e);
	        }
		            
            final MenuItem playPause = new MenuItem();
            if(player.getPlayState())
            	playPause.setLabel("Pause");
            else
            	playPause.setLabel("Play");
            
            playPause.addActionListener(new ActionListener()
            {
				@Override public void actionPerformed(final ActionEvent e)
				{
					if(player.getPlayState())
						player.pause();
					else if(player.getCurrentTrack() != null)
						player.play();
					else
						player.playNext();
				}
			});
            popup.add(playPause);
            popup.addSeparator();
	        
	        final MenuItem maximize = new MenuItem("PartyDJ maximieren");
	        maximize.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
	        maximize.addActionListener(new ActionListener()
	    	{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					ClassicWindow.this.setVisible(true);
					ClassicWindow.this.setExtendedState(MAXIMIZED_BOTH);
					tray.remove(trayIcon);
				}
			});
	        popup.add(maximize);
	        
	        final MenuItem exit = new MenuItem("Schließen");
	        exit.addActionListener(new ActionListener()
	    	{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					controller.closePartyDJ();
				}
			});
	        popup.add(exit);

            trayIcon.setPopupMenu(popup);
		}
		
		@Override
		public void windowIconified(final WindowEvent e)
		{
			if(!Boolean.parseBoolean(data.readSetting("SYSTEM_TRAY", "true")))
				return;
			
			ClassicWindow.this.setVisible(false);
			try
			{
				tray.add(trayIcon);
			}
			catch (final AWTException e1)
			{
				log.error("Erstellen con Tray-Icon fehlgeschlagen.", e1);
			}
		}
		@Override
		public void mouseClicked(final MouseEvent e)
		{
			if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
			{
				ClassicWindow.this.setVisible(true);
				ClassicWindow.this.setExtendedState(MAXIMIZED_BOTH);
				tray.remove(trayIcon);
			}
		}
		@Override
		public void mouseEntered(final MouseEvent e) { /* ignore */ }
		@Override
		public void mouseExited(final MouseEvent e) { /* ignore */ }
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown())
			{
				ClassicWindow.this.setVisible(true);
				ClassicWindow.this.setExtendedState(MAXIMIZED_BOTH);
			}
		}
		@Override
		public void mouseReleased(final MouseEvent e)
		{
			ClassicWindow.this.setVisible(false);
		}
		@Override
		public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
		{
			if(!Boolean.parseBoolean(data.readSetting("SYSTEM_TRAY", "true")))
				return;
			if(Boolean.parseBoolean(data.readSetting("TOOLTIP", "true")) && playingCurrent != null)
				trayIcon.displayMessage(null, playingCurrent.getName(), MessageType.NONE);
			init();
		}
		@Override
		public void playStateChanged(final boolean playState)
		{
			init();
			if(!Boolean.parseBoolean(data.readSetting("SYSTEM_TRAY", "true")))
				return;
			if(!playState)
				trayIcon.setImage(Toolkit.getDefaultToolkit().getImage("Resources/Pause.png"));
			else if(playState)
				trayIcon.setImage(Toolkit.getDefaultToolkit().getImage("Resources/p32.gif"));
		}
		@Override
		public void volumeChanged(final int vol) { /* ignore */ }
	}
}
