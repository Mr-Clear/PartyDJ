package basics;

import players.IPlayer;

public class Scripter
{
	protected final Controller controller = Controller.getInstance();
	protected final IPlayer player = controller.getPlayer();
	
	protected Scripter()
	{
	}
	
	public void executeCommand(String command)
	{
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
	
	public String[] getAvailableCommands()
	{
		return new String[]{"Play", "Stop", "Pause", "PlayPause", "FadeInOut", "PlayNext", "PlayPrevious"};
	}
}
