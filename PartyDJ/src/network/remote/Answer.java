package network.remote;

import java.io.Serializable;

public class Answer implements Serializable
{
	private static final long serialVersionUID = -2132043016792096701L;

	protected final long invocationId;
	protected final Serializable data;
	
	public Answer(final long invocationId, final Serializable data)
	{
		this.invocationId = invocationId;
		this.data = data;
	}
	
	public Serializable getData()
	{
		return data;
	}
	
	public long getInvocationId()
	{
		return invocationId;
	}
	
	@Override
	public String toString()
	{
		return "Answer " + invocationId + ": " + data.toString();
	}
}