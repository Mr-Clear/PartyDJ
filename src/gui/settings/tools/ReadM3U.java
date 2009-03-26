package gui.settings.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.ListException;
import basics.Controller;
import common.Track;

public class ReadM3U implements StatusSupportedFunction
{
	private final String filePath;
	private boolean goOn = true;

	public ReadM3U(String path)
	{
		filePath = path;
	}


	@Override
	public void runFunction(StatusDialog sd) 
	{
		int count = 0;
		int bytes = 0;
		
		BufferedReader r;
		try
		{
			r = new BufferedReader(new FileReader(filePath));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(sd, "Kann Datei nicht öffnen:\n" + filePath + "\n\n" + e.getMessage(), "Datei einfügen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sd.setBarMaximum((int)new File(filePath).length());
		sd.setLabel("Beginne.");
		while (goOn)
		{
			String line;
			try
			{
				line = r.readLine();
			}
			catch (IOException e1)
			{
				break;
			}
			if(line == null)
				break;
			
			bytes += line.length();
			
			if(line.charAt(0) != '#')
			{
				final String name = line.substring(line.lastIndexOf("\\") + 1, line.lastIndexOf("."));
				sd.setLabel(count + ": " + name);
				try
				{
					final Track newTrack = new Track(-1, line, name, 0, new File(line).length(), Track.Problem.NONE, null);
					Controller.getInstance().getData().addTrack(newTrack);
					if(newTrack.index != -1)
						count++;
				}
				catch (ListException e){}
				sd.setBarPosition(bytes);
			}
		}
			
		sd.setLabel("Fertig.");

		JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}

	public void stopTask()
	{
		goOn = false;
	}
}
