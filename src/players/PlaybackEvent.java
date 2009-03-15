package players;


/**
 * An event which indicates a <code>Player</code> has performed an 'playback action'
 * @author Paul Stanton (http://wanto.f2o.org/)
 */
public class PlaybackEvent
{
	public static int STOPPED = 1;
	public static int STARTED = 2;

	private AdvancedPlayer source;
	private int frame;
	private int id;

	public PlaybackEvent(AdvancedPlayer source, int id, int frame)
	{
		this.id = id;
		this.source = source;
		this.frame = frame;
	}

	public int getId(){return id;}
	public void setId(int id){this.id = id;}

	public int getFrame(){return frame;}
	public void setFrame(int frame){this.frame = frame;}

	public AdvancedPlayer getSource(){return source;}
	public void setSource(AdvancedPlayer source){this.source = source;}

}

