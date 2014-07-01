package network.remoteV2;

import flexjson.JSONException;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import network.remoteV2.beans.Message;

/**
 * Schreibt Kind-Klassen von Message als Json-Steram.
 */
public class JsonEncoder
{
	/** Writer, in den geschrieben wird. */
	private final Writer writer;
	private final JSONSerializer jsonSerializer;

	/** 
	 * @param outputStream Ziel, in das Json geschriben wird. 
	 */
	public JsonEncoder(OutputStream outputStream)
	{
		writer = new PrintWriter(outputStream);
		jsonSerializer = new JSONSerializer();
		jsonSerializer.exclude("*.class");
	}
	
	/**
	 * @param message Zu schreibende Nachricht.
	 * @throws IOException
	 */
	public void write(Message message) throws IOException
	{
		try
		{
			jsonSerializer.serialize(message, writer);
		}
		catch(JSONException e)
		{
			throw new IOException(e);
		}
		writer.flush();
	}
}
