package gui.settings;

import basics.Controller;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

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
	
	public SettingWindow()
	{
		super("Party DJ Einstellungen");
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override public void run()
			{
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setSize(1000, 600);
				setIconImage(Toolkit.getDefaultToolkit().createImage("Resources/Settings32.gif"));
				
				tree.addMouseListener(new TreeListener());
				tree.setMinimumSize(new Dimension(200, tree.getMinimumSize().height));
				
				panel.setLayout(new BorderLayout());
				
				splitPane.add(tree);
				splitPane.add(panel);
				SettingWindow.this.add(splitPane);
				
				tree.setSelectionRow(0);
				
				new TreeListener().mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, 0, false));
		
				setVisible(true);
			}
		});
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(final WindowEvent e)
			{
				panel.setSettingComponent(null);
			}
		});
	}

	class TreeListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(final MouseEvent e)
		{
			if(SwingUtilities.isEventDispatchThread())
			{
				final TreePath path = tree.getSelectionPath();
				final Object o = path.getLastPathComponent();
				if(o instanceof SettingNode)
				{
					final SettingNode node = (SettingNode)o;
					final Class<? extends Component> compClass = node.getComponent();
					
					try
					{
						panel.setSettingComponent(compClass.getConstructor(JFrame.class).newInstance(SettingWindow.this));
					}
					catch (final Exception e1)
					{
						try
						{
							panel.setSettingComponent(compClass.getConstructor().newInstance());
						}
						catch (final Exception e2)
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
					SwingUtilities.invokeAndWait(new Runnable()
					{
						@Override public void run()
						{
							final TreePath path = tree.getSelectionPath();
							final Object o = path.getLastPathComponent();
							if(o instanceof SettingNode)
							{
								final SettingNode node = (SettingNode)o;
								final Class<? extends Component> compClass = node.getComponent();
								
								try
								{
									panel.setSettingComponent(compClass.getConstructor(Frame.class).newInstance(SettingWindow.this));
								}
								catch (final Exception e1)
								{
									try
									{
										panel.setSettingComponent(compClass.getConstructor().newInstance());
									}
									catch (final Exception e2)
									{
										Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e1, "Kann Einstellung nicht öffnen.");
									}
								}				
							}
						}
					});
				}
				catch (final InterruptedException e1)
				{
					Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e1, "Kann Einstellung nicht öffnen.");
				}
				catch (final InvocationTargetException e1)
				{
					Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e1, "Kann Einstellung nicht öffnen.");
				}
		}		
	}
}
