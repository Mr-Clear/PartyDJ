package network.remoteV2.server;

import java.io.IOException;
import java.net.Socket;
import network.remoteV2.InputHandler;
import network.remoteV2.JsonDecoder;
import network.remoteV2.beans.Message;
import network.remoteV2.client.ClientTest;

public class ServerHandler implements InputHandler
{
	private final Server server;
	private final Socket socket;
	private final JsonDecoder jsonDecoder;

	public ServerHandler(Server server, Socket socket) throws IOException
	{
		this.server = server;
		this.socket = socket;

		server.addServerHandler(this);

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
		System.out.println(message);
	}

	@Override
	public void inputHandlerClosed()
	{
		server.removeServerHandler(this);
	}

	public static void main(String... args) throws InterruptedException, IOException
	{
		new Server().start();
		ClientTest.main();
		Thread.sleep(1000);
	}
}
