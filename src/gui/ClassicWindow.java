package gui;

import gui.dnd.ForeignDrop;
import gui.dnd.ListDropMode;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.melloware.jintellitype.JIntellitype;
import players.IPlayer;
import players.PlayStateAdapter;
import players.PlayStateListener;
import common.Track;
import data.IData;
import data.SettingException;
import basics.Controller;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import lists.ListException;
import lists.ListProvider;
import lists.SearchListModel;
import lists.TrackListModel;


/**
 * Grafische Benutzeroberfl�che f�r Party DJ.
 * 
 * @author Sam
 */


public class ClassicWindow extends JFrame
{	
	private static final long serialVersionUID = 5672123337960808686L;
	private final Controller controller = Controller.getInstance();
	private final IPlayer player = controller.getPlayer();
	private final ListProvider listProvider = controller.getListProvider();
	private final IData data = controller.getData();
	private Container gcp = getContentPane();
	private PDJSlider slider;
	private JSlider volume;
	private ClassicWindow classicWindow;
	private JButton buttonPause;
	
	public ClassicWindow()
	{
		super("Party DJ");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/p32.gif"));
		classicWindow = this;
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		player.addPlayStateListener(new PlayState());

		GridBagConstraints con = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		
		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		manageSize();
		
		gcp.setBackground(Color.darkGray);
		
		con.gridx = 0;
		con.gridy = 0;
		con.weightx = 0.0;
		con.weighty = 0.0;
		con.ipadx = GridBagConstraints.REMAINDER;
		con.ipady = 22;
		add(Control(), con);

		con.gridx = 0;
		con.gridy = 1;
		con.weightx = 1.0;
		con.weighty = 1.0;
		add(MainPart(), con);

		initKeyStrokes();
		setVisible(true);
	}
	
	private void initKeyStrokes()
	{
		controller.getPlayer().addPlayStateListener(new VolumeListener());
		KeyStrokeManager ghk = KeyStrokeManager.getInstance();
		ghk.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "Pause");
		ghk.getActionMap().put("Pause", new AbstractAction(){
										private static final long serialVersionUID = 4607893203556916986L;

										@Override
										public void actionPerformed(ActionEvent e)
										{
											player.fadeInOut();
										}});
		
