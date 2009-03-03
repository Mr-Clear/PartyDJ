package data;

public class OpenDbException extends Exception
{
	private static final long serialVersionUID = -8684379581639704347L;

	public OpenDbException(String msg)
    {
    	super(msg);
	}

	public OpenDbException(Exception e)
    {
		super(e);
	}
	
	public OpenDbException(String msg, Exception e)
    {
		super(msg, e);
	}
}