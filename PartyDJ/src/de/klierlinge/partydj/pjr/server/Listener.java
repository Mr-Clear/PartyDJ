package de.klierlinge.partydj.pjr.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.pjr.client.ClientConnection;

/**
 * Wartet auf ankommende Verbindungen.
 */
public class Listener extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(Listener.class);
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
				serverSocket = new ServerSocket(ClientConnection.DEFAULT_PORT);
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
					log.warn("Failed to accept connection.", e);
				}
			}
			synchronized (serverSocketMonitor)
			{
				serverSocket = null;
			}
		}
		catch (final IOException e)
		{
			log.error("Failed to listen for incomming connections.", e);
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
