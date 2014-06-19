package network.remote;

import basics.Controller;
import data.IData;
import lists.ListException;
import lists.data.ListProvider;
import gui.SplashWindow;
import players.IPlayer;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Controller der sich über Netzwerk mit RemoteServer verbindet.
 * Bietet keine Listen an.
 * 
 * @author Eraser
 *
 */
public class LightNetworkRemote extends Controller implements AnswerListener
{
	protected NetworkPlayer player;
	protected NetworkData data;
	protected Socket server;

	protected final Map<Long, Thread> invocationThreads = Collections.synchronizedMap(new HashMap<Long, Thread>());
	protected final Map<Long, Serializable> invocationAnswers = Collections.synchronizedMap(new HashMap<Long, Serializable>());

	public LightNetworkRemote(String[] args)
	{
		super(args);
		
		try
		{
			server = new Socket("merkur", RemoteServer.PORT);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
			return;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}

		ObjectOutputStream oos;
		try
		{
			oos = new ObjectOutputStream(server.getOutputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		data = new NetworkData(oos, invocationThreads, invocationAnswers);
		player = new NetworkPlayer(oos, invocationThreads, invocationAnswers);
		new ServerListener(server, data, player, this);
		
		player.updatePlayState();
		
		/* Splash Window laden. */
		final SplashWindow splash = new SplashWindow();
		
		/* closeListenThread starten um Schließen-Ereignisse abzufangen. */
		closeListenThread = new Thread()
		{
			@Override public void run()
			{
				closePartyDJ();
			}
		};
		Runtime.getRuntime().addShutdownHook(closeListenThread);
		
		loadLookAndFeel(splash);
		
		/* Parameter auswerten. */
		final List<String> windowsToLoad = new ArrayList<>();
		
		int lastParam = 0;
		for(final String arg : args)
		{
			final String argl = arg.toLowerCase();
			if(arg.charAt(0) == '-' || arg.charAt(0) == '+')
			{
				if("+window".equals(argl))
					lastParam = 3;
				else
					lastParam = 0;
			}
			else
			{
				switch(lastParam)
				{
				case 3:	//+window
					windowsToLoad.add(arg);
					break;
				}
				lastParam = 0;
			}
		}
					
		//loadLists(splash);

		loadWindows(splash, windowsToLoad);

		
		splash.setOpacity(1f);
		
		splash.setInfo("Lade Plugins");
		{
			try
			{
				//new network.ClassicUdpReceiver().start();
				//new network.WinLircReceiver().initialise();
				//new RemoteServer().initialise();
			}
			catch(final Throwable t)
			{
				logError(IMPORTANT_ERROR, this, t, "Plugin laden fehlgeschlagen.");
			}
		}
		
		splash.close();

		splash.setInfo("PartyDJ bereit :)");
		
		loadFinished = true;
	}
	
	/** Lädt SystemLookAndFeel. */
	private void loadLookAndFeel(final SplashWindow splash)
	{
		splash.setInfo("Lade Look And Feel");
		
		//UI
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final ClassNotFoundException e)
		{
			logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
		}
		catch (final InstantiationException e)
		{
			logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
		}
		catch (final IllegalAccessException e)
		{
			logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
		}
		catch (final UnsupportedLookAndFeelException e)
		{
			logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
		}
	}
	
	@SuppressWarnings("unused")
	private void loadLists(final SplashWindow splash)
	{
		splash.setOpacity(0.9f);
		splash.setInfo("Lade Listen");

		//TODO Gepufferte Listen.
		try
		{
			listProvider = new ListProvider();
			playList = listProvider.getDbList("Wunschliste");				
			lastPlayedList = listProvider.getDbList("LastPlayed");
		}
		catch (final ListException e)
		{
			logError(FATAL_ERROR, this, e, "Listen konnten nicht geladen werden!");
		}
	}
	
	private void loadWindows(final SplashWindow splash, final List<String> windowsToLoad)
	{
		splash.setOpacity(0.95f);
		splash.setInfo("Lade Fenster");
		{
			if(windowsToLoad.size() == 0)
				windowsToLoad.add("gui.ClassicWindow");
			
			int loaded = 0;
			
			for(final String window : windowsToLoad)
				if(loadWindow(window))
					loaded++;
			
			if(loaded == 0)
				logError(FATAL_ERROR, this, null, "Konnte kein Fenster laden. PartyDJ wird beendet.");
		}
	}

	@Override
	public void closePartyDJ()
	{
		try
		{
			server.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		super.closePartyDJ();
	}

	@Override
	public IData getData()
	{
		return data;
	}

	@Override
	public IPlayer getPlayer()
	{
		return player;
	}

	@Override
	public void answerArrived(final Answer answer)
	{
		invocationAnswers.put(answer.getInvocationId(), answer.getData());

		final Thread invocationThread;
		invocationThread = invocationThreads.remove(answer.getInvocationId());
		
		if(invocationThread != null)
			invocationThread.interrupt();
	}
}
