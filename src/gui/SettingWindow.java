package gui;

import java.awt.event.*;
import javax.swing.*;

public class SettingWindow extends JFrame
{
	private static final long serialVersionUID = -6606890610202063266L;
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	JTree tree = new JTree();
	JPanel panel = new JPanel();
	
	public SettingWindow()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 300);
		
		
		tree.addMouseListener(new TreeListener());

		splitPane.add(tree);
		splitPane.add(panel);
		this.add(splitPane);
		//new JLabel(new ImageIcon("cow.jpg")), new JLabel(new ImageIcon("astronaut.jpg")));
		//splitPane.setOneTouchExpandable(true);
		
		setVisible(true);
	}
	
	class TreeListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			panel.add(new JLabel(tree.getSelectionPath().toString()));			
		}		
	}
}
