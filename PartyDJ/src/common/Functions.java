package common;

import common.annotation.NonNullByDefault;

@NonNullByDefault

/**
 * Stellt allgemeine Funktionen bereit.
 * 
 * @author Eraser
 */
public final class Functions
{
	private Functions(){}
	
	/**
	 * Formatert eine Zeit im Format 00:00.
	 * 
	 * @param time Die zu formatierende Zeit in Sekunden.
	 * @return Die Zeit als String formatiert.
	 */
	public static String formatTime(final double time)
	{
        long seconds = Math.round(time);
        final long hours = seconds / 3600;
        seconds = seconds % 3600;
        final long minutes = seconds / 60;
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
	
	/**Wandelt eine Zahl in eine Formatierte Dateigößenangabe um.
	 * <br>Gibt Größe mit 3 Dezimalziffern aus.
	 * 
	 * @param size Dateigröße als Zahl.
	 * @return Formatierte Dateigröße.
	 */
	public static String formatSize(final long size)
	{
		return formatSize(size, 3, false);
	}
	
	/**Wandelt eine Zahl in eine Formatierte Dateigößenangabe um.
	 * 
	 * @param size Dateigröße als Zahl.
	 * @param digits Anzahl der Dezimalziffern.
	 * @return Formatierte Dateigröße.
	 */
	public static String formatSize(final long size, final int digits)
	{
		return formatSize(size, digits, false);
	}

	/**Wandelt eine Zahl in eine Formatierte Dateigößenangabe um.
	 * 
	 * @param size Dateigröße als Zahl.
	 * @param digits Anzahl der Dezimalziffern.
	 * @param full true: Die Größe in Byte wird zusätzlich in Klammern angezeigt.
	 * @return Formatierte Dateigröße.
	 */
	public static String formatSize(final long size, final int digits, final boolean full)
	{
		final StringBuilder sb = new StringBuilder();
		
		final String[] prefix = {"Byte", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
		int magnitude;
		if(size > 0)
			magnitude = (int)(Math.log10(size) / 3.010299956639812); // Log(size, 1024)
		else
			magnitude = 0;
		
		final double quotient = size / Math.pow(1024, magnitude);
		final int pre = (int) Math.log10(quotient);
		int decimals = digits - pre;
		if(decimals < 0)
			decimals = 0;
		final double factor = Math.pow(10, decimals - 1);
		sb.append(Math.round(quotient * factor) / factor);
		sb.append(" " + prefix[magnitude]);
		
		if(full && magnitude > 0)
			sb.append(" (" + Long.toString(size) + " Byte)");
		
		final String ret = sb.toString();
		if(ret != null)
			return ret;
		return "0";
	}

	/**
	 * Gibt den Ordner zurück in dem der PartyDJ arbeitet.
	 * Ist normalerweise nicht das Verzeichnis in dem der PartyDJ selbst steht. 
	 * @return Ordner, in dem der PartyDJ arbeitet.
	 */
	public static String getFolder()
	{
		final javax.swing.JFileChooser fr = new javax.swing.JFileChooser();
		final javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
	    return fw.getDefaultDirectory().toString() + System.getProperty("file.separator") + "PartyDJ";
	}
	
	/**
	 * Gibt einen Dateipfad zurück, der im Arbeitsverzeichnis des PartyDJ liegt.
	 * @param fileName Name der Datei.
	 * @return Dateipfad, der im Arbeitsverzeichnis des PartyDJ liegt.
	 */
	public static String getFolder(final String fileName)
	{
		String folder = getFolder();
		if(!(folder.endsWith("\\") || folder.endsWith("/")))
			folder += System.getProperty("file.separator");
	    return folder + fileName;
	}
}
