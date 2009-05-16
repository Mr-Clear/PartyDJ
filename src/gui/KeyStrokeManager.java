package gui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import com.melloware.jintellitype.JIntellitype;

public class KeyStrokeManager extends EventQueue
{
	private static final KeyStrokeManager instance = new KeyStrokeManager();
	private final InputMap keys = new InputMap();
	private final ActionMap actions = new ActionMap();

	static
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
	}
	
	public static KeyStrokeManager getInstance() 
	{
		return instance;
	}

	public InputMap getInputMap()
	{
		return keys;
	}
	
	public ActionMap getActionMap()
	{
		return actions;
	}
	
	@Override
	protected void dispatchEvent(AWTEvent event)
	{
		if(event instanceof KeyEvent)
		{
			KeyStroke key = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
			System.out.println(key.getKeyCode());
			Action action = actions.get(keys.get(key));
			if(action != null && action.isEnabled())
			{
				if(keys.get(key) instanceof String)
					action.actionPerformed(new ActionEvent(event.getSource(), event.getID(), (String) keys.get(key), ((KeyEvent)event).getModifiers()));
			}
		}  
		super.dispatchEvent(event);
	}
	
	private int getRawCode(KeyEvent ke)
	{
		return 0;
	}
	
	public boolean enableHotKeys()
	{
		
		return true;
	}
	
	public boolean disableHotKeys()
	{
		return false;
	}

}
