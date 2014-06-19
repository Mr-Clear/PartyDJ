package data;

/**
 * Tritt auf, wenn eine Einstellung nicht geschrieben oder gelesen werden kann.
 * 
 * @author Eraser 
 */
public class SettingException extends RuntimeException
{
	private static final long serialVersionUID = 4289702892751764657L;

	public SettingException(final Exception e)
    {
    	super(e);
	}
	
	public SettingException(final String msg)
    {
    	super(msg);
	}
	
	public SettingException(final String msg, final Exception e)
	{
		super(msg, e);
	}
}
