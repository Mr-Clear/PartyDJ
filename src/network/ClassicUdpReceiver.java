package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import players.IPlayer;
import basics.Controller;

public class ClassicUdpReceiver extends Thread
{
	public ClassicUdpReceiver()
	{
		setDaemon(true);
	}
	
	public void run()
	{

		DatagramSocket socket;
		try
		{
			socket = new DatagramSocket(19997);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			return;
		}
		
		while(socket.isBound())
		{
			DatagramPacket p = new DatagramPacket(new byte[100], 100);
			try
			{
				socket.receive(p);
				byte[] buf = p.getData();
				if(p.getLength() > 2 && buf[0] == 0)
				{
					Controller controller = Controller.getInstance();
					IPlayer player = controller.getPlayer();
					String command = new String(buf, 1, p.getLength() - 1).toLowerCase();
					if(command.equals("play"))
						player.play();
					if(command.equals("pause"))
						player.pause();
					if(command.equals("fade"))
						player.fadeInOut();
					if(command.equals("stop"))
						player.stop();
					if(command.equals("zurück"))
						player.playPrevious();
					if(command.equals("szurück"))
						player.setPosition(player.getPosition() - 5);
					if(command.equals("sweiter"))
						player.setPosition(player.getPosition() + 5);
					if(command.equals("weiter"))
						player.playNext();
					if(command.equals("lauter"))
						player.setVolume(player.getVolume() + 10);
					if(command.equals("leiser"))
						player.setVolume(player.getVolume() - 10);
				}
			}
			catch (IOException e)
			{
				Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, this, e, null);
			}
		}
	}
}
