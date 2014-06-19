package players;
import common.Track.Problem;

/**
 * Tritt auf, wenn der Player einen Fehler beim Abspielen auffängt.
 * 
 * @author Eraser
 */
public class PlayerException extends Exception
{
	private static final long serialVersionUID = 4289702892751764657L;
	
	private final Problem problem;

	public PlayerException(final Problem problem, final Exception e)
    {
    	super(e);
    	this.problem = problem;
	}
	
	public PlayerException(final Problem problem, final String msg)
    {
    	super(msg);
    	this.problem = problem;
	}
	
	public PlayerException(final Problem problem, final String msg, final Exception e)
	{
		super(msg, e);
    	this.problem = problem;
	}

	public Problem getProblem()
	{
		return problem;
	}
}
