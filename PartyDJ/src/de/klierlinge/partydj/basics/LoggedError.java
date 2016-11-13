package de.klierlinge.partydj.basics;

import java.util.Date;

public class LoggedError
{
	private final int priority;
	private final String sender;
	private final String senderType;
	private final Throwable exception;
	private final String message;
	private final Date timestamp;
	
	public LoggedError(final int priority, final Object sender, final Throwable exception, final String message)
	{
		this.priority = priority;
		this.sender = sender.toString();
		this.senderType = sender.getClass().getName();
		this.exception = exception;
		this.message = message;
		this.timestamp = new Date();
	}
	
	
	public int getPriority()
	{
		return priority;
	}



	public String getSender()
	{
		return sender;
	}



	public String getSenderType()
	{
		return senderType;
	}



	public Throwable getException()
	{
		return exception;
	}



	public String getMessage()
	{
		return message;
	}



	public Date getTimestamp()
	{
		return timestamp;
	}



	@Override
	public String toString()
	{
		return message;
	}
}
