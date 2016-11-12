package network;
import basics.Controller;
import basics.Plugin;
import data.IData;
import gui.settings.SettingNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**Verbindet sich mit dem Infrarot-Fernsteuerung-Empfänger WinLIRC und verarbeitet empfangene Signale.
 * <p>
 * Es darf immer nur eine Instanz dieses Plugins laufen.
 * 
 * @author Eraser
 */
public class WinLircReceiver implements Plugin
{
	protected static WinLircReceiver instance;
	protected WinLircReceiverThread thread;
	protected BufferedReader reader;
	protected static final Controller CONTROLLER = Controller.getInstance();
	protected static final IData DATA = CONTROLLER.getData();
	protected final List<WinLircListener> listeners = new ArrayList<>();
	protected final Map<String, WinLircReceiverKeyAction> keyActions = new WinLircReceiverSelfSavingMap();
	protected boolean running;
	
	static
	{
		Controller.addSettingNode(new SettingNode("WinLIRC", WinLircReceiverSettings.class), CONTROLLER.getSetingTree());
	}
	
	public WinLircReceiver()
	{
		if(instance == null)
			instance = this;
		else
			CONTROLLER.logError(Controller.NORMAL_ERROR, this, null, "Es wurde eine weitere Instanz von WinLircReceiver erstellt.");
	}
	
	@Override
	public String getName()
	{
		return "WinLIRC-Receiver";
	}
	
	@Override
	public String getDescription()
	{
		return "Verbindet sich mit dem Infrarot-Fernsteuerung-Empfänger WinLIRC und verarbeitet empfangene Signale.";
	}
	
	@Override
	public void initialise()
	{
		if(Boolean.parseBoolean(DATA.readSetting("WinLIRC-Autorun", "false")))
			start();
	}

	@Override
	public void start()
	{
		changeStatus(true, "Starte...");
		if(instance == this)
		{
			thread = new WinLircReceiverThread();
			thread.start();
		}
		else
			instance.start();
	}

	@Override
	public void stop()
	{
		if(instance == this)
		{
			if(isRunning())
				thread.interrupt();
			if(reader != null)
			{
				final BufferedReader r = reader;
				new Thread(){
					@Override
					public void run()
					{
						try
						{
							if(r != null)
								r.close();
						}
						catch (final IOException e)
						{
							CONTROLLER.logError(Controller.REGULAR_ERROR, this, e, "Socket.getInputStream() konnte nicht geschlossen werden.");
						}
					}
				}.start();
				
				changeStatus(false, "Offline");
			}
			changeStatus(false, "Offline");
		}
		else
			instance.stop();
	}
	
	@Override
	public boolean isRunning()
	{
		return running;
	}
	
	public void addWinLircListener(final WinLircListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeWinLircListener(final WinLircListener listener)
	{
		listeners.remove(listener);
	}
	
	// Workaround für Grundig.
	String lastGrundigKey = null;
	String savedGrundigKey = null;
	int lastGrundigRepeat = 0;
	int savedGrundigRepeat = 0;
	
	protected void execute(final String[] keyData)
	{
		final long keyCode = Long.parseLong(keyData[0], 16);
		int repeat = Integer.parseInt(keyData[1], 16);
		
		// Workaround für Grundig.
		if("TP_715_SAT".equals(keyData[3]))
		{
			lastGrundigKey = keyData[2];
			lastGrundigRepeat = repeat;
			
			if(savedGrundigKey != null && savedGrundigKey.equals(keyData[2]))
				repeat -= savedGrundigRepeat;
		}
		else if("TP_715_SAT_PRE".equals(keyData[3]) && "PRE".equals(keyData[2]))
		{
			savedGrundigKey = lastGrundigKey;
			savedGrundigRepeat = lastGrundigRepeat + 1;
		}
		
		synchronized(listeners)
		{
			for(final WinLircListener listener : listeners)
			{
				listener.keyPressed(keyData[3], keyData[2], repeat, keyCode);
			}
		}
		
		final WinLircReceiverKeyAction action = keyActions.get(keyData[3] + " " + keyData[2]);

		if(action != null && (action.repeat || repeat == 0))
			CONTROLLER.getScripter().executeCommand(action.command);
	}
	
	protected void changeStatus(final boolean newStatus, final String newMessage)
	{
		running = newStatus;
		synchronized (listeners)
		{
			for(final WinLircListener listener : listeners)
			{
				listener.statusChanged(newStatus, newMessage);
			}
		}
	}
	
	protected class WinLircReceiverThread extends Thread
	{
		WinLircReceiverThread()
		{
			setDaemon(true);
			setName("WinLIRC Socket reader");
		}
		
		@SuppressWarnings("resource")
		@Override
		public void run()
		{
			final String ip = DATA.readSetting("WinLIRC-IP", "127.0.0.1");
		 	final int port = Integer.parseInt(DATA.readSetting("WinLIRC-Port", "8765"));
		 	
		 	Socket socket = null;
		 	BufferedReader r = null;
	 		while(isRunning())
	 		{
				changeStatus(true, "Verbinde...");
			 	try
				{
			 		socket = new Socket(ip, port);
					r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					reader = r;
				}
				catch (final UnknownHostException e)
				{
					CONTROLLER.logError(Controller.REGULAR_ERROR, this, e, "Kann nicht zu WinLIRC verbinden.");
				}
				catch (final IOException e)
				{
					CONTROLLER.logError(Controller.REGULAR_ERROR, this, e, "Kann nicht zu WinLIRC verbinden.");
				}
				
				if(r != null)
					break;

				try
				{
					Thread.sleep(10000);
				}
				catch (final InterruptedException ignored) { /* ignore */ }
	 		}
			
	 		if(socket != null)
				try
				{
					socket.close();
				}
				catch (final IOException e1)
				{
					/* ignore */
				}
	 		
	 		if(r == null)
	 			return;

			changeStatus(true, "Online");

			while(true)
			{
				String in;
				try
				{
					in = r.readLine();
				}
				catch (final IOException e)
				{
					if(isRunning())
						CONTROLLER.logError(Controller.REGULAR_ERROR, this, e, "Fehler in Verbindung zu WinLIRC.");
					break;
				}
				if(in == null)
					break;
				
				if(isInterrupted() && !isRunning())
					return;

				final String[] keyData = in.split(" ");
				if(keyData.length == 4)
					execute(keyData);
			}
			
			if(isRunning())
				WinLircReceiver.this.start();
			else
				changeStatus(false, "Offline");
		}
	}
	
	/** Bekommt Daten von WinLirc */
	protected interface WinLircListener
	{
		/**Eine Taste einer Fernsteuerung wurde gedrückt.
		 * <br><b>Achtung Threadsave!</b>
		 * @param remote Der Name der Fernbedienung, von der das Signal empfangen wurde.
		 * @param key Der Name der gedrückten Taste.
		 * @param repeat Die Anzahl der Wiederholungen. Sie erhöht sich, wenn man die Taste gedrückt hält.
		 * @param keyCode Der Code der gedrückten Taste.
		 */
		void keyPressed(String remote, String key, int repeat, long keyCode);
		/**Der Verbindungsstatus zu WinLIRC hat sich geändert.
		 * <br><b>Achtung Threadsave!</b>
		 * @param running Neuer Status.
		 * @param message Statusmeldung.
		 */
		void statusChanged(boolean running, String message);
	}
}


