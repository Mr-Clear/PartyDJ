package gui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import com.melloware.jintellitype.JIntellitype;

/**KeyStrokeManager kümmert sich um globale und VM spezifische KeyEvents, unabhängig von Komponenten.
 * 
 * @author Sam
 * @date   16.05.09
 */
public class KeyStrokeManager extends EventQueue
{
	private static final KeyStrokeManager instance = new KeyStrokeManager();
	private final InputMap keys = new InputMap();
	private final ActionMap actions = new ActionMap();
	private List<Integer> regKeys = new ArrayList<Integer>();

	static
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
	}
	
	public void initHotKeys()
	{
		JIntellitype.getInstance().addHotKeyListener(GlobalHotKeys.getInstance());
		JIntellitype.getInstance().addIntellitypeListener(GlobalHotKeys.getInstance());
	}
	
	/**
	 * @return Gibt eine KeyStrokeManager Instanz zurück.
	 */
	public static KeyStrokeManager getInstance() 
	{
		return instance;
	}

	/**Für VM-weite KeyStrokes
	 * @return Die verwendete InputMap
	 */
	public InputMap getInputMap()
	{
		return keys;
	}
	
	/**Für VM-weite KeyStrokes
	 * @return Die ActionMap mit den in der InputMap gesetzten KeyStrokes
	 */
	public ActionMap getActionMap()
	{
		return actions;
	}
	
	@Override
	protected void dispatchEvent(AWTEvent event)
	{
		super.dispatchEvent(event);
		if(event instanceof KeyEvent)
		{
			if(event.getSource() instanceof JTextField)
			{
				super.dispatchEvent(event);
				return;
			}
				
			KeyStroke key = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
			if(key.getKeyCode() == 0)
				return;
			
			Action action = actions.get(keys.get(key));
			if(action != null && action.isEnabled())
			{
				if(keys.get(key) instanceof String)
					action.actionPerformed(new ActionEvent(event.getSource(), event.getID(), (String) keys.get(key), ((KeyEvent)event).getModifiers()));
			}
		} 
	}
	
	/**Gibt den RawCode eines KeyEvents zurück
	 * 
	 * @param ke KeyEvent, von dem der RawCode ausgegeben werden soll
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getRawCode(KeyEvent ke)
	{
		String s = ke.toString();
		int p = s.indexOf("rawCode=") + 8;
		return Integer.parseInt(s.substring(p, s.indexOf(",", p)));
	}
	
	/**Setzt einen globalen HotKey
	 * 
	 * @param modifier	Wie z.B. alt, strg oder Windows-Taste abzurufen unter JIntellitype.MOD_(was auch immer)
	 * @param keyCode	Code der Taste
	 */
	public synchronized void enableHotKey(int modifier, int keyCode)
	{
		int id = (String.valueOf(keyCode) + (char)0 + modifier).hashCode();

		JIntellitype.getInstance().registerHotKey(id, modifier, keyCode);
		regKeys.add(id);
	}
	
	/**Löscht einen globalen HotKey
	 * 
	 * @param id	ID des HotKeys, der gelöscht werden soll
	 */
	public synchronized void disableHotKey(int id)
	{
		JIntellitype.getInstance().unregisterHotKey(id);
		regKeys.remove((Integer)id);
	}

	/**
	 * @return	Liste aller global gesetzten HotKeys
	 */
	public List<Integer> getHotKeys()
	{
		return new ArrayList<Integer>(regKeys);
	}
}
