package network.remote;

import basics.Controller;
import basics.Plugin;
import common.Track;
import data.ListListener;
import data.SettingListener;
import lists.data.DbTrack;
import players.PlayStateListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * RemoteServer soll alle Funktionen und Events über Netzwerk verfügbar machen.
 * 
 * @author Eraser
 */
public class RemoteServer implements Plugin
{
	public static final int PORT = 2803;
	protected boolean running = false;
	protected Map<Socket, ObjectOutputStream> clientList = new HashMap<>();
	protected Controller controller = Controller.getInstance();
	protected Listener listener = new Listener();
	
	@Override
	public String getDescription()
	{
		return "Ermöglicht es über Netzwerk auf den PartyDJ zuzugreifen.";
	}

	@Override
	public String getName()
	{
		return "Remote Server";
	}

	@Override
	public void initialise()
	{
		start();
	}

	@Override
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void start()
	{
		running = true;
		new ConnectionListener();
		controller.getPlayer().addPlayStateListener(listener);
		controller.getData().addListListener(listener);
		controller.getData().addSettingListener(listener);
	}

	@Override
	public void stop()
	{
		running = false;
		controller.getPlayer().removePlayStateListener(listener);
		controller.getData().removeListListener(listener);
		controller.getData().removeSettingListener(listener);
		
		for(Socket socket : clientList.keySet())
		{
			try
			{
				socket.close();
			}
			catch(IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Remote Server Verbindung nicht beednen.");
			}
		}
		clientList.clear();
	}
	
	protected void sendEvent(final Event event)
	{
		synchronized(clientList)
		{
			List<Socket> toRemove = new ArrayList<>();
			for(Entry<Socket, ObjectOutputStream> entry : clientList.entrySet())
			{
				try
				{
					entry.getValue().writeObject(event);
				}
				catch(IOException e)
				{
					controller.logError(Controller.NORMAL_ERROR, this, e, "Senden von Remote Server fehlgeschlagen.\nClient wird getrennt.");
					try
					{
						entry.getKey().close();
					}
					catch(IOException e1)
					{
						controller.logError(Controller.NORMAL_ERROR, this, e, "Remote Server konnte Verbindung nicht beednen.");
					}

					toRemove.add(entry.getKey());
				}
			}
			
			for(Socket socket : toRemove)
			{
				clientList.remove(socket);
			}
		}
	}
	
	protected void sendAnswer(final long invocationId, final Serializable data, final Socket client)
	{
		synchronized(clientList)
		{
			try
			{
				clientList.get(client).writeObject(new Answer(invocationId, data));
			}
			catch(IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Antwort an Client von Remote Server fehlgeschlagen.");
			}
		}
	}
	
	protected class ClientListener extends Thread
	{
		final Socket client;
		public ClientListener(Socket client)
		{
			this.client = client;
			setDaemon(true);
			super.start();
		}
		
		@Override
		public void run()
		{
			ObjectInputStream ois;
			try
			{
				ois = new ObjectInputStream(client.getInputStream());
			}
			catch(IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler in Verbindung von Remote Server.");
				clientList.remove(client);
				return;
			}
			
			try
			{
				while(running)
				{
					Object o = ois.readObject();
					if(o instanceof Invocation)
						((Invocation)o).invoke(RemoteServer.this, client);
					else
						controller.logError(Controller.NORMAL_ERROR, this, null, "Remote Server hat unbekannte Daten empfangen:\n\t" + o.getClass().getName() + "\n\t" + o.toString());
				}
			}
			catch(SocketException e)
			{ /* Vermutlich Verbindung geschlossen. */ }
			catch(EOFException e)
			{ /* Vermutlich Verbindung geschlossen. */ }
			catch(IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler in Verbindung von Remote Server.");
			}
			catch(ClassNotFoundException e)
			{
				controller.logError(Controller.IMPORTANT_ERROR, this, e, "Remote Server hat Danten unbekannten Typs empfangen.");
			}
			
			try
			{
				client.close();
			}
			catch(IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Remote Server konnte Verbindung nicht beednen.");
			}
			
			clientList.remove(client);
		}
	}

	protected class ConnectionListener extends Thread
	{
		public ConnectionListener()
		{
			setDaemon(true);
			super.start();
		}
		
		@SuppressWarnings("resource")
		@Override
		public void run()
		{
			try(ServerSocket serverListener = new ServerSocket(PORT))
			{
				while(running)
				{
					Socket client;
					try
					{
						client = serverListener.accept();
						clientList.put(client, new ObjectOutputStream(client.getOutputStream()));
						new ClientListener(client);
					}
					catch(SocketTimeoutException ignored)
					{ /* Tritt alle 10 Sekunden auf. */ }
					catch(IOException e)
					{
						controller.logError(Controller.IMPORTANT_ERROR, this, e, "Verbindung mit Remote Server fehlgeschlagen.\nRemote Server beendet sich.");
						RemoteServer.this.stop();
					}
				}
				
				serverListener.setSoTimeout(10000);
			}
			catch(IOException e)
			{
				controller.logError(Controller.IMPORTANT_ERROR, this, e, "Remote Server kann keinen Socket öffnen.");
				return;
			}
		}
	}
	
	protected class Listener implements PlayStateListener, ListListener, SettingListener
	{		
		@Override
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
		{
			sendEvent(new Event.CurrentTrackChanged(playedLast, playingCurrent, reason));
		}

		@Override
		public void playStateChanged(boolean playState)
		{
			sendEvent(new Event.PlayStateChanged(playState));
		}

		@Override
		public void volumeChanged(int volume)
		{
			sendEvent(new Event.VolumeChanged(volume));
		}

		@Override
		public void listAdded(String listName)
		{
			sendEvent(new Event.ListAdded(listName));
		}

		@Override
		public void listCommentChanged(String listName, String newComment)
		{
			sendEvent(new Event.ListCommentChanged(listName, newComment));
		}

		@Override
		public void listPriorityChanged(String listName, int newPriority)
		{
			sendEvent(new Event.ListPriorityChanged(listName, newPriority));
		}

		@Override
		public void listRemoved(String listName)
		{
			sendEvent(new Event.ListRemoved(listName));
		}

		@Override
		public void listRenamed(String oldName, String newName)
		{
			sendEvent(new Event.ListRenamed(oldName, newName));
		}

		@Override
		public void trackAdded(DbTrack track, boolean eventsFollowing)
		{
			sendEvent(new Event.TrackAdded(track, eventsFollowing));
		}

		@Override
		public void trackChanged(DbTrack newTrack, Track oldTrack, boolean eventsFollowing)
		{
			sendEvent(new Event.TrackChanged(newTrack, oldTrack, eventsFollowing));
		}

		@Override
		public void trackDeleted(DbTrack track, boolean eventsFollowing)
		{
			sendEvent(new Event.TrackDeleted(track, eventsFollowing));
		}

		@Override
		public void trackInserted(String listName, int position, DbTrack track, boolean eventsFollowing)
		{
			sendEvent(new Event.TrackInserted(listName, position, track, eventsFollowing));
		}

		@Override
		public void trackRemoved(String listName, int position, boolean eventsFollowing)
		{
			sendEvent(new Event.TrackRemoved(listName, position, eventsFollowing));
		}

		@Override
		public void tracksSwaped(String listName, int positionA, int positionB, boolean eventsFollowing)
		{
			sendEvent(new Event.TracksSwaped(listName, positionA, positionB, eventsFollowing));
		}

		@Override
		public void settingChanged(String name, String value)
		{
			sendEvent(new Event.SettingChanged(name, value));
		}
	}
}
