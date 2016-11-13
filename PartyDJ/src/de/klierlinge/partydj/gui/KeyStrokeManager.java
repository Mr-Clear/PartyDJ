package de.klierlinge.partydj.gui;

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
import com.melloware.jintellitype.JIntellitype;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.players.IPlayer;

/**KeyStrokeManager kümmert sich um globale und VM spezifische KeyEvents, unabhängig von Komponenten.
 * 
 * @author Sam
 * @date   16.05.09
 */
public class KeyStrokeManager extends EventQueue
{
	protected static final KeyStrokeManager INSTANCE = new KeyStrokeManager();
	protected final InputMap keys = new InputMap();
	protected final ActionMap actions = new ActionMap();
	protected final HashMap<Integer, String> regKeys = new HashMap<>();
	protected final IData data = Controller.getInstance().getData();
	protected StringBuilder local = new StringBuilder();

	static
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(INSTANCE);
	}
	
	public void initHotKeys()
	{
		if("AMD64".equalsIgnoreCase(System.getenv("PROCESSOR_ARCHITECTURE")))
			JIntellitype.setLibraryLocation("Resources\\JIntellitype64.dll");
		else
			JIntellitype.setLibraryLocation("Resources\\JIntellitype.dll");
		
		JIntellitype.getInstance().addHotKeyListener(GlobalHotKeys.getInstance());
		JIntellitype.getInstance().addIntellitypeListener(GlobalHotKeys.getInstance());
		
		String raw = data.readSetting("GlobalHotKeys");
		if(raw != null && raw.length() > 1)
		{
			raw = raw.substring(1);
			final String[] rawKeys = raw.split("§");
			for(final String k : rawKeys)
			{
				final String[] key = k.split("@");
				enableGlobalHotKey(Integer.valueOf(key[0]), Integer.valueOf(key[1]), key[2], false);
			}
		}
		raw = data.readSetting("LocalHotKeys", "§0@102@PLAY_PAUSE§0@100@PREVIOUS§0@98@VOLUME_DOWN§0@104@VOLUME_UP§0@102@NEXT§0@101@PLAY_PAUSE§0@96@STOP");
		if(raw != null && raw.length() > 1)
		{
			//Damit im Array erstes Element nicht leer. (Löscht nur das $)
			raw = raw.substring(1);
			final String[] rawKeys = raw.split("§");
			for(final String k : rawKeys)
			{
				final String[] key = k.split("@");
				enableLocalHotKey(Integer.valueOf(key[0]), Integer.valueOf(key[1]), key[2], false);
			}
		}
	}
	
	/**
	 * @return Gibt eine KeyStrokeManager Instanz zurück.
	 */
	public static KeyStrokeManager getInstance() 
	{
		return INSTANCE;
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
	protected void dispatchEvent(final AWTEvent event)
	{
		super.dispatchEvent(event);
		
		if(event instanceof KeyEvent)
		{
			if(event.getSource() instanceof JTextField)
				return;
				
			final KeyStroke key = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
			if(key.getKeyCode() == 0)
				return;
			
			final Action action = actions.get(keys.get(key));
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
	public static int getRawCode(final KeyEvent ke)
	{
		final String s = ke.toString();
		final int p = s.indexOf("rawCode=") + 8;
		return Integer.parseInt(s.substring(p, s.indexOf(",", p)));
	}
	
	/**Setzt einen globalen HotKey
	 * 
	 * @param modifier	Wie z.B. alt, strg oder Windows-Taste abzurufen unter JIntellitype.MOD_(was auch immer)
	 * @param keyCode	Code der Taste
	 * @param command	Aktion die bei dieser Taste ausgeführt werden soll.
	 */
	public synchronized void enableGlobalHotKey(final int modifier, final int keyCode, final String command)
	{
		enableGlobalHotKey(modifier, keyCode, command, true);
	}
	
	public synchronized void enableGlobalHotKey(final int modifier, final int keyCode, final String command, final boolean save)
	{
		JIntellitype.getInstance().registerHotKey(command.hashCode(), modifier, keyCode);
		GlobalHotKeys.setKeyAction(command.hashCode(), command);
		regKeys.put(command.hashCode(), modifier + "@" + keyCode + "@" + command);
		
		if(save)
		{

			final StringBuilder global = new StringBuilder();
			global.append(data.readSetting("GlobalHotKeys", ""));
			global.append("§" + modifier + "@" + keyCode + "@" + command);
			data.writeSetting("GlobalHotKeys", global.toString());
		}
	}
	
	public synchronized void enableLocalHotKey(final int modifier, final int keyCode, final String command)
	{
		enableLocalHotKey(modifier, keyCode, command, true);
	}
	
	public synchronized void enableLocalHotKey(final int modifier, final int keyCode, final String command, final boolean save)
	{
		final KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifier);

		keys.put(keyStroke, command);
		actions.put(command, new AbstractAction()
		{
			private static final long serialVersionUID = 8899350838466037814L;
			@Override public void actionPerformed(final ActionEvent e)
			{
				final IPlayer p = Controller.getInstance().getPlayer();
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
			}
		});
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
	public synchronized void disableGlobalHotKey(final int id)
	{
		JIntellitype.getInstance().unregisterHotKey(id);
		
		regKeys.remove(id);
		
		final StringBuilder global = new StringBuilder();
		for(final String s : regKeys.values())
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
		return new HashSet<>(regKeys.values());
	}

	public void disableLocalHotKeys()
	{
		keys.clear();

		if(regKeys.values().size() == 0)
			local = new StringBuilder();
		for(final String s : regKeys.values())
		{
			local.append("$" + s);
		}
		data.writeSetting("LocalHotKeys", local.toString());
	}
}