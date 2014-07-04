package network.remoteV2.server;

import java.io.IOException;
import java.net.Socket;
import network.remoteV2.InputHandler;
import network.remoteV2.JsonDecoder;
import network.remoteV2.JsonEncoder;
import network.remoteV2.beans.Message;
import network.remoteV2.beans.Test;

public class ServerHandler implements InputHandler
{
	private final Server server;
	private final Socket socket;
	private final JsonDecoder jsonDecoder;
	private JsonEncoder jsonEncoder;

	public ServerHandler(Server server, Socket socket) throws IOException
	{
		this.server = server;
		this.socket = socket;

		server.addServerHandler(this);
		jsonEncoder = new JsonEncoder(socket.getOutputStream());
		jsonDecoder = new JsonDecoder(socket.getInputStream(), this);
	}

	public void stop()
	{
		jsonDecoder.stop();
		try
		{
			socket.close();
		}
		catch(IOException ignore)
		{
			/* Ignore */
		}
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

	@Override
	public void inputHandlerClosed(boolean externalReason)
	{
		server.removeServerHandler(this);
	}

	public static void main(String... args) throws InterruptedException
	{
		System.out.println("Server: Start");
		new Server().start();
		Thread.sleep(5000);
		System.out.println("Server: End");
	}
}
