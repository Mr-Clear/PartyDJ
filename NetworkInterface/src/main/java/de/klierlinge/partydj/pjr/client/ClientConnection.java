package de.klierlinge.partydj.pjr.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import de.klierlinge.partydj.pjr.InputHandler;
import de.klierlinge.partydj.pjr.JsonDecoder;
import de.klierlinge.partydj.pjr.JsonEncoder;
import de.klierlinge.partydj.pjr.beans.Message;

public class ClientConnection implements InputHandler
{
	public static final int DEFAULT_PORT = 2804;
	
	private volatile boolean running = true;
	private int port;
	private String host;
	private final Client client;
	private JsonEncoder encoder;
	
	public ClientConnection(final Client client)
    {
        super();
        this.client = client;
    }

    public void connect()
	{
		connect("localhost", DEFAULT_PORT);
	}

	public void connect(final String connectHost)
	{
		connect(connectHost, DEFAULT_PORT);
	}

	@SuppressWarnings("resource")
	public void connect(final String connectHost, final int connectPort)
	{
		this.host = connectHost;
		this.port = connectPort;

		Executors.newSingleThreadExecutor().execute(new Runnable() // TODO: Use common pool.
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
						encoder = new JsonEncoder(socket.getOutputStream());
						connectionOpened();
						
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
							e.printStackTrace(); // TODO: Log me.
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
	
    synchronized public void send(final Message message) throws IOException
    {
        if(encoder != null)
        {
        	encoder.write(message);
        }
        else
        {
        	throw new IOException("Encoder not ready.");
        }
    }

	public void connectionOpened()
	{
		client.connectionOpened();
	}

	@Override
	public void inputHandlerClosed(final boolean externalReason)
	{
		encoder = null;
		client.connectionClosed(externalReason);
        connect(host, port);
	}
}
