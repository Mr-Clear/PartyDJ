package network.remoteV2;

import flexjson.JSONException;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import network.remoteV2.beans.Message;

/**
 * Schreibt Kind-Klassen von Message als Json-Stream.
 */
public class JsonEncoder
{
	/** Writer, in den geschrieben wird. */
	private final Writer writer;
	private final JSONSerializer jsonSerializer;

	/** 
	 * @param outputStream Ziel, in das Json geschrieben wird. 
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
	public synchronized void write(Message message) throws IOException
	{
	    /* For debug. Set len = 0 to disable. */
	    final int len = 200;
	    if(len > 0)
	    {
	        String string = jsonSerializer.serialize(message);
	        if(string.length() > len)
	            string = string.substring(0, len);
	        System.out.println(string);
	    }
	    
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
