package gui;

import gui.dnd.ForeignDrop;
import gui.dnd.ListDropMode;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import java.lang.reflect.InvocationTargetException;
import lists.ListException;
import lists.ListProvider;
import lists.SearchListModel;
import lists.TrackListModel;


/**
 * Grafische Benutzeroberfläche für Party DJ.
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
	private ClassicWindow classicWindow = this;
	private JButton buttonPause;
	
	public ClassicWindow()
	{
		super("Party DJ");
		if(SwingUtilities.isEventDispatchThread())
		{
			initGUI();
		}
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){
					@Override
					public void run()
					{
						initGUI();
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

	protected void initGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/p32.gif"));
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
		add(control(), con);

		con.gridx = 0;
		con.gridy = 1;
		con.weightx = 1.0;
		con.weighty = 1.0;
		add(mainPart(), con);

		setVisible(true);
	}
	/**
	 * Lautstärkeregler, Fortschrittsbalken und Buttons werden zu einer Kontrolleinheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Steuerungen enthält.
	 */
	private Component control()
	{
		final JPanel control = new JPanel(new GridBagLayout());
		
		if(SwingUtilities.isEventDispatchThread())
		{
			GridBagConstraints c = new GridBagConstraints();
					
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
			c.ipadx = 50;
			c.gridx = 1;
			control.add(volume(), c);
			
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.ipadx = 500;
			c.gridx = 2;
			control.add(slider(), c);
		}
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){

					@Override
					public void run()
					{
						GridBagConstraints c = new GridBagConstraints();
						
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
						c.ipadx = 50;
						c.gridx = 1;
						control.add(volume(), c);
						
						c.weightx = 1.0;
						c.weighty = 0.0;
						c.ipadx = 500;
						c.gridx = 2;
						control.add(slider(), c);
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
		
				
		return control;
	}
	
	/**
	 * Alle Listen und die Suche werden zu einer Einheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Listen und die Suche enthält.
	 */
	private Component mainPart()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel mainPart = new JPanel(new GridBagLayout());	
			
		c.insets = new Insets(0, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		mainPart.setBackground(Color.darkGray);

		mainPart.add(list("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);

		
		c.gridy = 1;
		try
		{
			mainPart.add(list("Playlist", listProvider.getDbList("Playlist"), ListDropMode.COPY_OR_MOVE), c);
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
			mainPart.add(list("Wunschliste", listProvider.getDbList("Wunschliste"), ListDropMode.COPY_OR_MOVE), c);
		}
		catch (ListException e)
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
	private JPanel buttons()
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
		JButton bwd = customizeButton("Resources/Zurückspulen.png");
		JButton skipFWD = customizeButton("Resources/Vor.png");
		JButton skipBWD = customizeButton("Resources/Zurück.png");
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
		
		play.addMouseListener(new MouseAdapter()
		{
			@Override public void mouseClicked(MouseEvent me) 
			{
				player.start();
			}
		});
		
		pause.addMouseListener(new MouseAdapter()
				{
					@Override public void mouseClicked(MouseEvent me) 
					{
						player.fadeInOut();
					}
				});

		stop.addMouseListener(new MouseAdapter()
				{
					@Override public void mouseClicked(MouseEvent me) 
					{
						player.stop();
					}
				});
		
		fwd.addMouseListener(new MouseAdapter()
				{
					@Override public void mouseClicked(MouseEvent me) 
					{
						player.setPosition(player.getPosition() + 5);
					}
				});
		
		bwd.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent me) 
					{
						player.setPosition(player.getPosition() - 5);
					}
				});
		
		skipFWD.addMouseListener(new MouseAdapter()
					{
						@Override public void mouseClicked(MouseEvent me) 
						{
							player.playNext();
						}
					});
		
		skipBWD.addMouseListener(new MouseAdapter()
					{
						@Override public void mouseClicked(MouseEvent me) 
						{
							player.playPrevious();
						}
					});
		
		setting.addMouseListener(new MouseAdapter()
				{
					@Override public void mouseClicked(MouseEvent me) 
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
	 * @return JPanel mit GridBagLayout, welches die Liste und Titel enthält.
	 */
	private Component list(String title, TrackListModel l, ListDropMode ldMode)
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
	private Component search()
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
		textField.setBorder(BorderFactory.createEmptyBorder());
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
	private Component slider()
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
	 * Erzeugt den Lautstärkeregler.
	 * @return	JPanel mit GridBagLayout, welches den Lautstärkeregler beinhaltet.
	 */
	private Component volume()
	{
		volume = new JSlider(SwingConstants.VERTICAL, 0, 100, player.getVolume());
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
	
	public void setVolume(final int vol)
	{
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run()
			{
				volume.setValue(vol);
			}});
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
		if(SwingUtilities.isEventDispatchThread())
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
			
			classicWindow.setLocationRelativeTo(null); //Macht dass das Fenster in Bildschirmmitte steht
			
			addWindowStateListener(new WindowStateListener(){
				public void windowStateChanged(WindowEvent evt)
		        {
		            resize();
		        }});
			
			getContentPane().addComponentListener(new ComponentAdapter(){  
		        @Override public void componentResized(ComponentEvent evt) 
		        {
		            resize();
		        }});
		
			resize();
		}
		else
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){

					@Override
					public void run()
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
						
						classicWindow.setLocationRelativeTo(null); //Macht dass das Fenster in Bildschirmmitte steht
						
						addWindowStateListener(new WindowStateListener(){
							public void windowStateChanged(WindowEvent evt)
					        {
					            resize();
					        }});
						
						getContentPane().addComponentListener(new ComponentAdapter(){  
					        @Override public void componentResized(ComponentEvent evt) 
					        {
					            resize();
					        }});
					
						resize();
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
	
	/** Wird aufgerufen wenn sich die Größe des Fensters ändert.*/
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
		
		@Override
		public void currentTrackChanged(Track playedLast, final Track playingCurrent, Reason reason)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					if(playingCurrent != null)
						classicWindow.setTitle(playingCurrent.name + "   -   PartyDJ");
					else
						classicWindow.setTitle("PartyDJ");
				}});
			
		}

		@Override
		public void playStateChanged(final boolean playState)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					if(playState)
						buttonPause.setIcon(new ImageIcon("Resources/Pause.png"));
					else
						buttonPause.setIcon(new ImageIcon("Resources/Resume.png"));
				}});
			
		}

		@Override
		public void volumeChanged(int vol)
		{
			volume.setValue(vol);
		}
	}
	
	class VolumeListener implements ChangeListener, PlayStateListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if(e.getSource() instanceof JSlider)
				player.setVolume(((JSlider)e.getSource()).getValue());
		}

		@Override
		public void volumeChanged(final int newVolume)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					volume.setValue(newVolume);
				}});
			
		}

		@Override
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason){}

		@Override
		public void playStateChanged(boolean playState){}
	}
}