		JIntellitype.getInstance().addHotKeyListener(GlobalHotKeys.getInstance());
		JIntellitype.getInstance().addIntellitypeListener(GlobalHotKeys.getInstance());
	}

	/**
	 * Lautst�rkeregler, Fortschrittsbalken und Buttons werden zu einer Kontrolleinheit zusammengef�gt.
	 * @return JPanel mit GridBagLayout, welches alle Steuerungen enth�lt.
	 */
	private Component Control()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel control = new JPanel(new GridBagLayout());
				
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		control.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		control.add(Buttons(), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 50;
		c.gridx = 1;
		control.add(Volume(), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.ipadx = 500;
		c.gridx = 2;
		control.add(Slider(), c);
				
		return control;
	}
	
	/**
	 * Alle Listen und die Suche werden zu einer Einheit zusammengef�gt.
	 * @return JPanel mit GridBagLayout, welches alle Listen und die Suche enth�lt.
	 */
	private Component MainPart()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel mainPart = new JPanel(new GridBagLayout());	
				
		c.insets = new Insets(0, 3, 3, 3);;
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		mainPart.setBackground(Color.darkGray);

		mainPart.add(List("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);

		
		c.gridy = 1;
		try
		{
			mainPart.add(List("Playlist", listProvider.getDbList("Playlist"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (ListException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		c.gridx = 1;
		c.gridy = 0;
		try
		{
			mainPart.add(List("Wunschliste", listProvider.getDbList("Wunschliste"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Wunschliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		c.gridy = 1;
		mainPart.add(Search(), c);
		
		return mainPart;
	}
	
	/**
	 * Erzeugt die Buttons und ordnet sie mit einem GridBagLayout an.
	 * @return JPanel mit GridBagLayout, welches alle Buttons enth�lt.
	 */
	private JPanel Buttons()
	{
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.darkGray);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0.0;
		c.weighty = 0.0;

		JButton play = customizeButton("Resources/Play.png");
		JButton pause = customizeButton("Resources/Pause.png");
		JButton stop = customizeButton("Resources/Stop.png");
		JButton fwd = customizeButton("Resources/Vorspulen.png");
		JButton bwd = customizeButton("Resources/Zur�ckspulen.png");
		JButton skipFWD = customizeButton("Resources/Vor.png");
		JButton skipBWD = customizeButton("Resources/Zur�ck.png");
		JButton setting = customizeButton("Resources/Einstellungen.png");
	
	    /*InputMap pauseInput = pause.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    pauseInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "pause");
	    ActionMap pauseAction = pause.getActionMap();
	    pauseAction.put("pause", new AbstractAction("pause")
	    								{
											private static final long serialVersionUID = 7795134144645322604L;

											@Override
											public void actionPerformed(ActionEvent e)
											{
												System.out.println("Pause");
												player.fadeInOut();
										}});
	  
	    pause.setActionMap(pauseAction);*/
		
		pause.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.fadeInOut();
					}
				});

		stop.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.stop();
					}
				});
		
		fwd.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.setPosition(player.getPosition() + 5);
					}
				});
		
		bwd.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.setPosition(player.getPosition() - 5);
					}
				});
		
		skipFWD.addMouseListener(new MouseAdapter()
					{
						public void mouseClicked(MouseEvent me) 
						{
							player.playNext();
						}
					});
		
		skipBWD.addMouseListener(new MouseAdapter()
					{
						public void mouseClicked(MouseEvent me) 
						{
							player.playPrevious();
						}
					});
		
		setting.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						controller.registerWindow(new gui.settings.SettingWindow());
					}
				});
				
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
	 * @return JPanel mit GridBagLayout, welches die Liste und Titel enth�lt.
	 */
	private Component List(String title, TrackListModel l, ListDropMode ldMode)
	{
		GridBagConstraints c = new GridBagConstraints();
		
		PDJList list = new PDJList(l, ldMode, title);
		JScrollPane scrollPane = new JScrollPane(list);
		JLabel label = new JLabel(title);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(scrollPane);
		
		
		scrollPane.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		list.setForeground(new Color(0, 255, 0));
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		list.setBackground(Color.black);
		scrollPane.setBackground(Color.black);
		
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(label, c);
		
		c.ipadx = super.getSize().width;
		c.ipady = super.getSize().height;
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
	private Component Search()
	{
		final JTextField textField = new JTextField();												// final damit die innere Klasse
		final PDJList searchList = new PDJList(new SearchListModel(), ListDropMode.NONE, "Search");	// darauf zugreifen kann.
		JScrollPane scrollPane = new JScrollPane(searchList);
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel label = new JLabel("Suche");
		
		//DnD
		textField.setDropMode(DropMode.INSERT);
		new DropTarget(textField, new ForeignDrop());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		textField.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		scrollPane.setAutoscrolls(false);
		
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		textField.setBackground(Color.black);
		textField.setForeground(Color.green);
		
		searchList.setForeground(new Color(0, 255, 0));
		searchList.setBackground(Color.black);
		
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
			public void actionPerformed(ActionEvent arg0)
			{
				String text = textField.getText();
				if(text == null)
					text = "";

				Color bgColor = textField.getBackground();
				try
				{
					textField.setBackground(Color.GRAY);
					//TODO textField neu zeichnen
					((SearchListModel)searchList.getListModel()).search(text);
				}
				catch (ListException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Suche fehlgeschlagen.\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					textField.setBackground(bgColor);
				}
			}});
		
		return panel;
	}
	
	/**Erzeugt den Fortschrittsbalken.
	 * @return JPanel mit GridBagLayout, welches den Titel und Slider beinhaltet.
	 */
	private Component Slider()
	{
		try
		{
			slider = new PDJSlider();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.setBackground(Color.darkGray);
		slider.setBackground(Color.darkGray);
		slider.setForeground(Color.green);
		GridBagConstraints c = new GridBagConstraints();	
			
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		panel.add(slider, c);
		
		return panel;
	}
	
	/**
	 * Erzeugt den Lautst�rkeregler.
	 * @return	JPanel mit GridBagLayout, welches den Lautst�rkeregler beinhaltet.
	 */
	private Component Volume()
	{
		volume = new JSlider(JSlider.VERTICAL, 0, 100, player.getVolume());
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
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
	
	public void setVolume(int vol)
	{
		volume.setValue(vol);
	}
	
	
	private JButton customizeButton(String iconPath)
	{
		JButton button = new JButton(new ImageIcon(iconPath));

		button.setMargin(new Insets(0 ,0, 0, 0));
		
		button.setFocusPainted(false);
		
		button.setBackground(Color.darkGray);
		
		Border buttonBorder = new javax.swing.plaf.basic.BasicBorders.ButtonBorder(Color.gray, Color.black, Color.black, Color.gray);
		
		button.setBorder(buttonBorder);

		return button;
	}
	
	
	private void manageSize()
	{
		try
		{
			setSize(Integer.parseInt(data.readSetting("ClassicWindowWidth", "800")), Integer.parseInt(data.readSetting("ClassicWindowHeight", "600")));
		}
		catch (NumberFormatException e1)
		{
			setSize(800, 600);
		}
		catch (SettingException e1)
		{
			setSize(800, 600);
		}
		
		try
		{
			setExtendedState(Integer.parseInt(data.readSetting("ClassicWindowState", Integer.toString(MAXIMIZED_BOTH))));
		}
		catch (NumberFormatException e)
		{
			setExtendedState(MAXIMIZED_BOTH);
		}
		catch (SettingException e)
		{
			setExtendedState(MAXIMIZED_BOTH);
		}
		
		this.setLocationRelativeTo(null); //Macht dass das Fenster in Bildschirmmitte steht
		
		addWindowStateListener(new WindowStateListener(){
			public void windowStateChanged(WindowEvent evt)
	        {
	            resize();
	        }});
		
		getContentPane().addComponentListener(new ComponentAdapter(){  
	        public void componentResized(ComponentEvent evt) 
	        {
	            resize();
	        }});
	
		resize();
	}
	
	/** Wird aufgerufen wenn sich die Gr��e des Fensters �ndert.*/
	private void resize()
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
		catch (SettingException e)
		{
			e.printStackTrace();
		}
        
	}
	
	class PlayState extends PlayStateAdapter
	{
		public int duration;
		
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
		{
			if(playingCurrent != null)
				classicWindow.setTitle(playingCurrent.name + "   -   PartyDJ");
			else
				classicWindow.setTitle("PartyDJ");
		}

		public void playStateChanged(boolean playState)
		{
			if(playState)
				buttonPause.setIcon(new ImageIcon("Resources/Pause.png"));
			else
				buttonPause.setIcon(new ImageIcon("Resources/Resume.png"));
		}

		public void volumeChanged(int vol)
		{
			volume.setValue(vol);
		}
	}
	
	class VolumeListener implements ChangeListener, PlayStateListener
	{
		public void stateChanged(ChangeEvent e)
		{
			JSlider slider;
			if(e.getSource() instanceof JSlider)
				slider = (JSlider)e.getSource();
			
			else
				return;
			
			player.setVolume(slider.getValue());
		}

		@Override
		public void volumeChanged(int volume)
		{
			classicWindow.volume.setValue(volume);
		}

		@Override
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
		{
			//System.out.println(reason);
		}

		@Override
		public void playStateChanged(boolean playState)
		{
			//System.out.println(playState);
		}
	}
}

