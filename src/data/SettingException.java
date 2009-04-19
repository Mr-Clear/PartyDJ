package data;

/**Tritt auf, wenn eine Einstellung nicht geschrieben oder gelesen werden kann.*/
public class SettingException extends RuntimeException
{
	private static final long serialVersionUID = 4289702892751764657L;

	public SettingException(Exception e)
    {
    	super(e);
	}
	
	public SettingException(String msg)
    {
    	super(msg);
	}
	
	public SettingException(String msg, Exception e)
	{
		super(msg, e);
	}
}
