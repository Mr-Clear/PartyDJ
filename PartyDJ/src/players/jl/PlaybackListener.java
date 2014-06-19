package players.jl;

/**
 * Nachrichten vom AdvancedPlayer an JLPlayer.
 * 
 * @author Eraser
 * 
 * @see JLPlayer
 * @see AdvancedPlayer
 */
public interface PlaybackListener
{
	void playbackFinished(AdvancedPlayer source, Reason reason);
	public enum Reason
	{
		END_OF_TRACK,
		RECEIVED_STOP,
		RECEIVED_PAUSE,
		ERROR
	}
}