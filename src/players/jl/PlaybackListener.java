package players.jl;

public interface PlaybackListener
{
	void playbackFinished(AdvancedPlayer source, Reason reason);
	public enum Reason
	{
		END_OF_TRACK,
		RECEIVED_STOP,
		RECEIVED_PAUSE
	}
}