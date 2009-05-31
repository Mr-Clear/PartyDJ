package network;
import gui.settings.SettingNode;
import java.io.*;
import java.net.*;
import java.util.*;
import data.IData;
import basics.Controller;
import basics.Plugin;

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
	final static protected Controller controller = Controller.getInstance();
	final static protected IData data = controller.getData();
	final protected List<WinLircListener> listeners = new ArrayList<WinLircListener>();
	final protected Map<String, WinLircReceiverKeyAction> keyActions = new WinLircReceiverSelfSavingMap();
	protected boolean running;
	
	static
	{
		controller.addSettingNode(new SettingNode("WinLIRC", WinLircReceiverSettings.class), controller.getSetingTree());
	}
	
	public WinLircReceiver()
	{
		if(instance == null)
			instance = this;
		else
			controller.logError(Controller.NORMAL_ERROR, this, null, "Es wurde eine weitere Instanz von WinLircReceiver erstellt.");
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
	public void start()
	{
		if(instance == this)
		{
			if(!isRunning())
			{
				thread = new WinLircReceiverThread();
				thread.start();
			}
			else
				controller.logError(Controller.UNIMPORTANT_ERROR, this, null, "WinLircReceiver wurde gestartet, obwohl er schon läuft.");
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
						catch (IOException e)
						{
							controller.logError(Controller.REGULAR_ERROR, this, e, "Socket.getInputStream() konnte nicht geschlossen werden.");
						}
					}
				}.start();
				
				changeStatus(false);
			}
		}
		else
			instance.stop();
	}
	
	@Override
	public boolean isRunning()
	{
		return running;
	}
	
	public void addWinLircListener(WinLircListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeWinLircListener(WinLircListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void execute(String[] data)
	{
		long keyCode = Long.parseLong(data[0], 16);
		int repeat = Integer.parseInt(data[1], 16);
		synchronized (listeners)
		{
			for(WinLircListener listener : listeners)
			{
				listener.keyPressed(data[3], data[2], repeat, keyCode);
			}
		}
		
		WinLircReceiverKeyAction action = keyActions.get(data[3] + " " + data[2]);

		if(action != null && (action.repeat || repeat == 0))
			controller.getScripter().executeCommand(action.command);
	}
	
	protected void changeStatus(boolean newStatus)
	{
		running = newStatus;
		synchronized (listeners)
		{
			for(WinLircListener listener : listeners)
			{
				listener.statusChanged(newStatus);
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
		
		@Override
		public void run()
		{
			String ip = data.readSetting("WinLIRC-IP", "127.0.0.1");
		 	int port = Integer.parseInt(data.readSetting("WinLIRC-Port", "8765"));
		 	
		 	BufferedReader reader;
		 	try
			{
		 		Socket socket = new Socket(ip,port);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				WinLircReceiver.this.reader = reader;
			}
			catch (UnknownHostException e)
			{
				controller.logError(Controller.REGULAR_ERROR, this, e, "Fehler bei Verbinden zu WinLIRC.");
				return;
			}
			catch (IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Verbinden zu WinLIRC.");
				return;
			}
			
			changeStatus(true);

			while(true)
			{
				String in;
				try
				{
					in = reader.readLine();
				}
				catch (IOException e)
				{
					changeStatus(false);
					controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler in Verbindung zu WinLIRC.");
					break;
				}
				if(in == null)
					break;
				
				if(isInterrupted())
					return;

				String[] data = in.split(" ");
				if(data.length == 4)
					execute(data);
			}
			changeStatus(false);
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
		 */
		void statusChanged(boolean running);
	}
}


