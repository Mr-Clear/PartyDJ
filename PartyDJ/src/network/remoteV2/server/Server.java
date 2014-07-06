package network.remoteV2.server;

import basics.Plugin;

import java.util.HashSet;
import java.util.Set;

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
			for(ServerHandler serverHandler : serverHandlers)
				serverHandler.stop();
		}
	}

	@Override
	public boolean isRunning()
	{
		return listener != null;
	}
	
	void addServerHandler(ServerHandler serverHandler)
	{
		synchronized (serverHandlers)
		{
			serverHandlers.add(serverHandler);
		}
	}
	
	void removeServerHandler(ServerHandler serverHandler)
	{
		synchronized (serverHandlers)
		{
			serverHandlers.remove(serverHandler);
		}
	}

	public static void main(String... args)
	{
	    new basics.PartyDJ(args);
	}
}