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
import network.remoteV2.beans.Test;
import network.remoteV2.server.Server;

public class Client implements InputHandler
{
	private volatile boolean running = true;
	private int port;
	private String host;
	private JsonEncoder jsonEncoder;
	
	private Thread testThread;

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
						jsonEncoder = new JsonEncoder(socket.getOutputStream());
						connectionOpened();
						
						new JsonDecoder(socket.getInputStream(), Client.this);
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
		if(message instanceof Test)
		{
			Test test = (Test)message;
			System.out.println((test.echo ? "Ping " : "Pong ") + test.content);
			if(test.echo)
				try
				{
					jsonEncoder.write(new Test(false, test.content));
				}
				catch(IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void connectionOpened()
	{
		testThread = new Thread()
		{
			@Override
			public void run()
			{
				while(running && testThread == this)
				{
					try
					{
						jsonEncoder.write(new Test(true, "Test"));
						Thread.sleep(2000);
					}
					catch(IOException | InterruptedException e)
					{
						e.printStackTrace();
						Client.this.stop();
					}
				}
			}
		};
		testThread.start();
	}

	@Override
	public void inputHandlerClosed(boolean externalReason)
	{
		testThread = null;
		if(running)
			connect(host, port);
	}

	public static void main(String... args) throws InterruptedException
	{
		Client client = new Client();
		client.connect();
		Thread.sleep(100000);
	}
}
