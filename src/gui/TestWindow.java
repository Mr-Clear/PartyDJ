package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.*;
import lists.EditableListModel;
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
			masterList = new JList(Controller.instance.listProvider.getMasterList());
			testList = new JList(Controller.instance.listProvider.getDbList("Test"));
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
		masterList.addKeyListener(new MasterListModelListener());
		masterList.addMouseListener(new MasterListModelListener());
		testList.addKeyListener(new ClientListModelListener());
		/*masterList.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent e)
					{
						if (e.getSource() instanceof JList)
						{
							JList list = (JList)e.getSource();
							Track selected = (Track)list.getSelectedValue();
														
							try
							{
								if(e.getButton() == 1)
									((EditableListModel)testList.getModel()).add(selected);
								else if(e.getButton() == 3)
									((EditableListModel)testList.getModel()).add(Integer.parseInt(text.getText()), selected);
							}
							catch (ListException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});*/
		
		Controller.instance.addPlayStateListener(new PlayStateListener(){

			public void currentTrackChanged(Track playedLast, Track playingCurrent)
			{
				setTitle(playingCurrent.name);			
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
			int textInt = Integer.parseInt(text.getText());
			Controller.instance.playTrack(Controller.instance.listProvider.masterList.get(textInt));

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
	
	private class MasterListModelListener implements KeyListener, MouseListener
	{
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode())
			{
			case 10:	// RETURN
				if (e.getSource() instanceof JList)
				{
					Track selected = (Track)masterList.getSelectedValue();
												
					try
					{
						((EditableListModel)testList.getModel()).add(selected);
					}
					catch (ListException e1)
					{
						e1.printStackTrace();
					}
				}
			default:
				//System.out.println("[" + e.getKeyCode() + "]");
			}
		}
	
		public void keyReleased(KeyEvent e)
		{
		}
	
		public void keyTyped(KeyEvent e)
		{		
		}

		public void mouseClicked(MouseEvent e)
		{
			if(SwingUtilities.isRightMouseButton(e))
			{
				synchronized(masterList)
				{
				    masterList.setSelectedIndex(e.getY() / masterList.getFixedCellHeight());
					
					JPopupMenu popup = new JPopupMenu();
					JMenuItem newItem = new JMenuItem(masterList.getSelectedValue().toString());
					newItem.setEnabled(false);
					popup.add(newItem);
					popup.addSeparator();
					newItem = new JMenuItem("Bearbeiten...");
					newItem.addActionListener(new EditListener((Track)masterList.getSelectedValue()));
					popup.add(newItem);
	
					popup.show(masterList, e.getX(), e.getY());
				}
			}
			

		}
		
		class EditListener implements ActionListener
		{
			private Track item;
			EditListener(Track item)
			{
				this.item = item;
			}
			public void actionPerformed(ActionEvent e)
			{
					new EditTrackWindow(item);			
			}				
		}

		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
		}

		public void mouseExited(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ClientListModelListener implements KeyListener
	{
	
		public void keyPressed(KeyEvent e)
		{
			JList list = (JList)e.getSource();
			switch(e.getKeyCode())
			{
			case 127:	// DEL
				try
				{
					((EditableListModel)list.getModel()).remove(list.getSelectedIndex());
				}
				catch (ListException e1)
				{
					e1.printStackTrace();
				}
				break;
			case 38: // UP
				try
				{
					((EditableListModel)list.getModel()).move(list.getSelectedIndex(), list.getSelectedIndex() - 1);
				}
				catch (ListException e1)
				{
					e1.printStackTrace();
				}
				break;
			case 40: // DOWN
				int listIndex = list.getSelectedIndex();
				try
				{
					((EditableListModel)list.getModel()).move(listIndex, listIndex + 1);
				}
				catch (ListException e1)
				{
					e1.printStackTrace();
				}
				list.setSelectedIndex(listIndex);	//Geht nur so, warum auch immer...
				break;
			default:
				//System.out.println("[" + e.getKeyCode() + "]");
			}
		}
	
		public void keyReleased(KeyEvent arg0)
		{
		}
	
		public void keyTyped(KeyEvent arg0)
		{
		}
	}
}
