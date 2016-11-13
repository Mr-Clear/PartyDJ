package de.klierlinge.partydj.players.jl;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public final class ThreadBufferedInputStream extends FilterInputStream
{
	protected LinkedList<Byte> queue = new LinkedList<>();
	protected boolean closed = false;
	InputThread thread;
	
	public ThreadBufferedInputStream(final InputStream in)
	{
		super(new BufferedInputStream(in));
		thread = new InputThread();
		thread.start();		
	}
	
	@Override
	public int read()
	{
		System.out.println("r");
		while(queue.size() == 0 && !closed)
		{
			try
			{
				Thread.sleep(0);
			}
			catch(final InterruptedException e) { /* ignore */ }
		}
		
		if(closed)
			return -1;
		
		synchronized(queue)
		{
			return queue.poll();
		}
	}
	
	@Override
	public int read(final byte[] b, final int off, final int len)
	{
		while(queue.size() < len || closed)
		{
			try
			{
				Thread.sleep(0);
			}
			catch(final InterruptedException e) { /* ignore */ }
		}
		
		synchronized(queue)
		{
			for(int i = off; i - off < len; i++)
			{
				final Byte polled = queue.poll();
				if(polled == null)
					return i - off;
				b[i] = polled;
			}
		}
		
		return len;
	}
	
	@Override
	public void close() throws IOException
	{
		closed = true;
		super.close();
	}
	
	protected class InputThread extends Thread
	{
		@Override
		public void run()
		{
			while(!closed)
			{
				int red;
				try
				{
					red = in.read();
				}
				catch(final IOException e)
				{
					red = -1;
				}
				if(red == -1)
				{
					closed = true;
					break;
				}
				
				synchronized(queue)
				{
					queue.offer((byte)red);
				}
			}
			
			try
			{
				in.close();
			}
			catch(final IOException ignored){ /* Wenns nicht geht, gehts nicht. */ }
		}
	}
}
