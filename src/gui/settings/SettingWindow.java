package gui.settings;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import basics.Controller;

public class SettingWindow extends JFrame
{
	private static final long serialVersionUID = -6606890610202063266L;
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private final JTree tree = new JTree(Controller.getInstance().getSetingTree());
	private final SettingContainer panel = new SettingContainer();
	private final Frame me = this;
	
	public SettingWindow()
	{
		super("Party DJ Einstellungen");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/Settings32.gif"));
		
		tree.addMouseListener(new TreeListener());
		tree.setMinimumSize(new Dimension(200, tree.getMinimumSize().height));
		
		panel.setLayout(new BorderLayout());
		
		splitPane.add(tree);
		splitPane.add(panel);
		this.add(splitPane);
		
		tree.setSelectionRow(0);
		
		new TreeListener().mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, 0, false));

		setVisible(true);
	}
	
	class TreeListener extends MouseAdapter
	{
		TreePath lastPath;
		public void mouseClicked(MouseEvent e)
		{
			
			TreePath path = tree.getSelectionPath();
			Object o = path.getLastPathComponent();
			if(o instanceof SettingNode)
			{
				SettingNode node = (SettingNode)o;
				Class<? extends Component> compClass = node.getComponent();
				
				try
				{
					panel.setSettingComponent(compClass.getConstructor(Frame.class).newInstance(me));
				}
				catch (Exception e1)
				{
					try
					{
						panel.setSettingComponent(compClass.getConstructor().newInstance());
					}
					catch (Exception e2)
					{
						throw new RuntimeException("Kein Kostruktor für Eintrag gefunden.");
					}
				}				
			}
		}		
	}
}
