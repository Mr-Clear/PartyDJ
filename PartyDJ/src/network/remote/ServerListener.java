package network.remote;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerListener extends Thread
{
	final protected NetworkPlayer player;
	final protected NetworkData data;
	final protected Socket server;
	final protected AnswerListener answerListener;

	public ServerListener(Socket server, NetworkData data, NetworkPlayer player, AnswerListener answerListener)
	{
		setDaemon(true);
		setName("ServerListener");
		this.server = server;
		this.data= data;
		this.player = player;
		this.answerListener = answerListener;
		super.start();
	}

	@Override
	public void run()
	{
		ObjectInputStream ois;
		try
		{
			ois = new ObjectInputStream(server.getInputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}

		while(true)
		{
			final Object o;
			try
			{
				o = ois.readObject();
			}
			catch(SocketException e)
			{
				/* Vermutlich Verbindung geschlossen. */
				break;
			}
			catch(EOFException e)
			{
				/* Vermutlich Verbindung geschlossen. */
				break;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				break;
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}

			if(o instanceof Event)
			{
				new Thread(){
					@Override public void run()
					{
						Event e = (Event)o;
						e.invoke(player.getPlayStateListener(), data.getListListener(), data.getSettingListener());
					}
				}.start();
			}
			else if(o instanceof Answer)
			{
				answerListener.answerArrived((Answer)o);
			}
			else
			{
				System.err.println("Was komisches angekommen:\n\t" + o.getClass().getName() + "\n\t" + o.toString());
			}
		}
	}
}
