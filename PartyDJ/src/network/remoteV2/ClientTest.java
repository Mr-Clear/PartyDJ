package network.remoteV2;

import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import network.remoteV2.json.Command;
import network.remoteV2.json.TrackList;


public class ClientTest
{
	public static void main(String... args) throws InterruptedException, IOException
	{
		//System.out.println("Client Start");
		try(Socket socket = new Socket())
		{
			socket.connect(new InetSocketAddress("localhost", Server.PORT));
			
			
			JSONSerializer serializer = new JSONSerializer();
			serializer.exclude("*.class");
			serializer.include("network.remoteV2.json.Message");
			
			Writer writer = new PrintWriter(socket.getOutputStream());
			
			String string = "Track Name";
			int[] array = new int[100];
			Random random = new Random(0);
			for(int i = 0; i < array.length; i++)
				array[i] = random.nextInt(100000);
			TrackList trackList = new TrackList(string, array);
			for(int i = 0; i < 5; i++)
			{
				serializer.serialize(trackList, writer);
				writer.flush();
			    Thread.sleep(1000);
			    if(i == 1)
			    {
					serializer.serialize(new Command("CMD"), writer);
					writer.flush();
				    Thread.sleep(1000);
			    }
			}
		    Thread.sleep(1000);
		}
		//System.out.println("Client End");
	}
}