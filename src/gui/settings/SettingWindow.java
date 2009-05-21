package gui.settings;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.tree.TreePath;
import basics.Controller;

/**
 * Zeigt die Einstellungen des PartyDJ an.
 * <p>Das Fenster wird geteit duch eine vertikales JSplitPane.
 * <p>Auf der linken Seite ist ein JTree mit Knoten vom Typ SettingNode.
 * <br>Die Knoten enthalten Verweise auf Komponenten die angezeigt werden,
 * wenn auf den Knoten geklickt wird.
 * <p>Auf der rechten Seite ist der SettingContainer, der die in den SettingNodes
 * angegebenen Komponenten anzeigt.  
 * 
 * @author Eraser
 * 
 * @see SettingNode
 * @see SettingContainer
 */
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
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setSize(1000, 600);
				setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/Settings32.gif"));
				
				tree.addMouseListener(new TreeListener());
				tree.setMinimumSize(new Dimension(200, tree.getMinimumSize().height));
				
				panel.setLayout(new BorderLayout());
				
				splitPane.add(tree);
				splitPane.add(panel);
				me.add(splitPane);
				
				tree.setSelectionRow(0);
				
				new TreeListener().mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, 0, false));
		
				setVisible(true);
			}});
	}
	
	class TreeListener extends MouseAdapter
	{
		TreePath lastPath;
		public void mouseClicked(MouseEvent e)
		{
			if(SwingUtilities.isEventDispatchThread())
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
							e2.printStackTrace();
							throw new RuntimeException("Kein Kostruktor für Eintrag gefunden.", e2);
						}
					}				
				}
			}
			else
				try
				{
					SwingUtilities.invokeAndWait(new Runnable(){
						@Override
						public void run()
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
										e2.printStackTrace();
										throw new RuntimeException("Kein Kostruktor für Eintrag gefunden.", e2);
									}
								}				
							}
						}});
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (InvocationTargetException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}		
	}
}
