package network.remoteV2;

import basics.Controller;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import network.remoteV2.json.Message;
import network.remoteV2.json.TrackList;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
			try(InputStream inputStream = new FilterInputStream(socket.getInputStream())
			{
				int c;
				StringBuilder sb = new StringBuilder();

				@Override
				public int read() throws IOException
				{
					int i = in.read();
					if(i == -1)
						return i;
					c++;

					sb.append((char)i);
					if(i == '}')
					{
						System.out.println(sb);
						sb.setLength(0);
					}
					//					if (c == 168)
					//						System.out.println("Data arrived");
					//					if (c > 168)
					//						System.out.println("Too much Data!: " + i);
					return i;
				}

				@Override
				public int read(byte[] b, int off, int len) throws IOException
				{
					/* Copied from Inputsream. super.super.read(b, off, len) */
					if(b == null)
					{
						throw new NullPointerException();
					}
					else if(off < 0 || len < 0 || len > b.length - off)
					{
						throw new IndexOutOfBoundsException();
					}
					else if(len == 0)
					{
						return 0;
					}

					int c1 = read();
					if(c1 == -1)
					{
						return -1;
					}
					b[off] = (byte)c1;

					int i = 1;
					for(; i < len; i++)
					{
						c1 = read();
						if(c1 == -1)
						{
							break;
						}
						b[off + i] = (byte)c1;
					}
					return i;
				}
			})
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
				JsonFactory f = mapper.getJsonFactory();
				try(JsonParser jp = f.createJsonParser(inputStream))
				{
					while(running)
					{
						Message m = jp.readValueAs(Message.class);
						System.out.println(((TrackList)m).getName());
					}
				}
				catch(EOFException e) // Connection closed.
				{
					System.out.println("Close");
					ServerHandler.this.stop();
				}

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

	@SuppressWarnings("resource")
	public static void main(String... args) throws JsonParseException, JsonMappingException, IOException, InterruptedException
	{
		//		ObjectMapper mapper = new ObjectMapper();
		//		JsonFactory f = mapper.getJsonFactory();
		//		JsonParser jp = f.createJsonParser(new FileInputStream("test.txt"));
		//		while (true)
		//		{
		//			try
		//			{
		//				Message m = jp.readValueAs(Message.class);
		//				System.out.println(((TrackList) m).getName());
		//			}
		//			catch (EOFException e)
		//			{
		//				break;
		//			}
		//		}

		new Server().start();
		ClientTest.main();
		Thread.sleep(1000);
	}
}
