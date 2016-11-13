package de.klierlinge.partydj.pjr;

/**
 * Exception in PartyDJ JSON Remote.
 */
public class PjrException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PjrException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PjrException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PjrException(String message)
	{
		super(message);
	}

	public PjrException(Throwable cause)
	{
		super(cause);
	}
}
