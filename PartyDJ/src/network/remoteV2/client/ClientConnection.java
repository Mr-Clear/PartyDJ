package network.remoteV2.client;

import basics.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import network.remoteV2.InputHandler;
import network.remoteV2.JsonDecoder;
import network.remoteV2.JsonEncoder;
import network.remoteV2.beans.Message;
import network.remoteV2.server.Server;

public class ClientConnection implements InputHandler
{
	private volatile boolean running = true;
	private int port;
	private String host;
	private final Client client;
	
	public ClientConnection(Client client)
    {
        super();
        this.client = client;
    }

    public void connect()
	{
		connect("localhost", Server.PORT);
	}

	public void connect(String connectHost)
	{
		connect(connectHost, Server.PORT);
	}

	@SuppressWarnings("resource")
	public void connect(String connectHost, int connectPort)
	{
		this.host = connectHost;
		this.port = connectPort;
		Thread connectThread = new Thread(){
			@Override
			public void run()
			{
				Socket socket = null;
				boolean retry = true;
				while(retry && running)
				{
					try
					{
						socket = new Socket();
						socket.connect(new InetSocketAddress(host, port));
						connectionOpened(new JsonEncoder(socket.getOutputStream()));
						
						new JsonDecoder(socket.getInputStream(), ClientConnection.this);
						retry = false;
					}
					catch(IOException e)
					{
						if(e instanceof UnknownHostException || "Connection refused: connect".equals(e.getMessage()) || "Connection timed out: connect".equals(e.getMessage()))
						{
							try
							{
								Thread.sleep(1000);
							}
							catch(InterruptedException ignore)
							{
								/* ignore */
							}
							retry = true;
						}
						else
						{
							Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
						}
					}
				}
			}
		};
		connectThread.setDaemon(true);
		connectThread.setName("ConnectThread");
		connectThread.start();
		
	}

	public void stop()
	{
		running = false;
	}

	@Override
	public void messageReceived(Message message)
	{
		client.messageReceived(message);
	}

	public void connectionOpened(JsonEncoder jsonEncoder)
	{
		client.setJsonEncoder(jsonEncoder);
	}

	@Override
	public void inputHandlerClosed(boolean externalReason)
	{
        client.setJsonEncoder(null);
        connect(host, port);
	}
}
