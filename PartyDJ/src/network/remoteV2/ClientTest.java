package network.remoteV2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import network.remoteV2.json.TrackList;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class ClientTest
{
	public static void main(String... args) throws JsonGenerationException, JsonMappingException, IOException, InterruptedException
	{
		//System.out.println("Client Start");
		try(Socket socket = new Socket())
		{
			socket.connect(new InetSocketAddress("localhost", Server.PORT));
			
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer();
			mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
			String string = "Jackson uses the third approach, exposing a logical cursor as \"JsonParser\" object. This choice was done by choosing combination of convenience and efficiency (other choices would offer one but not both of these). The entity used as cursor is named \"parser\" (instead of something like \"reader\") to closely align with the Json specification; the same principle is followed by the rest of API (so structured set of key/value fields is called \"Object\", and a sequence of values \"Array\" -- alternate names might make sense, but it seemed like a good idea to try to be compatible with the data format specification first!).";
			int[] array = new int[100];
			Random random = new Random(0);
			for(int i = 0; i < array.length; i++)
				array[i] = random.nextInt(100000);
			TrackList trackList = new TrackList(string, array);
			for(int i = 0; i < 5; i++)
			{
				writer.writeValue(socket.getOutputStream(), trackList);
			    Thread.sleep(1000);
			}
		    Thread.sleep(1000);
		}
		//System.out.println("Client End");
	}
}