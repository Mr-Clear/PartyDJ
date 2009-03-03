package common;

/**Tritt auf, wenn der Player einen Fehler beim Abspielen auffängt.*/
public class PlayerException extends Exception
{
	private static final long serialVersionUID = 4289702892751764657L;

	public PlayerException(Exception e)
    {
    	super(e);
	}
	
	public PlayerException(String msg)
    {
    	super(msg);
	}
	
	public PlayerException(String msg, Exception e)
	{
		super(msg, e);
	}
}
