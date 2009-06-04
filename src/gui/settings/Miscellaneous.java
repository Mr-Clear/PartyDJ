/*
 * Created by JFormDesigner on Thu Jun 04 16:49:56 CEST 2009
 */

package gui.settings;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import basics.Controller;
import com.jgoodies.forms.layout.*;

/**
 * @author Sam Meier
 */
public class Miscellaneous extends JPanel 
{
	private static final long serialVersionUID = -2425119706351835618L;
	
	public Miscellaneous() 
	{
		initComponents();
		initFuntions();
	}

	protected void initFuntions()
	{
		boolean autoPlay = Boolean.parseBoolean(Controller.getInstance().getData().readSetting("AUTO_PLAY", "true"));
		if(autoPlay)
			yesRadioB.setSelected(true);
		else
			noRadioB.setSelected(true);
		
		boolean fromStart = Controller.getInstance().getData().readSetting("POSITION", "-1").equalsIgnoreCase("-1");
		if(fromStart)
			postionRadioB.setSelected(true);
		else
			startRadioB.setSelected(true);
		
		yesRadioB.setName("AUTO_PLAY");
		yesRadioB.setActionCommand("true");
		noRadioB.setName("AUTO_PLAY");
		noRadioB.setActionCommand("false");
		postionRadioB.setName("POSITION");
		postionRadioB.setActionCommand("-1");
		startRadioB.setName("POSITION");
		startRadioB.setActionCommand("0");
		
		RadioButtonListener rbl = new RadioButtonListener();
		yesRadioB.addActionListener(rbl);
		noRadioB.addActionListener(rbl);
		postionRadioB.addActionListener(rbl);
		startRadioB.addActionListener(rbl);
	}
	
	protected class RadioButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JRadioButton radio = (JRadioButton)e.getSource();
			if(e.getSource() instanceof JRadioButton)
			{
				Controller.getInstance().getData().writeSetting(radio.getName(), radio.getActionCommand());
			}
		}
	}

	protected void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sam Meier
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		yesRadioB = new JRadioButton();
		noRadioB = new JRadioButton();
		label4 = new JLabel();
		startRadioB = new JRadioButton();
		postionRadioB = new JRadioButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setLayout(new FormLayout(
			"1dlu, $lcgap, 37dlu, $lcgap, 47dlu, $lcgap, 40dlu, $lcgap, 48dlu, $lcgap, 54dlu:grow",
			"1dlu, $lgap, 24dlu, $pgap, 13dlu, $lgap, 11dlu, $lgap, 20dlu, $lgap, 13dlu, $lgap, 15dlu"));
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{3, 5, 7, 9}});
		((FormLayout)getLayout()).setRowGroups(new int[][] {{7, 11}, {9, 13}});

		//---- label1 ----
		label1.setText("Verschiedenes");
		label1.setFont(new Font("Tahoma", Font.BOLD, 24));
		add(label1, cc.xywh(3, 3, 9, 1));

		//---- label2 ----
		label2.setText("AutoPlay Einstellungen");
		label2.setFont(new Font("Tahoma", Font.BOLD, 13));
		add(label2, cc.xywh(3, 5, 9, 1));

		//---- label3 ----
		label3.setText("Soll nach dem Start die Wiedergabe automatisch gestartet werden?");
		label3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label3, cc.xywh(3, 7, 9, 1));

		//---- yesRadioB ----
		yesRadioB.setText("Ja");
		add(yesRadioB, cc.xy(3, 9));

		//---- noRadioB ----
		noRadioB.setText("Nein");
		add(noRadioB, cc.xy(7, 9));

		//---- label4 ----
		label4.setText("Von wo ab soll beim Start gespielt werden?");
		label4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(label4, cc.xywh(3, 11, 9, 1));

		//---- startRadioB ----
		startRadioB.setText("Von Anfang an");
		add(startRadioB, cc.xywh(3, 13, 3, 1));

		//---- postionRadioB ----
		postionRadioB.setText("Von der letzten Positon");
		add(postionRadioB, cc.xywh(7, 13, 3, 1));

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(yesRadioB);
		buttonGroup2.add(noRadioB);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(startRadioB);
		buttonGroup1.add(postionRadioB);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sam Meier
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JRadioButton yesRadioB;
	private JRadioButton noRadioB;
	private JLabel label4;
	private JRadioButton startRadioB;
	private JRadioButton postionRadioB;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
