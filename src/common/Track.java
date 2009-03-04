package common;

public class Track
{
	public Track(int index, String path, String name, double duration, long size, Problem problem, String info)
	{
		this.index = index;
		this.path = path;
		this.name = name;
		this.duration = duration;
		this.size = size;
		this.problem = problem;
		this.info = info;
	}
	
	/** Index in der Hauptliste */
	public int index;
	/** Absoluter Pfad der Datei */
	public String path;
	/** Angezeigter Name */
	public String name;
	/** Dauer des Tracks */
	public double duration;
	/** Größe der Datei */
	public long size;
	/** Bekannte Probleme mit dem Track */
	public Problem problem;
	/** Beliebigen Info über den Track */
	public String info;
	
	public String toString()
	{
		return name;
	}
	
	/**Stellt ein Problem mit einem Track dar.
	 * 
	 * @author Eraser
	 */
	public static enum Problem
	{
		NONE,
		FILE_NOT_FOUND,
		CANT_PLAY,
		OTHER;
		
		public static String[] getStringArray()
		{
			return new String[]{"Kein Problem", 
								"Datei nicht gefunden", 
								"Datei kann nicht abgespielt werden",
								"Unbestimmtes Problem"};
		}
		
		public static int problemToArrayIndex(Problem problem)
		{
			switch(problem)
			{
			case NONE:
				return 0;
			case FILE_NOT_FOUND:
				return 1;
			case CANT_PLAY:
				return 2;
			case OTHER:
			default:
				return 3;
			}
		}
		
		public static Problem arrayIndexToProblem(int index)
		{
			switch(index)
			{
			case 0:
				return NONE;
			case 1:
				return FILE_NOT_FOUND;
			case 2:
				return CANT_PLAY;
			case 3:
			default:
				return OTHER;
			}
		}
	}
	
	/**Wählt ein Element aus dem Track aus.
	 * 
	 * @author Eraser
	 */
	public enum TrackElement
	{
		PATH,
		NAME,
		DURATION,
		SIZE,
		PROBLEM,
		INFO;
	}
}


