package de.klierlinge.partydj.network.remoteV2.server;

import java.util.HashSet;
import java.util.Set;
import de.klierlinge.partydj.basics.Plugin;

/** Ermöglicht es, den PartyDJ über Netzwerk zu bedienen. */
public class Server implements Plugin
{
	public final static int PORT = 2804;

	/** Jeder verbundene Client bekommt einen ServerHandler. */
	private final Set<ServerHandler> serverHandlers = new HashSet<>();
	private Listener listener;

	@Override
	public String getName()
	{
		return "Network Server";
	}

	@Override
	public String getDescription()
	{
		return "Network Server V2";
	}

	@Override
	public void initialise()
	{
		/* Nothing to do. */
	}

	@Override
	public synchronized void start()
	{
		if(listener == null)
			listener = new Listener(this);
		new Listener(this).start();
	}

	@Override
	synchronized public void stop()
	{
		if(listener != null)
			listener.stopGracefully();
		listener = null;
		synchronized (serverHandlers)
		{
			for(final ServerHandler serverHandler : serverHandlers)
				serverHandler.stop();
		}
	}

	@Override
	public boolean isRunning()
	{
		return listener != null;
	}
	
	void addServerHandler(final ServerHandler serverHandler)
	{
		synchronized (serverHandlers)
		{
			serverHandlers.add(serverHandler);
		}
	}
	
	void removeServerHandler(final ServerHandler serverHandler)
	{
		synchronized (serverHandlers)
		{
			serverHandlers.remove(serverHandler);
		}
	}

	public static void main(final String... args)
	{
	    new de.klierlinge.partydj.basics.PartyDJ(args);
	}
}