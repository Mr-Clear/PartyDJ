package de.klierlinge.partydj.network.remoteV2.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.network.remoteV2.InputHandler;
import de.klierlinge.partydj.network.remoteV2.JsonDecoder;
import de.klierlinge.partydj.network.remoteV2.JsonEncoder;
import de.klierlinge.partydj.network.remoteV2.beans.Message;
import de.klierlinge.partydj.network.remoteV2.server.Server;

public class ClientConnection implements InputHandler
{
	private volatile boolean running = true;
	private int port;
	private String host;
	private final Client client;
	
	public ClientConnection(final Client client)
    {
        super();
        this.client = client;
    }

    public void connect()
	{
		connect("localhost", Server.PORT);
	}

	public void connect(final String connectHost)
	{
		connect(connectHost, Server.PORT);
	}

	@SuppressWarnings("resource")
	public void connect(final String connectHost, final int connectPort)
	{
		this.host = connectHost;
		this.port = connectPort;
		Controller.getInstance().getExecutor().execute(new Runnable()
        {
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
					catch(final IOException e)
					{
						if(e instanceof UnknownHostException || "Connection refused: connect".equals(e.getMessage()) || "Connection timed out: connect".equals(e.getMessage()))
						{
							try
							{
								Thread.sleep(1000);
							}
							catch(final InterruptedException ignore)
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
		});
		
	}

	public void stop()
	{
		running = false;
	}

	@Override
	public void messageReceived(final Message message)
	{
		client.messageReceived(message);
	}

	public void connectionOpened(final JsonEncoder jsonEncoder)
	{
		client.setJsonEncoder(jsonEncoder);
	}

	@Override
	public void inputHandlerClosed(final boolean externalReason)
	{
        client.setJsonEncoder(null);
        connect(host, port);
	}
}
