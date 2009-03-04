package gui;

import gui.DnD.ListDropMode;
import javax.swing.*;
import javax.swing.border.Border;
import common.IPlayer;
import common.ListException;
import basics.Controller;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lists.EmptyListModel;

/**
 * Grafische Benutzeroberfläche für Party DJ.
 * 
 * @author Sam
 * @version 0.0.0.0.0.1
 */


public class ClassicWindow extends JFrame
{	
	private static final long serialVersionUID = 5672123337960808686L;
	private Container gcp = getContentPane();
	
	public ClassicWindow()
	{
		super("Party DJ");
		assert Controller.instance != null : "Controller nicht geladen!";
		
		GridBagConstraints con = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		
		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		setSize((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 3, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 3);
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 4);
		gcp.setBackground(Color.darkGray);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
	
		setExtendedState(MAXIMIZED_BOTH);
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
		c.gridx = 1;
		control.add(Volume(), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 2;
		control.add(Slider("Titel"), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
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
		
		c.anchor = GridBagConstraints.WEST;
		mainPart.setBackground(Color.darkGray);

		try
		{
			mainPart.add(List("Alle", basics.Controller.instance.listProvider.getMasterList(), ListDropMode.DELETE), c);
		}
		catch (ListException e)
		{
			JOptionPane.showMessageDialog(this, "Konnte Hauptliste nicht erstellen!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		c.gridy = 1;
		mainPart.add(List("Playlist", new lists.LightClientListModel(), ListDropMode.COPY_OR_MOVE), c);

		c.gridx = 1;
		c.gridy = 0;
		try
		{
			mainPart.add(List("Wunschliste", basics.Controller.instance.listProvider.getDbList("Test"), ListDropMode.COPY_OR_MOVE), c);
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
		final IPlayer player = basics.Controller.instance.player;
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
						System.out.println("MOUSE pressed");
						fwd = true;
						new fwdThread().start();
					}
						
					public void mouseReleased(MouseEvent me) 
					{
						System.out.println("MOUSE released");
						fwd = false;
					}
					
					class fwdThread extends Thread
					{
						public void run()
						{
							System.out.println("run new thread");
							while(fwd == true)
							{
								System.out.println("time +2seconds");
								player.setPosition(player.getPosition() + 2);
								/*try
								{
									player.wait(40);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
									break;
								}*/
							}
							
						}
					}
					
				});
		
		bwd.addMouseListener(new MouseAdapter()
				{
					boolean bwd = true;
					public void mousePressed(MouseEvent me) 
					{
						System.out.println("MOUSE pressed");
						while(bwd == true)
						{
							System.out.println("time +2seconds");
							player.setPosition(player.getPosition() - 2);
						}
							
					}
					
					public void mouseReleased(MouseEvent me) 
					{
						System.out.println("MOUSE released");
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
		scrollPane.setVisible(true);
		list.setVisible(true);
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
		c.anchor = GridBagConstraints.WEST;
		
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
		JTextField textField = new JTextField();
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel label = new JLabel("Suche");
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(8, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		textField.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		textField.setBackground(Color.black);
		textField.setForeground(Color.green);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		panel.add(label, c);
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy = 2;
		panel.add(List("Ergebnisse", new EmptyListModel(), ListDropMode.NONE), c);
		
		c.weighty = 0.0;
		c.ipady = 8;
		c.gridy = 1;
		panel.add(textField, c);
		
		return panel;
	}
	
	/**
	 * Erzeugt den Fortschrittsbalken.
	 * @param Titel des gespielten Liedes.
	 * @return JPanel mit GridBagLayout, welches den Titel und Slider beinhaltet.
	 */
	public Component Slider(String title)
	{
		JLabel label = new JLabel(title);
		JSlider slider = new JSlider(0, 100);
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.setBackground(Color.darkGray);
		label.setBackground(Color.darkGray);
		label.setForeground(Color.green);
		slider.setBackground(Color.darkGray);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 16)); 		
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		c.gridx = 0;
		c.gridy = 1;
		panel.add(label, c);
		
		c.gridy = 0;
		panel.add(slider, c);
		
		return panel;
	}
	
	/**
	 * Erzeugt den Lautstärkeregler.
	 * @return	JPanel mit GridBagLayout, welches den Lautstärkeregler beinhaltet.
	 */
	public Component Volume()
	{
		JSlider volume = new JSlider(JSlider.VERTICAL, 0, 100, 0);
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		panel.setBackground(Color.darkGray);
		volume.setBackground(Color.darkGray);
		
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.0;
		panel.add(volume, c);
		
		return panel;
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
}
