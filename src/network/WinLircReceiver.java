package network;
import java.io.*;
import java.net.*;
import data.IData;
import basics.Controller;
import basics.Plugin;

public class WinLircReceiver implements Plugin
{
	protected WinLircReceiverThread thread;
	protected BufferedReader reader;
	final protected Controller controller = Controller.getInstance();
	final protected IData data = controller.getData();
	
	@Override
	public String getName()
	{
		return "WinLIRC-Receiver";
	}
	
	@Override
	public String getDescription()
	{
		return "Verbindet sich mit dem Infrarot-Fernsteuerung-Empfänger WinLIRC und verarbeitet empfangene Signale.";
	}

	@Override
	public void start()
	{
		thread = new WinLircReceiverThread();
		thread.start();
	}

	@Override
	public void stop()
	{
		if(isRunning())
			thread.interrupt();
		if(reader != null)
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				controller.logError(Controller.REGULAR_ERROR, this, e, "Socket.getInputStream() konnte nicht geschlossen werden.");
			}
	}
	
	@Override
	public boolean isRunning()
	{
		return thread != null && thread.isAlive();
	}
	
	protected void execute(String[] data)
	{
		
	}
	
	protected class WinLircReceiverThread extends Thread
	{
		WinLircReceiverThread()
		{
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			String ip = data.readSetting("WinLIRC-IP", "127.0.0.1");
		 	int port = Integer.parseInt(data.readSetting("WinLIRC-Port", "8765"));
		 	
		 	try
			{
		 		Socket socket = new Socket(ip,port);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (UnknownHostException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Vehler bei Verbinden zu WinLIRC.");
				return;
			}
			catch (IOException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Vehler bei Verbinden zu WinLIRC.");
				return;
			}
			
			System.out.println("WL");
			StringBuilder sb = new StringBuilder();
			while(!isInterrupted())
			{
				int in;
				try
				{
					in = reader.read();
				}
				catch (IOException e)
				{
					controller.logError(Controller.NORMAL_ERROR, this, e, "Vehler in Verbindung zu WinLIRC.");
					return;
				}
				if(in == -1)
					break;
				
				if(in == '\n')
				{
					String[] data = sb.toString().split(" ");
					if(data.length == 4)
						execute(data);
					sb.setLength(0);
				}
				else
					sb.append((char)in);
			}
		}
	}
}
