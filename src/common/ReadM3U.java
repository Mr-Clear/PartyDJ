package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import gui.StatusDialog;

//TODO Mehrere Dateiformate.
//TODO Geht auch ohne Status.

/**
 * Liest M3U-Dateien.
 * <p>Die gefundenen Tracks werden an ein Objekt vom Typ Reporter<Track> gesendet.
 * <p>Der Status wird auf einem StatusDialog ausgegeben.
 * 
 * @author Eraser
 * 
 * @see Reporter
 * @see StatusDialog
 */
public class ReadM3U
{
	public static int readM3U(String filePath, Reporter<Track> rep, StatusDialog sd)
	{
		return readM3U(filePath, rep, sd, false, true);
	}
	/**
	 * 
	 * @param filePath
	 * @param rep
	 * @param sd
	 * @param getInfos
	 * @param onlyMasterList  true, wenn nur in die Hauptliste eingefügt wird. false, wenn in ein EditableListModel und die
	 * 						  Hauptliste eingefügt wird.
	 * @return
	 */
	public static int readM3U(String filePath, Reporter<Track> rep, StatusDialog sd, boolean getInfos, boolean onlyMasterList)
	{
		int count = 0;
		int bytes = 0;

		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(filePath));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(sd, "Kann Datei nicht öffnen:\n" + filePath + "\n\n" + e.getMessage(), "M3U Lesen", JOptionPane.ERROR_MESSAGE);
			return 0;
		}

		sd.setBarMaximum((int)new File(filePath).length());
		sd.setLabel("Beginne.");
		while (!rep.isStopped())
		{
			String line;
			try
			{
				line = reader.readLine();
			}
			catch (IOException ignored)
			{
				break;
			}
			if(line == null)
				break;
			
			bytes += line.length();
			line = line.trim();
			
			if(line.length() > 0 && line.charAt(0) != '#')
			{
				final String name = line.substring(line.lastIndexOf("\\") + 1, line.lastIndexOf("."));
				sd.setLabel(count + ": " + name);
				boolean counted = rep.report(new Track(line, getInfos));
				if(counted  || !onlyMasterList)
					count++;
				sd.setBarPosition(bytes);
			}
		}
			
		sd.setLabel("Fertig.");

		return count;
	}
}
