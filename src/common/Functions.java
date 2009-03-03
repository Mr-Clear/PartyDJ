package common;

public class Functions
{
	public static String formatTime(double time)
	{
        long seconds = (long)Math.round(time);
        long hours = seconds / 3600;
        seconds = seconds % 3600;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String output = "";
        if (hours > 0)
        {
            output = Long.toString(hours) + ":";
        }

        if (minutes < 10)
        {
            output += "0";
        }
        output += Long.toString(minutes) + ":";

        if (seconds < 10)
        {
            output += "0";
        }
        output += Long.toString(seconds);
        return output;
	}
	
	public static String getFolder()
	{
		javax.swing.JFileChooser fr = new javax.swing.JFileChooser();
		javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
	    return fw.getDefaultDirectory().toString() + System.getProperty("file.separator") + "PartyDJ";
	}
}
