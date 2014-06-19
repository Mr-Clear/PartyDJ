package data;

/**
 * Tritt auf, wenn kein Zugang zur Datenbank hergestellt werden kann.
 * 
 * @author Eraser
 */
public class OpenDbException extends Exception
{
	private static final long serialVersionUID = -8684379581639704347L;

	public OpenDbException(final String msg)
    {
    	super(msg);
	}

	public OpenDbException(final Exception e)
    {
		super(e);
	}
	
	public OpenDbException(final String msg, final Exception e)
    {
		super(msg, e);
	}
}
