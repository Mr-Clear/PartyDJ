package network.remote;

import basics.CloseListener;
import basics.Controller;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;

public abstract class NetworkInterface implements CloseListener
{
	protected final ObjectOutputStream oos;
	protected final Map<Long, Thread> invocationThreads;
	protected final Map<Long, Serializable> invocationAnswers;
	protected boolean closed;
	protected long nextInvocationId = new Random().nextLong();
	
	public NetworkInterface(final ObjectOutputStream oos, final Map<Long, Thread> invocationThreads, Map<Long, Serializable> invocationAnswers)
	{
		this.oos = oos;
		this.invocationThreads = invocationThreads;
		this.invocationAnswers = invocationAnswers;
		Controller.getInstance().addCloseListener(this);
	}
	
	protected void sendInvocation(Serializable data)
	{
		if(!closed)
			synchronized(oos)
			{
				try
				{
					oos.writeObject(data);
				}
				catch(IOException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Anfrage konnte nicht über Netzwerk gesendet werden.");
				}
			}
	}
	
	protected Serializable waitForAnswer(final long invocationId)
	{
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException ignored)
			{ /* Wird geweckt wenn eine Antwort kommt. */ }
		
		synchronized(invocationAnswers)
		{
			return invocationAnswers.remove(invocationId);
		}
	}
	
	protected long generateInvocationId()
	{
		return nextInvocationId++;
	}
	
	@Override
	public void closing()
	{
		closed = true;
	}
}
