package gui;
import gui.dnd.ListDropMode;
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
import players.PlayStateAdapter;
import lists.ListException;
import lists.ListProvider;
import common.Track;
import data.IData;
import data.SettingException;
import basics.Controller;

/**
 * Testfenster zum Testen neuer Funktionen.
 * 
 * @author Eraser
 */
public class TestWindow extends JFrame 
{
	private static final long serialVersionUID = -3880026026104218593L;

	private Controller controller = Controller.getInstance();
	private final ListProvider listProvider = controller.getListProvider();
	IData data = controller.getData();
	
	JScrollPane masterListPane;
	JList masterList;
	JScrollPane testListPane;
	JList testList;
	JTextField text = new JTextField();
	
	
	public TestWindow() throws HeadlessException
	{
		super("PartyDJ");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		try
		{
			masterList = new PDJList(listProvider.getMasterList(), ListDropMode.DELETE, "");
			testList = new PDJList(listProvider.getDbList("Test"));
		}
		catch (ListException e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler siehe Konsole!", "PartyDJ", JOptionPane.ERROR_MESSAGE);
		}
		
		masterListPane = new JScrollPane(masterList);
		testListPane = new JScrollPane(testList);
		
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
	        @Override public void componentResized(ComponentEvent evt) 
	        {
	            resize();
	        }});

		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(masterListPane, BorderLayout.CENTER);
		getContentPane().add(testListPane, BorderLayout.EAST);
				
		try
		{
			text.setText(controller.getData().readSetting("Test", "Default"));
		}
		catch (SettingException e)
		{
			e.printStackTrace();
		}
		getContentPane().add(text, BorderLayout.NORTH);
		
		masterList.setCellRenderer(new TrackRenderer());
		testList.setCellRenderer(new TrackRenderer());
		
		text.addActionListener(new TextBoxListener());

		controller.getPlayer().addPlayStateListener(new PlayStateAdapter(){
			@Override public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
			{
				setTitle(playingCurrent.getName());			
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
				controller.getData().writeSetting("Test", text.getText());
			}
			catch (SettingException e)
			{
				e.printStackTrace();
			}
		}
	}
}
