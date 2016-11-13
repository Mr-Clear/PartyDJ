package de.klierlinge.partydj.pjr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import de.klierlinge.partydj.pjr.beans.Message;
import flexjson.JSONException;
import flexjson.JSONSerializer;

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
	public JsonEncoder(final OutputStream outputStream)
	{
		writer = new PrintWriter(outputStream);
		jsonSerializer = new JSONSerializer();
		jsonSerializer.exclude("*.class");
	}
	
	/**
	 * @param message Zu schreibende Nachricht.
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
    public synchronized void write(final Message message) throws IOException
	{
	    /* For debug. Set len = 0 to disable. */
	    final int len = 0;
	    if(len > 0)
	    {
	        String string = jsonSerializer.deepSerialize(message);
	        System.out.print(string.length() + "\t");
	        if(string.length() > len)
	            string = string.substring(0, len);
	        System.out.println(string);
	    }
	    
	    try
		{
		    jsonSerializer.deepSerialize(message, writer);
		}
		catch(final JSONException e)
		{
			throw new IOException(e);
		}
		writer.flush();
	}
}
