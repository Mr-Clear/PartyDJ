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
					if(command.equals("zur�ck"))
						player.playPrevious();
					if(command.equals("szur�ck"))
						player.setPosition(player.getPosition() - 5);
					if(command.equals("sweiter"))
						player.setPosition(player.getPosition() + 5);
					if(command.equals("weiter"))
						player.playNext();
				}
			}
			catch (IOException e)
			{
				System.err.println("ClassicUdpReceiver");
				e.printStackTrace();
			}
		}
	}
}
