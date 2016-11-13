package de.klierlinge.partydj.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.players.IPlayer;

public class ClassicUdpReceiver extends Thread
{
	public ClassicUdpReceiver()
	{
		setDaemon(true);
	}
	
	@Override
	public void run()
	{
		try (DatagramSocket socket = new DatagramSocket(19997))
		{
			while (socket.isBound())
			{
				final DatagramPacket p = new DatagramPacket(new byte[100], 100);
				try
				{
					socket.receive(p);
					final byte[] buf = p.getData();
					if (p.getLength() > 2 && buf[0] == 0)
					{
						final Controller controller = Controller.getInstance();
						final IPlayer player = controller.getPlayer();
						final String command = new String(buf, 1, p.getLength() - 1).toLowerCase();
						if ("play".equals(command))
							player.play();
						if ("pause".equals(command))
							player.pause();
						if ("fade".equals(command))
							player.fadeInOut();
						if ("stop".equals(command))
							player.stop();
						if ("zurück".equals(command))
							player.playPrevious();
						if ("szurück".equals(command))
							player.setPosition(player.getPosition() - 5);
						if ("sweiter".equals(command))
							player.setPosition(player.getPosition() + 5);
						if ("weiter".equals(command))
							player.playNext();
						if ("lauter".equals(command))
							player.setVolume(player.getVolume() + 10);
						if ("leiser".equals(command))
							player.setVolume(player.getVolume() - 10);
					}
				}
				catch (final IOException e)
				{
					Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, this, e, "Empfangen von Daten aus dem Netzwerk fehlgeschlagen.");
				}
			}
		}
		catch (final SocketException e)
		{
			Controller.getInstance().logError(Controller.UNIMPORTANT_ERROR, this, e, "Kann UDP-Port 19997 nicht öffnen.");
		}
	}
}
