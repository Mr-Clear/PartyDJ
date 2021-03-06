package de.klierlinge.partydj.basics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.OpenDbException;
import de.klierlinge.partydj.data.SettingException;
import de.klierlinge.partydj.data.derby.DerbyDB;
import de.klierlinge.partydj.gui.KeyStrokeManager;
import de.klierlinge.partydj.gui.SplashWindow;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.lists.data.ListProvider;
import de.klierlinge.partydj.pjr.server.Server;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.players.PlayerException;
import de.klierlinge.utils.Functions;

/**
 * Hauptklasse vom PartyDJ.
 * 
 * @author Eraser
 *
 */
public class PartyDJ extends Controller
{
	private static Logger log = LoggerFactory.getLogger(PartyDJ.class);
	/** Verbindung zur Datenbank. */
	protected IData data;
	/** Verwendeter Player. */
	protected IPlayer player;
	
	public PartyDJ(final String[] args)
	{
		super(args);
		
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
		closeListenThread.setName("Close Listener");
		Runtime.getRuntime().addShutdownHook(closeListenThread);
		
		loadLookAndFeel(splash);
		
		/* Parameter auswerten. */
		String dbPath = Functions.getFolder() + System.getProperty("file.separator") + "DataBase";
		final List<String> windowsToLoad = new ArrayList<>();
		
		int lastParam = 0;
		for(final String arg : args)
		{
			final String argl = arg.toLowerCase();
			if(arg.charAt(0) == '-' || arg.charAt(0) == '+')
			{
				if("-dbpath".equals(argl))
					lastParam = 1;
				else if("+window".equals(argl))
					lastParam = 3;
				else
					lastParam = 0;
			}
			else
			{
				switch(lastParam)
				{
				case 1:	//-dbpath
					dbPath = arg;
					break;
				case 3:	//+window
					windowsToLoad.add(arg);
					break;
				}
				lastParam = 0;
			}
		}
		
		loadDb(splash, dbPath);
			
		loadPlayer(splash);
		
		loadLists(splash);

		loadWindows(splash, windowsToLoad);

		
		splash.setOpacity(1f);
		if(listProvider.getMasterList().getSize() == 0)
		{
			registerWindow(new de.klierlinge.partydj.gui.SetupWindow());
		}
		else
		{
			startPlayer(splash);
		}
		
		splash.setInfo("Lade Plugins");
		{
			try
			{
				//new network.WinLircReceiver().initialise();
				new Server().start();
			}
			catch(final Throwable t)
			{
				log.error("Plugin laden fehlgeschlagen.", t);
			}
		}
		
		/* KeyStrokeManager laden. */
		try
		{
			if(System.getenv().get("OS").equalsIgnoreCase("windows_NT"))
				KeyStrokeManager.getInstance().initHotKeys();
		}
		catch(final Throwable t)
		{
			log.error("Fehler beim Laden von Intellitype!", t);
		}
		
		getData().writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		splash.close();

		splash.setInfo("PartyDJ bereit :)");
		
		loadFinished = true;
		
		/*player.addPlayStateListener(new players.PlayStateAdapter(){
			@Override
			public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
			{

				System.out.println(playingCurrent);
			}
		});*/
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
	public void closePartyDJ()
	{
		data.writeSetting("LastPosition", Double.toString(player.getPosition()));
		super.closePartyDJ();
	}

	/** Lädt SystemLookAndFeel. */
	private static void loadLookAndFeel(final SplashWindow splash)
	{
		splash.setInfo("Lade Look And Feel");
		
		//UI
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			log.error("Fehler bei Laden von Look And Feel.", e);
		}
	}
	
	/** Verbindet zur Datenbank. */
	private void loadDb(final SplashWindow splash, final String dbPath)
	{
		splash.setOpacity(0.82f);
		splash.setInfo("Verbinde zur Datenbank");

		try
		{
			data = new DerbyDB(dbPath);
		}
		catch (final OpenDbException e)
		{
			log.error("Keine Verbindung zur Datenbank möglich!", e);
			Controller.getInstance().fatalExit();
		}
	}
	
	/** Startet den Player. */
	private void loadPlayer(final SplashWindow splash)
	{
		splash.setOpacity(0.88f);
		splash.setInfo("Lade Player");

		final PlayerListener playerListener = new PlayerListener();	// implements PlayerContact, PlayStateListener
		splash.setInfo("Lade Player0");
		player = new de.klierlinge.partydj.players.jl.JLPlayer(playerListener);
		splash.setInfo("Lade Player 1");
		try
		{
			player.setVolume(Integer.parseInt(data.readSetting("Volume", "100")));
		}
		catch (final NumberFormatException e1)
		{
			player.setVolume(100);
		}
		catch (final SettingException e1)
		{
			player.setVolume(100);
		}
		
		splash.setInfo("Lade Player2");
		player.addPlayStateListener(playerListener);
		splash.setInfo("Lade Player3");
	}
	
	private void loadLists(final SplashWindow splash)
	{
		splash.setOpacity(0.9f);
		splash.setInfo("Lade Listen");

		try
		{
			listProvider = new ListProvider();
			playList = listProvider.getDbList("Wunschliste");				
			lastPlayedList = listProvider.getDbList("LastPlayed");
		}
		catch (final ListException e)
		{
			log.error("Listen konnten nicht geladen werden!", e);
			Controller.getInstance().fatalExit();
		}
	}
	
	private void loadWindows(final SplashWindow splash, final List<String> windowsToLoad)
	{
		splash.setOpacity(0.95f);
		splash.setInfo("Lade Fenster");
		{
			if(windowsToLoad.size() == 0)
				windowsToLoad.add(de.klierlinge.partydj.gui.ClassicWindow.class.getName());
			
			int loaded = 0;
			
			for(final String window : windowsToLoad)
				if(loadWindow(window))
					loaded++;
			
			if(loaded == 0)
			{
				log.error("Konnte kein Fenster laden.");
				Controller.getInstance().fatalExit();
			}
		}
	}
	
	private void startPlayer(final SplashWindow splash)
	{
		splash.setInfo("Starte");
		String firstTrackPath = null;
		try
		{
			firstTrackPath = data.readSetting("Playing");
		}
		catch (final SettingException e)
		{
			log.warn("data.readSetting(\"Playing\")", e);
		}
		if(firstTrackPath != null)
		{
			DbTrack firstTrack = null;
			try
			{
				firstTrack = data.getTrack(firstTrackPath, false);
			}
			catch(final ListException e1)
			{
				log.error("Erster Track konnte nicht gefunden werden.", e1);
			}
			if(firstTrack != null)
			{
				splash.setInfo("Starte " + firstTrack.getName());
				try
				{
					double pos = 0;
					double lastPosition = 0;
					boolean autoPlay = false;
					player.load(firstTrack);
					try
					{
						autoPlay = Boolean.parseBoolean(data.readSetting("AUTO_PLAY", "true"));
						pos = Double.parseDouble(data.readSetting("POSITION", "-1"));
						lastPosition = Double.parseDouble(data.readSetting("LastPosition", "0"));
					}
					catch (final NumberFormatException e) { /* ignored */ }
					catch (final SettingException e) { /* ignored */ }
					
					pos = pos >= 0 ? pos : lastPosition;
					player.setPosition(pos, false);
					if(autoPlay)
						player.fadeIn();
				}
				catch (final PlayerException e)
				{
					log.error("Failed to start track.", e);
				}
			}
		}
	}
}
