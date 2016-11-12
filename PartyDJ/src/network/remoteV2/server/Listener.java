package network.remoteV2.server;

import basics.Controller;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Wartet auf ankommende Verbindungen.
 */
public class Listener extends Thread
{
	private volatile boolean running = true;
	private final Server server;

	private final Object serverSocketMonitor = new Object();
	private ServerSocket serverSocket;

	public Listener(final Server server)
	{
		this.server = server;
		setDaemon(true);
		setName("ServerListener");
	}

	@Override
	public void run()
	{
		try
		{

			synchronized (serverSocketMonitor)
			{
				if(serverSocket != null)
					throw new IllegalStateException("One Listener must not run twicet the same time.");
				serverSocket = new ServerSocket(Server.PORT);
			}
			while (running)
			{
				try
				{
					@SuppressWarnings("resource")
					final
					Socket socket = serverSocket.accept();
					new ServerHandler(server, socket);
				}
				catch (final IOException e)
				{
					Controller.getInstance().logError(Controller.INERESTING_INFO, e);
				}
			}
			synchronized (serverSocketMonitor)
			{
				serverSocket = null;
			}
		}
		catch (final IOException e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, e);
		}
	}

	public void stopGracefully()
	{
		running = false;
		synchronized (serverSocketMonitor)
		{
			if (serverSocket != null)
				try
				{
					serverSocket.close();
				}
				catch (final IOException e)
				{
					/* OMG STFU! */
				}
		}
	}
}
