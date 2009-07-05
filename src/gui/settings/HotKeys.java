package gui.settings;

import gui.KeyStrokeManager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import basics.Controller;
import data.IData;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class HotKeys extends javax.swing.JPanel 
{
	private static final long serialVersionUID = 6338055709683059526L;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JTextField volUpL;
	private JTextField volUpG;
	private JTextField setOnPlaylist;
	private JLabel jLabel16;
	private JButton clearLocal;
	private JButton clearGlobal;
	private JLabel jLabel15;
	private JLabel jLabel14;
	private JTextField nextG;
	private JTextField nextL;
	private JTextField stopG;
	private JTextField stopL;
	private JTextField volDownG;
	private JTextField volDownL;
	private JTextField prevG;
	private JTextField prevL;
	private JTextField playG;
	private JTextField playL;
	private JLabel jLabel13;
	private JLabel jLabel12;
	private JLabel jLabel11;
	private JLabel jLabel10;
	private JLabel jLabel9;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabel1;

	public HotKeys() 
	{
		super();
		initGUI();
		initFuntions();
	}
	
	protected void initFuntions()
	{
		playL.setName("PLAY_PAUSE");
		playG.setName("PLAY_PAUSE");
		stopL.setName("STOP");
		stopG.setName("STOP");
		nextL.setName("NEXT");
		nextG.setName("NEXT");
		prevL.setName("PREVIOUS");
		prevG.setName("PREVIOUS");
		volUpL.setName("VOLUME_UP");
		volUpG.setName("VOLUME_UP");
		volDownL.setName("VOLUME_DOWN");
		volDownG.setName("VOLUME_DOWN");
		setOnPlaylist.setName("SET_ON_PLAYLIST");
		
		clearGlobal.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for(String cmd : KeyStrokeManager.getInstance().getGlobalHotKeys())
				{
					KeyStrokeManager.getInstance().disableGlobalHotKey(cmd.split("@")[2].hashCode());
				}
				playG.setText("");
				stopG.setText("");
				volDownG.setText("");
				volUpG.setText("");
				nextG.setText("");
				prevG.setText("");
				setOnPlaylist.setText("");
			}});

		clearLocal.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				KeyStrokeManager.getInstance().disableLocalHotKeys();
				playL.setText("");
				stopL.setText("");
				volDownL.setText("");
				volUpL.setText("");
				nextL.setText("");
				prevL.setText("");
			}});
		
		KeyListener listener = new GlobalHotKeySetter(playG, stopG, volDownG, volUpG, nextG, prevG);
		playG.addKeyListener(listener);
		stopG.addKeyListener(listener);
		volDownG.addKeyListener(listener);
		volUpG.addKeyListener(listener);
		nextG.addKeyListener(listener);
		prevG.addKeyListener(listener);
		setOnPlaylist.addKeyListener(listener);
		
		listener = new LocalHotKeySetter(playL, stopL, volDownL, volUpL, nextL, prevL);
		playL.addKeyListener(listener);
		stopL.addKeyListener(listener);
		volDownL.addKeyListener(listener);
		volUpL.addKeyListener(listener);
		nextL.addKeyListener(listener);
		prevL.addKeyListener(listener);
		
		setGlobalFieldText(playG, stopG, prevG, nextG, volUpG, volDownG, setOnPlaylist);
		setLocalFieldText(playL, stopL, volDownL, volUpL, nextL, prevL);
	}
	
	protected void setGlobalFieldText(JTextField...fields)
	{
		IData data = Controller.getInstance().getData();
		JTextField[] txtFields = fields;
		
		String raw = data.readSetting("GlobalHotKeys");
		
		if(raw != null)
		{
			if(raw.length() < 1)
				return;
			raw = raw.substring(1);
			String[] regKeys = raw.split("§");
			for(String k : regKeys)
			{
				String[] key = k.split("@");
				for(JTextField toSet : txtFields)
				{
					if(key[2].equalsIgnoreCase(toSet.getName()))
					{
						String[] represent = KeyStroke.getKeyStroke(Integer.valueOf(key[1]), Integer.valueOf(key[0])).toString().split("pressed ");
						StringBuilder sb = new StringBuilder();
						sb.append(represent[0].toUpperCase());
						sb.append(represent[1]);
						toSet.setText(sb.toString());
					}
				}
			}
		}
	}
	
	protected void setLocalFieldText(JTextField...fields)
	{
		IData data = Controller.getInstance().getData();
		JTextField[] txtFields = fields;
		
		String raw = data.readSetting("LocalHotKeys", "§0@102@PLAY_PAUSE§0@100@PREVIOUS§0@98@VOLUME_DOWN§0@104@VOLUME_UP§0@102@NEXT§0@101@PLAY_PAUSE§0@96@STOP");
		if(raw != null)
		{
			if(raw.length() < 1)
				return;
			raw = raw.substring(1);
			String[] regKeys = raw.split("§");
			for(String k : regKeys)
			{
				String[] key = k.split("@");
				for(JTextField toSet : txtFields)
				{
					if(key[2].equalsIgnoreCase(toSet.getName()))
					{
						String[] represent = KeyStroke.getKeyStroke(Integer.valueOf(key[1]), Integer.valueOf(key[0])).toString().split("pressed ");
						StringBuilder sb = new StringBuilder();
						sb.append(represent[0].toUpperCase());
						sb.append(represent[1]);
						while(sb.length() < 10)
							sb.append(" ");
						toSet.setText(sb.toString());
					}
				}
			}
		}
	}
	
	protected class GlobalHotKeySetter implements KeyListener
	{
		protected JTextField[] fields;
		protected KeyStrokeManager manager = KeyStrokeManager.getInstance();
		protected int key = 0;
		protected int mod = 0;
		protected boolean ignore = false;
		
		public GlobalHotKeySetter(JTextField...txts)
		{
			fields = txts;
		}
		
		@Override
		public void keyPressed(final KeyEvent e){}

		@Override
		public void keyReleased(final KeyEvent e)
		{
			if(!(e.getComponent() instanceof JTextField))
				return;
			
			final JTextField actual = (JTextField) e.getComponent();
			
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					if(e.getKeyCode() == 0)
					{
						actual.setText("Unknown Key!");
						return;
					}
					if(e.getModifiers() != 0)
					{
						ignore = true;
						key = e.getKeyCode();
						mod = e.getModifiers();
					}
					else if(ignore)
					{
						ignore = false;
						manager.enableGlobalHotKey(mod, key, actual.getName());
						actual.setText(KeyEvent.getKeyText(e.getKeyCode()) + "  " + KeyEvent.getKeyText(key));
					}
					else
					{
						manager.enableGlobalHotKey(e.getModifiers(), e.getKeyCode(), actual.getName());
						actual.setText(KeyEvent.getKeyText(e.getKeyCode()));
					}
					actual.repaint();
				}});
		}

		@Override
		public void keyTyped(final KeyEvent e){}
	}
	
	protected class LocalHotKeySetter implements KeyListener
	{
		protected JTextField[] fields;
		protected KeyStrokeManager manager = KeyStrokeManager.getInstance();
		protected int key = 0;
		protected int mod = 0;
		protected boolean ignore = false;
		
		public LocalHotKeySetter(JTextField...txts)
		{
			fields = txts;
		}
		
		@Override
		public void keyPressed(final KeyEvent e){}

		@Override
		public void keyReleased(final KeyEvent e)
		{
			InputMap iMap = manager.getInputMap();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
			if(!(e.getComponent() instanceof JTextField))
				return;
			
			final JTextField actual = (JTextField) e.getComponent();
			
			if(iMap.get(keyStroke) != null)
			{
				for(final JTextField field : fields)
				{
					if(iMap.get(keyStroke).toString().equalsIgnoreCase(field.getName()))
					{		
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run()
							{
								field.setText("NONE          ");
								field.repaint();
							}});
					}
				}
			}
			
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					if(e.getKeyCode() == 0)
					{
						actual.setText("Unknown Key!");
						return;
					}
					if(e.getModifiers() != 0)
					{
						ignore = true;
						key = e.getKeyCode();
						mod = e.getModifiers();
					}
					else if(ignore)
					{
						ignore = false;
						manager.enableLocalHotKey(mod, key, actual.getName());
						actual.setText(KeyEvent.getKeyText(e.getKeyCode()) + "  " + KeyEvent.getKeyText(key));
					}
					else
					{
						manager.enableLocalHotKey(e.getModifiers(), e.getKeyCode(), actual.getName());
						actual.setText(KeyEvent.getKeyText(e.getKeyCode()));
					}
					actual.repaint();
				}});
		}
		
		@Override
		public void keyTyped(KeyEvent e){}
	}
	
	protected void initGUI() 
	{
		try 
		{
			GridBagLayout thisLayout = new GridBagLayout();
			this.setPreferredSize(new java.awt.Dimension(826, 557));
			thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
			thisLayout.rowHeights = new int[] {15, 10, 10, 10, 10, 10, 10, 10, 10, 20, 54, 20};
			thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			thisLayout.columnWidths = new int[] {140, 115, 115, 140, 115, 115};
			this.setLayout(thisLayout);
			this.setFont(new java.awt.Font("Dialog",1,20));
			{
				jLabel1 = new JLabel();
				this.add(jLabel1, new GridBagConstraints(0, 0, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 9, 3, 3), 0, 0));
				jLabel1.setText("HotKeys");
				jLabel1.setFont(new java.awt.Font("SansSerif",1,24));
			}
			{
				jLabel2 = new JLabel();
				this.add(jLabel2, new GridBagConstraints(0, 1, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 9, 3, 3), 0, 0));
				jLabel2.setText("Hier können sie bestimmen, mit welchen Tasten Sie den PartyDJ steuern wollen. Mediatasten müssen nicht extra gesetzt werden.");
				jLabel2.setFont(new java.awt.Font("SansSerif",2,12));
			}
			{
				jLabel3 = new JLabel();
				this.add(jLabel3, new GridBagConstraints(0, 2, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 9, 3, 3), 0, 0));
				jLabel3.setText("ACHTUNG:");
				jLabel3.setFont(new java.awt.Font("SansSerif",3,14));
			}
			{
				jLabel4 = new JLabel();
				this.add(jLabel4, new GridBagConstraints(0, 3, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel4.setText("Globale HotKeys fuktionieren immer.");
				jLabel4.setFont(new java.awt.Font("SansSerif",2,12));
			}
			{
				jLabel6 = new JLabel();
				this.add(jLabel6, new GridBagConstraints(0, 4, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel6.setText("Bsp: Sie setzen die Leertaste global f\u00fcr Play / Pause. Wenn sie dann in Word ein Leerzeichen tippen pausiert der PartyDJ!");
				jLabel6.setFont(new java.awt.Font("SansSerif",2,12));
			}
			{
				jLabel5 = new JLabel();
				this.add(jLabel5, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel5.setText("Play / Pause:");
			}
			{
				jLabel7 = new JLabel();
				this.add(jLabel7, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel7.setText("Vorheriges Lied:");
			}
			{
				jLabel8 = new JLabel();
				this.add(jLabel8, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel8.setText("Leiser:");
			}
			{
				jLabel9 = new JLabel();
				this.add(jLabel9, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
				jLabel9.setText("Lokal:");
			}
			{
				jLabel10 = new JLabel();
				this.add(jLabel10, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
				jLabel10.setText("Global:");
			}
			{
				jLabel11 = new JLabel();
				this.add(jLabel11, new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel11.setText("Nächstes Lied:");
			}
			{
				jLabel12 = new JLabel();
				this.add(jLabel12, new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel12.setText("Lauter:");
			}
			{
				jLabel13 = new JLabel();
				this.add(jLabel13, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel13.setText("Stop:");
			}
			{
				playL = new JTextField();
				this.add(playL, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				playG = new JTextField();
				this.add(playG, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				prevL = new JTextField();
				this.add(prevL, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				prevG = new JTextField();
				this.add(prevG, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				volDownL = new JTextField();
				this.add(volDownL, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				volDownG = new JTextField();
				this.add(volDownG, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				stopL = new JTextField();
				this.add(stopL, new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				stopG = new JTextField();
				this.add(stopG, new GridBagConstraints(5, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				nextL = new JTextField();
				this.add(nextL, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				nextG = new JTextField();
				this.add(nextG, new GridBagConstraints(5, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				volUpL = new JTextField();
				this.add(volUpL, new GridBagConstraints(4, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				volUpG = new JTextField();
				this.add(volUpG, new GridBagConstraints(5, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				jLabel14 = new JLabel();
				this.add(jLabel14, new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
				jLabel14.setText("Lokal:");
			}
			{
				jLabel15 = new JLabel();
				this.add(jLabel15, new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
				jLabel15.setText("Global:");
			}
			{
				clearGlobal = new JButton();
				this.add(clearGlobal, new GridBagConstraints(5, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
				clearGlobal.setText("<html><p style=\\\"text-align:center\\\"><b>Alle globalen</font></b><br>HotKeys l\u00f6schen!</p></html>");
			}
			{
				clearLocal = new JButton();
				this.add(clearLocal, new GridBagConstraints(4, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
				clearLocal.setText("<html><p style=\\\"text-align:center\\\"><b>Alle lokalen</font></b><br>HotKeys l\u00f6schen!</p></html>");
			}
			{
				jLabel16 = new JLabel();
				this.add(jLabel16, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 15), 0, 0));
				jLabel16.setText("<html>Lied auf Wunschliste<br>setzen:</br></html>");
			}
			{
				setOnPlaylist = new JTextField();
				this.add(setOnPlaylist, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
