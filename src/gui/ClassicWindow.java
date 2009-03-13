package gui;

import gui.dndy.ListDropMode;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import players.IPlayer;
import common.Track;
import data.IData;
import data.SettingException;
import basics.Controller;
import basics.PlayStateListener;
import java.awt.*;
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


/**
 * Grafische Benutzeroberfläche für Party DJ.
 * 
 * @author Sam
 * @version 0.0
 */


public class ClassicWindow extends JFrame
{	
	private static final long serialVersionUID = 5672123337960808686L;
	private final Controller controller = Controller.getInstance();
	private final IPlayer player = controller.getPlayer();
	private final ListProvider listProvider = controller.getListProvider();
	private final IData data = controller.getData();
	private Container gcp = getContentPane();
	private Timer refreshTimer;
	private PDJSlider slider;
	private JLabel label;
	private static JSlider volume;
	private ClassicWindow classicWindow;
	
	public ClassicWindow()
	{
		super("Party DJ");
		this.setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/p32.gif"));
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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		controller.registerWindow(this);
		
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
		
		setVisible(true);
	}
	
	/**
	 * Lautstärkeregler, Fortschrittsbalken und Buttons werden zu einer Kontrolleinheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Steuerungen enthält.
	 */
	public Component Control()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel control = new JPanel(new GridBagLayout());
		
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		control.setBackground(Color.darkGray);	
		
		c.weightx = 0.05;
		c.weighty = 0.0;
		c.ipadx = 75;
		c.gridx = 1;
		control.add(Volume(), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.ipadx = 500;
		c.gridx = 2;
		control.add(Slider(), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		control.add(Buttons(), c);
		
		return control;
	}
	
	/**
	 * Alle Listen und die Suche werden zu einer Einheit zusammengefügt.
	 * @return JPanel mit GridBagLayout, welches alle Listen und die Suche enthält.
	 */
	public Component MainPart()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel mainPart = new JPanel(new GridBagLayout());	
		
		c.insets = new Insets(0, 3, 3, 3);;
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		mainPart.setBackground(Color.darkGray);

		try
		{
			mainPart.add(List("Alle", listProvider.getMasterList(), ListDropMode.DELETE), c);
		}
		catch (ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Hauptliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
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
			Controller.getInstance().setPlayList(listProvider.getDbList("Wunschliste"));
		}
		catch (ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Testliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		c.gridy = 1;
		mainPart.add(Search(), c);
		
		return mainPart;
	}
	
	/**
	 * Erzeugt die Buttons und ordnet sie mit einem GridBagLayout an.
	 * @return JPanel mit GridBagLayout, welches alle Buttons enthält.
	 */
	public JPanel Buttons()
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
		JButton fade = customizeButton("Resources/Abblenden.png");
		
		play.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me) 
			{
				player.play();
			}
		});

		pause.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.pause();
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
					boolean fwd = true;
					public void mousePressed(MouseEvent me) 
					{
						fwd = true;
						new fwdThread().start();
					}
						
					public void mouseReleased(MouseEvent me) 
					{
						fwd = false;
					}
					
					class fwdThread extends Thread
					{
						public void run()
						{
							while(fwd == true)
							{
								player.setPosition(player.getPosition() + 2);
							}
						}
					}
				});
		
		bwd.addMouseListener(new MouseAdapter()
				{
					boolean bwd = true;
					public void mousePressed(MouseEvent me) 
					{
						while(bwd == true)
						{
							player.setPosition(player.getPosition() - 2);
						}
							
					}
					
					public void mouseReleased(MouseEvent me) 
					{
						bwd = false;
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
		
		fade.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						player.fadeInOut();
					}
				});
				
				c.insets = new Insets(1, 1, 1, 1);
				
				panel.add(play, c);
				panel.add(pause, c);
				panel.add(fade, c);
				panel.add(stop, c);
				
				c.gridy = 1;
				
				panel.add(skipBWD, c);
				panel.add(bwd, c);
				panel.add(fwd, c);
				panel.add(skipFWD, c);
		
		
		return panel;
		
	}

	/**
	 * Erzeugt eine Liste mit Titel.
	 * @param Titel der Liste, Liste, DropMode
	 * @return JPanel mit GridBagLayout, welches die Liste und Titel enthält.
	 */
	public Component List(String title, ListModel l, ListDropMode ldMode)
	{
		GridBagConstraints c = new GridBagConstraints();
		
		PDJList list = new PDJList(l, ldMode, title);
		JScrollPane scrollPane = new JScrollPane(list);
		JLabel label = new JLabel(title);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(scrollPane);
		
		
		scrollPane.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		//scrollPane.setVisible(true);
		//list.setVisible(true);
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
		//c.anchor = GridBagConstraints.WEST;
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
	public Component Search()
	{
		final JTextField textField = new JTextField();					// final damit die innere Klasse
		final PDJList searchList = new PDJList(new SearchListModel());	// darauf zugreifen kann.
		JScrollPane scrollPane = new JScrollPane(searchList);
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel label = new JLabel("Suche");
		
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
				if(text != null && !text.equals(""))
				{
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
				}
			}});
		
		return panel;
	}
	
	/**
	 * Erzeugt den Fortschrittsbalken.
	 * @param Titel des gespielten Liedes.
	 * @return JPanel mit GridBagLayout, welches den Titel und Slider beinhaltet.
	 */
	public Component Slider()
	{
		label = new JLabel(" ");
		slider = new PDJSlider();
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18)); 		
		GridBagConstraints c = new GridBagConstraints();	
		
		refreshTimer = new Timer(0, new ActionListener()
									{
										public void actionPerformed(ActionEvent evt)
										{
											slider.setStartLabel(common.Functions.formatTime(player.getPosition()));
											slider.setEndLabel("-" + common.Functions.formatTime(player.getDuration() - player.getPosition()));
											slider.setValue((int)(player.getPosition() * 10000));
										}
									});
	
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.2;

		c.gridx = 0;
		c.gridy = 1;
		panel.add(label, c);
		
		c.gridy = 0;
		panel.add(slider, c);
	
		refreshTimer.setDelay(40);
		
		return panel;
	}
	
	/**
	 * Erzeugt den Lautstärkeregler.
	 * @return	JPanel mit GridBagLayout, welches den Lautstärkeregler beinhaltet.
	 */
	public Component Volume()
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
	
	public static void setVolume(int vol)
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
	
	/**Wird am Ende des Konstruktors aufgerufen um alles zu regeln was die Fenstergröße betrifft.*/
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
		
		this.setLocationRelativeTo(null); //Macht dass Fenster in Bildschirmmitte steht
		
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
	
	class PlayState implements PlayStateListener
	{
		public int duration;
		
		public void currentTrackChanged(Track playedLast, Track playingCurrent)
		{
			label.setText(playingCurrent.name);
			duration = (int)playingCurrent.duration;
			
			volume.setValue(player.getVolume());
	
			classicWindow.setTitle(playingCurrent.name + "   -   PartyDJ");	
			
			slider.setMiddleLabel(common.Functions.formatTime(duration));
			slider.setMaximum(duration * 10000);
			slider.setMajorTickSpacing(duration * 2500);
			slider.setMinorTickSpacing(duration * 1250);
		}

		public void playStateChanged(boolean playState)
		{
			if(playState)
				refreshTimer.start();
			
			else
				refreshTimer.stop();
			
		}
	}
	
	class VolumeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			JSlider slider;
			if(e.getSource() instanceof JSlider)
				slider = (JSlider)e.getSource();
			else
				slider = new JSlider();
			
			player.setVolume(slider.getValue());
		}
		
	}
}

