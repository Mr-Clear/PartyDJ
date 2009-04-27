package lists;

/** 
 * Tritt auf wenn beim Zugriff auf eine Liste nicht funktioniert 
 * 
 * @author Eraser
 */
public class ListException extends Exception
{
	private static final long serialVersionUID = -6637833184969535508L;

	public ListException(Exception e)
    {
    	super(e);
	}
	
	public ListException(String msg)
    {
    	super(msg);
	}
	
	public ListException(String msg, Exception e)
	{
		super(msg, e);
	}
}
