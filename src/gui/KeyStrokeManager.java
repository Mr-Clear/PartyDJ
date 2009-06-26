package gui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import players.IPlayer;
import basics.Controller;
import com.melloware.jintellitype.JIntellitype;
import data.IData;

/**KeyStrokeManager kümmert sich um globale und VM spezifische KeyEvents, unabhängig von Komponenten.
 * 
 * @author Sam
 * @date   16.05.09
 */
public class KeyStrokeManager extends EventQueue
{
	protected static final KeyStrokeManager instance = new KeyStrokeManager();
	protected final InputMap keys = new InputMap();
	protected final ActionMap actions = new ActionMap();
	protected HashMap<Integer, String> regKeys = new HashMap<Integer, String>();
	protected final IData data = Controller.getInstance().getData();
	protected StringBuilder global = new StringBuilder();
	protected StringBuilder local = new StringBuilder();

	static
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
	}
	
	public void initHotKeys()
	{
		JIntellitype.getInstance().addHotKeyListener(GlobalHotKeys.getInstance());
		JIntellitype.getInstance().addIntellitypeListener(GlobalHotKeys.getInstance());
		
		String raw = data.readSetting("GlobalHotKeys");

		if(raw != null && raw.length() > 1)
		{
			raw = raw.substring(1);
			String[] rawKeys = raw.split("§");
			for(String k : rawKeys)
			{
				String[] key = k.split("@");
				enableGlobalHotKey(Integer.valueOf(key[0]), Integer.valueOf(key[1]), key[2], false);
			}
		}
		raw = data.readSetting("LocalHotKeys");
		if(raw != null && raw.length() > 1)
		{
			raw = raw.substring(1);
			String[] rawKeys = raw.split("§");
			for(String k : rawKeys)
			{
				String[] key = k.split("@");
				enableLocalHotKey(Integer.valueOf(key[0]), Integer.valueOf(key[1]), key[2], false);
			}
		}
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
				return;
				
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
	
	/**Gibt den RawCode eines KeyEvents zurück.
	 * 
	 * @param ke KeyEvent, von dem der RawCode ausgegeben werden soll.
	 * @return RawCode des KeyEvents.
	 */
	public static int getRawCode(KeyEvent ke)
	{
		String s = ke.toString();
		int p = s.indexOf("rawCode=") + 8;
		return Integer.parseInt(s.substring(p, s.indexOf(",", p)));
	}
	
	/**Setzt einen globalen HotKey
	 * 
	 * @param modifier	Wie z.B. alt, strg oder Windows-Taste abzurufen unter JIntellitype.MOD_(was auch immer)
	 * @param keyCode	Code der Taste
	 * @param command	Aktion die bei dieser Taste ausgeführt werden soll.
	 */
	public synchronized void enableGlobalHotKey(int modifier, int keyCode, String command)
	{
		enableGlobalHotKey(modifier, keyCode, command, true);
	}
	
	public synchronized void enableGlobalHotKey(int modifier, int keyCode, String command, boolean save)
	{
		JIntellitype.getInstance().registerHotKey(command.hashCode(), modifier, keyCode);
		GlobalHotKeys.getInstance().setKeyAction(command.hashCode(), command);
		regKeys.put(command.hashCode(), modifier + "@" + keyCode + "@" + command);
		
		if(save)
		{
			global.append("§" + modifier + "@" + keyCode + "@" + command);
			data.writeSetting("GlobalHotKeys", global.toString());
		}
	}
	
	public synchronized void enableLocalHotKey(int modifier, int keyCode, String command)
	{
		enableLocalHotKey(modifier, keyCode, command, true);
	}
	
	public synchronized void enableLocalHotKey(int modifier, int keyCode, final String command, boolean save)
	{
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifier);

		keys.put(keyStroke, command);
		actions.put(command, new AbstractAction(){
			
								private static final long serialVersionUID = 8899350838466037814L;
								@Override
								public void actionPerformed(ActionEvent e)
								{
									IPlayer p = Controller.getInstance().getPlayer();
									if(command.equalsIgnoreCase("PLAY_PAUSE"))
										p.fadeInOut();
									else if(command.equalsIgnoreCase("STOP"))
										p.stop();
									else if(command.equalsIgnoreCase("VOLUME_UP"))
										p.setVolume(p.getVolume() + 10);
									else if(command.equalsIgnoreCase("VOLUME_DOWN"))
										p.setVolume(p.getVolume() - 10);
									else if(command.equalsIgnoreCase("NEXT"))
										p.playNext();
									else if(command.equalsIgnoreCase("PREVIOUS"))
										p.playPrevious();
								}});
		if(save)
		{
			local.append("§" + modifier + "@" + keyCode + "@" + command);
			data.writeSetting("LocalHotKeys", local.toString());
		}
	}
	
	/**Löscht einen globalen HotKey
	 * 
	 * @param id	ID des HotKeys, der gelöscht werden soll. ID ist der HashCode des commands.
	 */
	public synchronized void disableGlobalHotKey(int id)
	{
		JIntellitype.getInstance().unregisterHotKey(id);
		regKeys.remove(id);
		
		for(String s : regKeys.values())
		{
			global.append("$" + s);
		}
		data.writeSetting("GlobalHotKeys", global.toString());
	}

	/**
	 * @return	Liste der ID's aller global gesetzten HotKeys
	 */
	public Set<String> getGlobalHotKeys()
	{
		return new HashSet<String>(regKeys.values());
	}

	public void disableLocalHotKeys()
	{
		keys.clear();
		local = new StringBuilder();
		data.writeSetting("LocalHotKeys", local.toString());
	}
}