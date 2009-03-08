package gui.settings;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class SettingWindow extends JFrame
{
	private static final long serialVersionUID = -6606890610202063266L;
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private final JTree tree;
	private final SettingContainer panel = new SettingContainer();
	
	public SettingWindow()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 300);
		
		tree = new JTree(createTree());
		tree.addMouseListener(new TreeListener());
		tree.setMinimumSize(new Dimension(200, tree.getMinimumSize().height));
		
		panel.setLayout(new BorderLayout());
		
		splitPane.add(tree);
		splitPane.add(panel);
		this.add(splitPane);
		//new JLabel(new ImageIcon("cow.jpg")), new JLabel(new ImageIcon("astronaut.jpg")));
		//splitPane.setOneTouchExpandable(true);
		
		setVisible(true);
	}
	
	private TreeNode createTree()
	{
		DefaultMutableTreeNode  root = new DefaultMutableTreeNode("Einstellungen");
		
		root.add(new DefaultMutableTreeNode("Hauptliste"));
		
		return root;
	}
	
	class TreeListener extends MouseAdapter
	{
		TreePath lastPath;
		public void mouseClicked(MouseEvent e)
		{
			TreePath path = tree.getSelectionPath();
			//String path = tree.getSelectionPath().toString();
			
			if(path != null && !path.equals(lastPath))
			{
				lastPath = path;
				
				System.out.println(path);
				
				if(path.getPathCount() >= 1)
				{
					String root = path.getPathComponent(0).toString();
					
					if(root.equals("Einstellungen"))
					{
						if(path.getPathCount() >= 2)
						{
							String second = path.getPathComponent(1).toString();
							
							if(second.equals("Hauptliste"))
								panel.setSettingComponent(new MasterList());
							else
								panel.setSettingComponent(new JPanel());
							//panel.add(new JLabel());
						}
						else
						{
							panel.setSettingComponent(new About());
						}
					}
				}
			}
		}		
	}
}
