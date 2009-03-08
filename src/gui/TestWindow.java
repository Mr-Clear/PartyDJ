package gui;
import gui.DnD.ListDropMode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.*;
import common.ListException;
import common.PlayStateListener;
import common.SettingException;
import common.Track;
import data.IData;
import basics.Controller;

public class TestWindow extends JFrame 
{
	private static final long serialVersionUID = -3880026026104218593L;

	JScrollPane masterListPane;
	JList masterList;
	JScrollPane testListPane;
	JList testList;
	JTextField text = new JTextField();
	
	IData data = Controller.instance.data;
	
	public TestWindow() throws HeadlessException
	{
		super("PartyDJ");
		try
		{
			masterList = new PDJList(Controller.instance.listProvider.getMasterList(), ListDropMode.DELETE, "");
			testList = new PDJList(Controller.instance.listProvider.getDbList("Test"));
		}
		catch (ListException e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler siehe Konsole!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
		}
		
		masterListPane = new JScrollPane(masterList);
		testListPane = new JScrollPane(testList);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			setSize(Integer.parseInt(data.readSetting("TestWindowWidth", "800")), Integer.parseInt(data.readSetting("TestWindowHeight", "500")));
		}
		catch (NumberFormatException e1)
		{
			e1.printStackTrace();
		}
		catch (SettingException e1)
		{
			e1.printStackTrace();
		}
		//setExtendedState(MAXIMIZED_BOTH);
		
		this.addWindowStateListener(new WindowStateListener(){
			public void windowStateChanged(WindowEvent evt)
	        {
	            resize();
	        }});
		
		getContentPane().addComponentListener(new ComponentAdapter(){  
	        public void componentResized(ComponentEvent evt) 
	        {
	            resize();
	        }});

		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(masterListPane, BorderLayout.CENTER);
		getContentPane().add(testListPane, BorderLayout.EAST);
				
		try
		{
			text.setText(Controller.instance.data.readSetting("Test", "Default"));
		}
		catch (SettingException e)
		{
			e.printStackTrace();
		}
		getContentPane().add(text, BorderLayout.NORTH);
		
		masterList.setCellRenderer(new TrackRenderer());
		testList.setCellRenderer(new TrackRenderer());
		
		text.addActionListener(new TextBoxListener());

		Controller.instance.player.addPlayStateListener(new PlayStateListener(){
			public void currentTrackChanged(Track playedLast, Track playingCurrent)
			{
				setTitle(playingCurrent.name);			
			}

			public void playStateChanged(boolean playState)
			{
				// TODO Auto-generated method stub
				
			}});
				
		setVisible(true);
		resize();
		
	}
	
	private void resize()
	{
        testListPane.setPreferredSize(new Dimension(getSize().width / 2, testListPane.getPreferredSize().height));
        try
		{
			data.writeSetting("TestWindowWidth", Integer.toString(getSize().width));
			data.writeSetting("TestWindowHeight", Integer.toString(getSize().height));
		}
		catch (SettingException e)
		{
			e.printStackTrace();
		}
        
	}



	class TextBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			//int textInt = Integer.parseInt(text.getText());
			//Controller.instance.playTrack(Controller.instance.listProvider.masterList.get(textInt));

			try
			{
				Controller.instance.data.writeSetting("Test", text.getText());
			}
			catch (SettingException e)
			{
				e.printStackTrace();
			}
		}
	}
}
