package gui.settings.tools;

import basics.Controller;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import players.PlayerException;
import common.Track;

public class ReadDuration implements StatusSupportedFunction
{
	private boolean goOn = true;
	private final Track[] list;
	
	public ReadDuration(Track[] list)
	{
		this.list = list;
	}
	
	public void runFunction(StatusDialog status)
	{
		/*java.io.PrintWriter pw = null;
		try
		{
			 pw = new java.io.PrintWriter(new java.io.FileOutputStream("C:/Users/Eraser/Desktop/ReadDuration.csv"));
		}
		catch (java.io.FileNotFoundException e)
		{
			e.printStackTrace();
		}
		pw.println("Pfad;Lieddauer;Dateigröße;Lesedauer");*/
		
		int count = 0;
	
		if(status != null)
			status.setBarMaximum(list.length);
		
		for(Track track : list)
		{
			if(!goOn)
				break;
			
			if(status != null)
			{
				status.setLabel(track.name);
			}

			try
			{					
				//long time = System.nanoTime();
				Controller.getInstance().getPlayer().getDuration(track);
				//pw.println("\"" + track.path + "\";" + Double.toString(track.duration).replace('.', ',') + ";" + new File(track.path).length() + ";" + (System.nanoTime() - time));
			}
			catch (PlayerException e){}
			
			if(status != null)
			{
				count++;
				status.setBarPosition(count);
			}
		}
		/*pw.flush();
		pw.close();*/
	}

	public void stopTask()
	{
		goOn = false;
	}
}