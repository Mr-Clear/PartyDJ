package players.jl;

public interface PlaybackListener
{
	void playbackFinished(AdvancedPlayer source, int reason);
	public enum Reason
	{
		END_OF_TRACK,
		RECEIVED_STOP,
		RECEIVED_PAUSE
	}
}