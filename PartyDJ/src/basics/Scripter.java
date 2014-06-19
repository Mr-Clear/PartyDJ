package basics;

import players.IPlayer;

public class Scripter
{
	protected final Controller controller = Controller.getInstance();
	
	public Scripter()
	{
	}
	
	public void executeCommand(final String command)
	{
		final IPlayer player = controller.getPlayer();
		if(player == null)
		{
			controller.logError(Controller.NORMAL_ERROR, this, null, "Player noch nicht geladen.");
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
			controller.logError(Controller.UNIMPORTANT_ERROR, this, null, "Unbekannter Befehl: " + command);
	}
	
	public static String[] getAvailableCommands()
	{
		return new String[]{"Play", "Stop", "Pause", "PlayPause", "FadeInOut", "PlayNext", "PlayPrevious"};
	}
}
