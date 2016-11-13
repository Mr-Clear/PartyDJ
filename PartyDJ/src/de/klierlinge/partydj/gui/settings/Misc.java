package de.klierlinge.partydj.gui.settings;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.ClassicWindow;


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
public class Misc extends javax.swing.JPanel 
{
	private static final long serialVersionUID = 619371464866048141L;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JCheckBox tooltipCheckB;
	private JCheckBox sysTrayCheckB;
	private JLabel jLabel7;
	private JCheckBox masterlistCheckB;
	private JCheckBox playlistCheckB;
	private JLabel jLabel6;
	private JRadioButton positionRadioB;
	private JRadioButton startRadioB;
	private JRadioButton noRadioB;
	private JRadioButton yesRadioB;
	protected IData data = Controller.getInstance().getData();
	
	public Misc()
	{
		super();
		initGUI();
		initFuntions();
	}
	
	protected void initFuntions()
	{
		final ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(yesRadioB);
		buttonGroup2.add(noRadioB);

		final ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(startRadioB);
		buttonGroup1.add(positionRadioB);
		
		final boolean autoPlay = Boolean.parseBoolean(data.readSetting("AUTO_PLAY", "true"));
		if(autoPlay)
			yesRadioB.setSelected(true);
		else
			noRadioB.setSelected(true);
		
		final boolean fromStart = data.readSetting("POSITION", "-1").equalsIgnoreCase("-1");
		if(fromStart)
			positionRadioB.setSelected(true);
		else
			startRadioB.setSelected(true);
		
		final boolean isPlaylistEnabled = Boolean.parseBoolean(data.readSetting("PLAYLIST", "true"));
		playlistCheckB.setSelected(isPlaylistEnabled);

		final boolean isMainlistEnabled = Boolean.parseBoolean(data.readSetting("MASTERLIST", "true"));
		masterlistCheckB.setSelected(isMainlistEnabled);
		
		final boolean isSysTrayEnabled = Boolean.parseBoolean(data.readSetting("SYSTEM_TRAY", "true"));
		sysTrayCheckB.setSelected(isSysTrayEnabled);
		
		final boolean isTooltipEnabled = Boolean.parseBoolean(data.readSetting("TOOLTIP", "true"));
		tooltipCheckB.setSelected(isTooltipEnabled);
		
		yesRadioB.setName("AUTO_PLAY");
		yesRadioB.setActionCommand("true");
		noRadioB.setName("AUTO_PLAY");
		noRadioB.setActionCommand("false");
		positionRadioB.setName("POSITION");
		positionRadioB.setActionCommand("-1");
		startRadioB.setName("POSITION");
		startRadioB.setActionCommand("0");
		playlistCheckB.setName("PLAYLIST");
		playlistCheckB.setActionCommand("GUI");
		masterlistCheckB.setName("MASTERLIST");
		masterlistCheckB.setActionCommand("GUI");
		tooltipCheckB.setName("TOOLTIP");
		tooltipCheckB.setActionCommand("TRAY");
		sysTrayCheckB.setName("SYSTEM_TRAY");
		sysTrayCheckB.setActionCommand("TRAY");
		
		final ButtonListener bl = new ButtonListener(null);
		yesRadioB.addActionListener(bl);
		noRadioB.addActionListener(bl);
		positionRadioB.addActionListener(bl);
		startRadioB.addActionListener(bl);
		sysTrayCheckB.addItemListener(bl);
		tooltipCheckB.addItemListener(bl);
		playlistCheckB.addItemListener(new ButtonListener(masterlistCheckB));
		masterlistCheckB.addItemListener(new ButtonListener(playlistCheckB));
	}
	
