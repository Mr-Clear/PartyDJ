package de.klierlinge.partydj.basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.players.IPlayer;

public class Scripter
{
	private static final Logger log = LoggerFactory.getLogger(Scripter.class);
	protected final Controller controller = Controller.getInstance();
	
	public Scripter()
	{
	}
	
	public void executeCommand(final String command)
	{
		final IPlayer player = controller.getPlayer();
		if(player == null)
		{
			log.error("Player noch nicht geladen.");
			return;
		}
			
		if(command.equalsIgnoreCase("Play"))
			player.play();
		else if(command.equalsIgnoreCase("Stop"))
			player.stop();
		else if(command.equalsIgnoreCase("Pause"))
			player.pause();
		else if(command.equalsIgnoreCase("PlayPause"))
			player.playPause();
		else if(command.equalsIgnoreCase("FadeInOut"))
			player.fadeInOut();
		else if(command.equalsIgnoreCase("PlayNext"))
			player.playNext();
		else if(command.equalsIgnoreCase("PlayPrevious"))
			player.playPrevious();
		else
			log.error("Unbekannter Befehl: " + command);
	}
	
	public static String[] getAvailableCommands()
	{
		return new String[]{"Play", "Stop", "Pause", "PlayPause", "FadeInOut", "PlayNext", "PlayPrevious"};
	}
}
