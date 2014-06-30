package network.remoteV2;

import basics.Controller;
import flexjson.JSONDeserializer;
import flexjson.JSONException;
import flexjson.locators.TypeLocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import network.remoteV2.json.Command;
import network.remoteV2.json.Message;
import network.remoteV2.json.TrackList;

public class ServerHandler
{
	private final Server server;
	private final Socket socket;
	private volatile boolean running = true;
	private final HandlerThread handlerThread;

	public ServerHandler(Server server, Socket socket)
	{
		this.server = server;
		this.socket = socket;

		server.addServerHandler(this);

		handlerThread = new HandlerThread();
		handlerThread.run();
	}

	public void stop()
	{
		running = false;
	}

	class HandlerThread extends Thread
	{
		public HandlerThread()
		{
			this.setDaemon(true);
			this.setName("ServerHandlerThread");
		}

		@Override
		public void run()
		{
			JSONDeserializer<Message> deserializer = new JSONDeserializer<>();
			deserializer.use(".", new TypeLocator<String>("type").add("TrackList", TrackList.class).add("Command", Command.class));
			try(InputStream inputStream = socket.getInputStream();
					Reader reader = new InputStreamReader(inputStream))
			{
				while(running)
				{
					Message message = deserializer.deserialize(reader);
					System.out.println(message);
				}
			}
			catch(JSONException e)
			{
				if(!socket.isClosed())
					Controller.getInstance().logError(Controller.IMPORTANT_ERROR, e);
				ServerHandler.this.stop();
			}
			catch(IOException e)
			{
				Controller.getInstance().logError(Controller.IMPORTANT_ERROR, e);
			}
			finally
			{
				server.removeServerHandler(ServerHandler.this);
			}
		}
	}

	public static void main(String... args) throws InterruptedException, IOException
	{
		new Server().start();
		ClientTest.main();
		Thread.sleep(1000);
	}
}
