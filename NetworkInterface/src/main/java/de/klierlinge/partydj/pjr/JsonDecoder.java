package de.klierlinge.partydj.pjr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Executors;
import de.klierlinge.partydj.pjr.beans.Message;
import flexjson.JSONDeserializer;
import flexjson.JSONException;

/**
 * Dekodiert Daten aus einem Json Stream und gibt Messages aus.
 */
public class JsonDecoder
{
	/** Stream mit Json-Daten. */
	private final InputStream inputStream;
	/** Empfänger der Nachrichten. */
	private final InputHandler inputHandler;
	/** Wenn false, stoppt der decoder-Thread. */
	private volatile boolean running = true;

	/**
	 * @param inputStream Stream, aus dem die Json-Daten kommen.
	 * @param inputHandler Empfänger der Nachrichten.
	 */
	public JsonDecoder(final InputStream inputStream, final InputHandler inputHandler)
	{
		this.inputStream = inputStream;
		this.inputHandler = inputHandler;
		Executors.newSingleThreadExecutor().execute(new Receiver()); // TODO: Use common pool.
	}

	/** Stoppt den ReceiverThread. */
	public void stop()
	{
		running = false;
		try
		{
			inputStream.close();
		}
		catch(final IOException ignore)
		{
			/* Ignore */
		}
	}

	private class Receiver implements Runnable
	{
	    @Override
		public void run()
		{
			final JSONDeserializer<Message> deserializer = new JSONDeserializer<>();
			Message.configureDeserializer(deserializer);
			try(Reader reader = new InputStreamReader(inputStream))
			{
				while(running)
				{
					final Message message = deserializer.deserialize(reader);
					inputHandler.messageReceived(message);
				}
			}
			catch(JSONException | IOException e)
			{
				if(!("Stepping back two steps is not supported".equals(e.getMessage()) || "java.net.SocketException: Connection reset".equals(e.getMessage()))) // Stream closed.
				{
					// TODO: Log me.
					e.printStackTrace();
				}
				JsonDecoder.this.stop();
			}
			finally
			{
				inputHandler.inputHandlerClosed(true);
			}
		}
	}
}
