package network.remote.server;

import basics.Controller;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Listener extends Thread
{
	final Server server;
	private volatile boolean stop = false;
	ServerSocket serverSocket;

	public Listener(Server server)
	{
		this.server = server;
		setDaemon(true);
		setName(server.getName());
	}

	@Override
	public void run()
	{
		try
		{
			serverSocket = new ServerSocket(Server.PORT);
			while (!stop)
			{
				try
				{
					Client client = new Client(serverSocket.accept(), server);
					server.addClient(client);
				}
				catch(SocketException e)
				{
					if(!"socket closed".equals(e.getMessage()))
						throw e;
				}
				
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.treadExits();
	}

	void stopListener()
	{
		synchronized (server)
		{
			stop = true;
			try
			{
				if(serverSocket != null)
					serverSocket.close();
			}
			catch (IOException e)
			{
				Controller.getInstance().logError(Controller.UNIMPORTANT_INFO, e);
			}
		}
	}
}