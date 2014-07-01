package network.remoteV2.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import network.remoteV2.JsonEncoder;
import network.remoteV2.beans.PdjCommand;
import network.remoteV2.beans.PdjCommand.Command;
import network.remoteV2.beans.TrackList;
import network.remoteV2.server.Server;


public class ClientTest
{
	public static void main(String... args) throws InterruptedException, IOException
	{
		try(Socket socket = new Socket())
		{
			socket.connect(new InetSocketAddress("localhost", Server.PORT));
			
			JsonEncoder jsonEncoder = new JsonEncoder(socket.getOutputStream());
			
			String string = "Track Name";
			int[] array = new int[100];
			Random random = new Random(0);
			for(int i = 0; i < array.length; i++)
				array[i] = random.nextInt(100000);
			TrackList trackList = new TrackList(string, array);
			for(int i = 0; i < 3; i++)
			{
				jsonEncoder.write(trackList);
			    Thread.sleep(1000);
			    if(i == 1)
			    {
					jsonEncoder.write(new PdjCommand(Command.Play));
				    Thread.sleep(1000);
			    }
			}
		    Thread.sleep(1000);
		}
	}
}