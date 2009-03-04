package common;
import common.Track.Problem;

/**Tritt auf, wenn der Player einen Fehler beim Abspielen auffängt.*/
public class PlayerException extends Exception
{
	private static final long serialVersionUID = 4289702892751764657L;
	
	public Problem problem;

	public PlayerException(Problem problem, Exception e)
    {
    	super(e);
    	this.problem = problem;
	}
	
	public PlayerException(Problem problem, String msg)
    {
    	super(msg);
    	this.problem = problem;
	}
	
	public PlayerException(Problem problem, String msg, Exception e)
	{
		super(msg, e);
    	this.problem = problem;
	}
}
