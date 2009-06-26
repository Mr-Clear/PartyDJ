/*
 * Created by JFormDesigner on Tue Jun 02 14:14:35 CEST 2009
 */

package gui.settings;

import gui.KeyStrokeManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import basics.Controller;
import com.jgoodies.forms.layout.*;
import data.IData;

/**
 * @author Sam Meier
 */
public class HotKeys extends JPanel 
{
	private static final long serialVersionUID = -8152114130330377204L;
	
	public HotKeys() 
	{
		initComponents();
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
		
		listener = new LocalHotKeySetter(playL, stopL, volDownL, volUpL, nextL, prevL);
		playL.addKeyListener(listener);
		stopL.addKeyListener(listener);
		volDownL.addKeyListener(listener);
		volUpL.addKeyListener(listener);
		nextL.addKeyListener(listener);
		prevL.addKeyListener(listener);
		
		setGlobalFieldText(playG, stopG, prevG, nextG, volUpG, volDownG);
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
						while(sb.length() < 10)
							sb.append(" ");
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
			
			//TODO anderes Textfeld löschen
//			for(String raw : manager.getHotKeys())
//			{
//				String[] key = raw.split("@");
//				KeyStroke keyStroke = KeyEvent.getKeyText(key);
//				for(JTextField field : fields)
//				{
//					
//				}
//			}
			
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

	private void initComponents() 
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sam Meier
		label1 = new JLabel();
		label2 = new JLabel();
		label13 = new JLabel();
		label14 = new JLabel();
		label15 = new JLabel();
		label6 = new JLabel();
		label7 = new JLabel();
		label8 = new JLabel();
		label9 = new JLabel();
		label3 = new JLabel();
		playL = new JTextField();
		playG = new JTextField();
		label10 = new JLabel();
		stopL = new JTextField();
		stopG = new JTextField();
		label11 = new JLabel();
		prevL = new JTextField();
		prevG = new JTextField();
		label4 = new JLabel();
		nextL = new JTextField();
		nextG = new JTextField();
		label12 = new JLabel();
		volDownL = new JTextField();
		volDownG = new JTextField();
		label5 = new JLabel();
		volUpL = new JTextField();
		volUpG = new JTextField();
		clearLocal = new JButton();
		clearGlobal = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========

		setLayout(new FormLayout(
			"$rgap, $lcgap, 37dlu, $lcgap, 33dlu, $lcgap, 29dlu, 9dlu, 81dlu, $lcgap, 47dlu, $lcgap, 77dlu, $lcgap, $rgap",
			"fill:25dlu, $lgap, fill:17dlu, 2*($lgap, default), $rgap, 13dlu, 2*(14dlu), $lgap, 12dlu, $lgap, 5dlu, $lgap, 27dlu, 10dlu, 33dlu"));
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{3, 9}, {5, 7, 11, 13}});
		((FormLayout)getLayout()).setRowGroups(new int[][] {{13, 15, 17}});

		//---- label1 ----
		label1.setText("HotKeys");
		label1.setFont(new Font("Tahoma", Font.BOLD, 24));
		add(label1, cc.xywh(3, 1, 11, 1));

		//---- label2 ----
		label2.setText("Hier k\u00f6nnen sie bestimmen, mit welchen Tasten Sie den PartyDJ steuern wollen. Mediatasten m\u00fcssen nicht extra gesetzt werden.");
		label2.setFont(new Font("Tahoma", Font.ITALIC, 13));
		add(label2, cc.xywh(3, 3, 11, 1));

		//---- label13 ----
		label13.setText("ACHTUNG:");
		label13.setFont(new Font("Tahoma", Font.ITALIC, 13));
		add(label13, cc.xywh(3, 5, 11, 1));

		//---- label14 ----
		label14.setText(" Globale HotKeys fuktionieren immer.");
		label14.setFont(new Font("Tahoma", Font.ITALIC, 13));
		add(label14, cc.xywh(3, 7, 12, 1));

		//---- label15 ----
		label15.setText(" Bsp: Sie setzen die Leertaste global f\u00fcr Play / Pause. Wenn sie dann in Word ein Leerzeichen tippen pausiert der PartyDJ!");
		label15.setFont(new Font("Tahoma", Font.ITALIC, 13));
		add(label15, cc.xywh(3, 9, 11, 1));

		//---- label6 ----
		label6.setText("Lokal:");
		label6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label6, cc.xy(5, 11));

		//---- label7 ----
		label7.setText("Global:");
		label7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label7, cc.xy(7, 11));

		//---- label8 ----
		label8.setText("Lokal:");
		label8.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label8, cc.xy(11, 11));

		//---- label9 ----
		label9.setText("Global:");
		label9.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label9, cc.xy(13, 11));

		//---- label3 ----
		label3.setText("Play / Pause:");
		label3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label3, cc.xy(3, 13));

		//---- playL ----
		playL.setBackground(Color.white);
		playL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		playL.setEditable(false);
		add(playL, cc.xy(5, 13));

		//---- playG ----
		playG.setBackground(Color.white);
		playG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		playG.setEditable(false);
		add(playG, cc.xy(7, 13));

		//---- label10 ----
		label10.setText("Stop:");
		label10.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label10, cc.xy(9, 13));

		//---- stopL ----
		stopL.setBackground(Color.white);
		stopL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		stopL.setEditable(false);
		add(stopL, cc.xy(11, 13));

		//---- stopG ----
		stopG.setBackground(Color.white);
		stopG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		stopG.setEditable(false);
		add(stopG, cc.xy(13, 13));

		//---- label11 ----
		label11.setText("Vorheriges Lied:");
		label11.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label11, cc.xy(3, 15));

		//---- prevL ----
		prevL.setBackground(Color.white);
		prevL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		prevL.setEditable(false);
		add(prevL, cc.xy(5, 15));

		//---- prevG ----
		prevG.setBackground(Color.white);
		prevG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		prevG.setEditable(false);
		add(prevG, cc.xy(7, 15));

		//---- label4 ----
		label4.setText("N\u00e4chstes Lied:");
		label4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label4, cc.xy(9, 15));

		//---- nextL ----
		nextL.setBackground(Color.white);
		nextL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		nextL.setEditable(false);
		add(nextL, cc.xy(11, 15));

		//---- nextG ----
		nextG.setBackground(Color.white);
		nextG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		nextG.setEditable(false);
		add(nextG, cc.xy(13, 15));

		//---- label12 ----
		label12.setText("Leiser:");
		label12.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label12, cc.xy(3, 17));

		//---- volDownL ----
		volDownL.setBackground(Color.white);
		volDownL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		volDownL.setEditable(false);
		add(volDownL, cc.xy(5, 17));

		//---- volDownG ----
		volDownG.setBackground(Color.white);
		volDownG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		volDownG.setEditable(false);
		add(volDownG, cc.xy(7, 17));

		//---- label5 ----
		label5.setText("Lauter:");
		label5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label5, cc.xy(9, 17));

		//---- volUpL ----
		volUpL.setBackground(Color.white);
		volUpL.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		volUpL.setEditable(false);
		add(volUpL, cc.xy(11, 17));

		//---- volUpG ----
		volUpG.setBackground(Color.white);
		volUpG.setToolTipText("Einfach anklicken und gew\u00fcnschte Tastenkombi dr\u00fccken!");
		volUpG.setEditable(false);
		add(volUpG, cc.xy(13, 17));

		//---- clearLocal ----
		clearLocal.setText("<html><p style=\\\"text-align:center\\\"><b>Alle lokalen</font></b><br>HotKeys l\u00f6schen!</p></html>");
		add(clearLocal, cc.xy(11, 19));

		//---- clearGlobal ----
		clearGlobal.setText("<html><p style=\\\"text-align:center\\\"><b>Alle globalen</font></b><br>HotKeys l\u00f6schen!</p></html>");
		add(clearGlobal, cc.xy(13, 19));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sam Meier
	private JLabel label1;
	private JLabel label2;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label6;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label3;
	private JTextField playL;
	private JTextField playG;
	private JLabel label10;
	private JTextField stopL;
	private JTextField stopG;
	private JLabel label11;
	private JTextField prevL;
	private JTextField prevG;
	private JLabel label4;
	private JTextField nextL;
	private JTextField nextG;
	private JLabel label12;
	private JTextField volDownL;
	private JTextField volDownG;
	private JLabel label5;
	private JTextField volUpL;
	private JTextField volUpG;
	private JButton clearLocal;
	private JButton clearGlobal;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
