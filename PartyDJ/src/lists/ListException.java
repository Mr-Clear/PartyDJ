package lists;

/** 
 * Tritt auf wenn beim Zugriff auf eine Liste nicht funktioniert 
 * 
 * @author Eraser
 */
public class ListException extends Exception
{
	private static final long serialVersionUID = -6637833184969535508L;

	public ListException(final Exception e)
    {
    	super(e);
	}
	
	public ListException(final String msg)
    {
    	super(msg);
	}
	
	public ListException(final String msg, final Exception e)
	{
		super(msg, e);
	}
}