	private void initGUI() 
	{
		try 
		{
			final GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.1};
			thisLayout.rowHeights = new int[] {49, 10, 10, 15, 10, 15, 15, 10, 15, 15, 10, 10, 20, 20};
			thisLayout.columnWeights = new double[] {0.0, 0.1, 0.1, 0.1};
			thisLayout.columnWidths = new int[] {144, 7, 7, 7};
			this.setLayout(thisLayout);
			setPreferredSize(new Dimension(400, 300));
			{
				jLabel1 = new JLabel();
				this.add(jLabel1, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 5, 3, 5), 0, 0));
				jLabel1.setText("Verschiedenes");
				jLabel1.setFont(new Font("Tahoma", Font.BOLD, 24));
				jLabel1.setPreferredSize(new java.awt.Dimension(75, 16));
			}
			{
				jLabel2 = new JLabel();
				jLabel2.setFont(new Font("Tahoma", Font.BOLD, 13));
				this.add(jLabel2, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 8, 3, 3), 0, 0));
				jLabel2.setText("AutoPlay Einstellungen");
			}
			{
				jLabel3 = new JLabel();
				jLabel3.setFont(new Font("Tahoma", Font.PLAIN, 12));
				this.add(jLabel3, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 10, 3, 3), 0, 0));
				jLabel3.setText("Soll nach dem Start die Wiedergabe automatisch gestartet werden?");
			}
			{
				yesRadioB = new JRadioButton();
				this.add(yesRadioB, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 10, 3, 3), 0, 0));
				yesRadioB.setText("Ja");
			}
			{
				noRadioB = new JRadioButton();
				this.add(noRadioB, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 5, 3, 3), 0, 0));
				noRadioB.setText("Nein");
			}
			{
				jLabel4 = new JLabel();
				this.add(jLabel4, new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 10, 3, 3), 0, 0));
				jLabel4.setText("Von welcher Position soll abgespielt werden?");
			}
			{
				startRadioB = new JRadioButton();
				this.add(startRadioB, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 10, 3, 3), 0, 0));
				startRadioB.setText("Von Anfang an");
			}
			{
				positionRadioB = new JRadioButton();
				this.add(positionRadioB, new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 5, 3, 3), 0, 0));
				positionRadioB.setText("Von der letzten Position");
			}
			{
				jLabel5 = new JLabel();
				this.add(jLabel5, new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 8, 3, 3), 0, 0));
				jLabel5.setText("Listen");
				jLabel5.setFont(new Font("Tahoma", Font.BOLD, 13));
			}
			{
				jLabel6 = new JLabel();
				this.add(jLabel6, new GridBagConstraints(0, 7, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 10, 3, 3), 0, 0));
				jLabel6.setText("Welche Listen sollen angezeigt werden?");
			}
			{
				playlistCheckB = new JCheckBox();
				this.add(playlistCheckB, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 10, 3, 3), 0, 0));
				playlistCheckB.setText("Playlist");
			}
			{
				masterlistCheckB = new JCheckBox();
				this.add(masterlistCheckB, new GridBagConstraints(1, 8, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 5, 3, 3), 0, 0));
				masterlistCheckB.setText("Hauptliste");
			}
			{
				jLabel7 = new JLabel();
				this.add(jLabel7, new GridBagConstraints(0, 9, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 8, 3, 3), 0, 0));
				jLabel7.setText("SystemTray");
				jLabel7.setFont(new Font("Tahoma", Font.BOLD, 13));
			}
			{
				sysTrayCheckB = new JCheckBox();
				this.add(sysTrayCheckB, new GridBagConstraints(0, 10, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 10, 3, 3), 0, 0));
				sysTrayCheckB.setText("PartyDJ in das SystemTray minimieren");
			}
			{
				tooltipCheckB = new JCheckBox();
				this.add(tooltipCheckB, new GridBagConstraints(0, 11, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 10, 3, 3), 0, 0));
				tooltipCheckB.setText("Tooltip anzeigen, wenn neues Lied gespielt wird");
			}
		} 
		catch (final Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	protected final class ButtonListener implements ActionListener, ItemListener
	{
		protected final JCheckBox partner;
		
		public ButtonListener(final JCheckBox partner)
		{
			this.partner = partner;
		}
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			if(e.getSource() instanceof JRadioButton)
			{
				final JRadioButton radio = (JRadioButton)e.getSource();
				data.writeSetting(radio.getName(), radio.getActionCommand());						
			}
		}

		@Override
		public void itemStateChanged(final ItemEvent e)
		{
			if(e.getSource() instanceof JCheckBox && partner != null && ((JCheckBox)e.getSource()).getActionCommand().equalsIgnoreCase("GUI"))
			{
				final JCheckBox cb = (JCheckBox) e.getSource();
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					data.writeSetting(cb.getName(), "true");	
					ClassicWindow.getInstance().restoreDefaultGUI();
				}
				if(e.getStateChange() == ItemEvent.DESELECTED)
				{
					data.writeSetting(cb.getName(), "false");	
					if(!partner.isSelected())
						partner.setSelected(true);
					ClassicWindow.getInstance().removeListFromGui(cb.getName(), false);
				}
			}
			
			if(e.getSource() instanceof JCheckBox && ((JCheckBox)e.getSource()).getActionCommand().equalsIgnoreCase("TRAY"))
			{
				final JCheckBox cb = (JCheckBox) e.getSource();
				data.writeSetting(cb.getName(), String.valueOf(cb.isSelected()));
			}
		}
	}
}