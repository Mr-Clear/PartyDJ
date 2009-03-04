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
	
	public static String formatSize(long size)
	{
		return formatSize(size, 3, false);
	}
	
	public static String formatSize(long size, int digits)
	{
		return formatSize(size, digits, false);
	}

	public static String formatSize(long size, int digits, boolean full)
	{
		StringBuilder sb = new StringBuilder();
		
		String[] prefix = {"Byte", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
		int magnitude;
		if(size > 0)
			magnitude = (int)(Math.log10(size) / 3.010299956639812);
		else
			magnitude = 0;
		
		double quotient = size / Math.pow(1024, magnitude);
		int pre = (int) Math.log10(quotient);
		int decimals = digits - pre;
		if(decimals < 0)
			decimals = 0;
		double factor = Math.pow(10, decimals - 1);
		sb.append(Math.round(quotient * factor) / factor);
		sb.append(" " + prefix[magnitude]);
		
		if(full && magnitude > 0)
			sb.append(" (" + Long.toString(size) + " Byte)");
		
		return sb.toString(); 
	}

	
	public static String getFolder()
	{
		javax.swing.JFileChooser fr = new javax.swing.JFileChooser();
		javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
	    return fw.getDefaultDirectory().toString() + System.getProperty("file.separator") + "PartyDJ";
	}
}
