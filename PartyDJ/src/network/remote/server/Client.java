package network.remote.server;

import basics.Controller;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Client
{
	private final Socket socket;
	private final Server server;

	public Client(Socket socket, Server server)
	{
		this.socket = socket;
		this.server = server;
	}
	
	void close()
	{
		server.removeClient(this);
	}
	
	class inputHandler implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				@SuppressWarnings({ "unused", "resource" })
				final InputStream inputStream = socket.getInputStream();
				// http://wiki.fasterxml.com/JacksonStreamingApi
			}
			catch (IOException e)
			{
				Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, e);
			}
		}
		
	}
}
