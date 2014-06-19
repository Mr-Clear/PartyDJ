package network.remote.server;

import basics.Controller;
import basics.Plugin;
import gui.settings.SettingNode;
import java.util.HashSet;
import java.util.Set;

public class Server implements Plugin
{
	public final static int PORT = 6584;
	private Listener listener;
	private Set<Client> clients = new HashSet<>();
	
	@Override
	public String getName()
	{
		return "Network Server";
	}

	@Override
	public String getDescription()
	{
		return "Ermöglicht es, diesen PartyDJ fernzusteuern.";
	}

	@Override
	public void initialise()
	{
		Controller.addSettingNode(new SettingNode(getName(), network.remote.server.Settings.class), Controller.getInstance().getSetingTree());
	}

	@Override
	public synchronized void start()
	{
		if(isRunning())
			return;
		listener = new Listener(this); 
		listener.start();
	}

	@Override
	public synchronized void stop()
	{
		if(isRunning())
			listener.stopListener();
		for(Client client : clients)
			client.close();
	}

	@Override
	public synchronized boolean isRunning()
	{
		if(listener == null)
			return false;
		return listener.isAlive();
	}
	
	void treadExits()
	{
		stop();
	}
	
	synchronized void addClient(Client client)
	{
		clients.add(client);
	}
	
	synchronized void removeClient(Client client)
	{
		clients.remove(client);
	}
}
