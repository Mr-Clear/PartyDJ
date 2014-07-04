package network.remoteV2;

import basics.Controller;
import flexjson.JSONDeserializer;
import flexjson.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import network.remoteV2.beans.Message;

/**
 * Dekodiert Daten aus einem Json Stream und gibt Messages aus.
 */
public class JsonDecoder
{
	/** Stream mit Json-Daten. */
	private final InputStream inputStream;
	/** Empfänger der Nachrichten. */
	private final InputHandler inputHandler;
	/** Thread, der auf dem inputstream sitzt. */
	private final ReceiverThread receiverThread;
	/** Wenn false, stoppt der decoder-Thread. */
	private volatile boolean running = true;

	/**
	 * @param inputStream Stream, aus dem die Json-Daten kommen.
	 * @param inputHandler Empfänger der Nachrichten.
	 */
	public JsonDecoder(InputStream inputStream, InputHandler inputHandler)
	{
		this.inputStream = inputStream;
		this.inputHandler = inputHandler;

		receiverThread = new ReceiverThread();
		receiverThread.start();
	}

	/** Stoppt den ReceiverThread. */
	public void stop()
	{
		running = false;
		try
		{
			inputStream.close();
		}
		catch(IOException ignore)
		{
			/* Ignore */
		}
	}

	private class ReceiverThread extends Thread
	{
		public ReceiverThread()
		{
			setDaemon(true);
			setName("NetworkReceiver");
		}

		@Override
		public void run()
		{
			JSONDeserializer<Message> deserializer = new JSONDeserializer<>();
			Message.configureDeserializer(deserializer);
			try(Reader reader = new InputStreamReader(inputStream))
			{
				while(running)
				{
					Message message = deserializer.deserialize(reader);
					inputHandler.messageReceived(message);
				}
			}
			catch(JSONException | IOException e)
			{
				if(!("Stepping back two steps is not supported".equals(e.getMessage()) || "java.net.SocketException: Connection reset".equals(e.getMessage()))) // Stream closed.
					Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
				JsonDecoder.this.stop();
			}
			finally
			{
				inputHandler.inputHandlerClosed(true);
			}
		}
	}
}
