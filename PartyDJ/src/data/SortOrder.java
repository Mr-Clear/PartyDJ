package data;

/**
 * Gibt an nach was die Datenbank ihre Ausgabe sortieren soll.
 * 
 * @author Eraser
 */
public enum SortOrder
{
	NONE,
	DEFAULT,
	POSITION,
	MASTERLISTINDEX,
	NAME,
	PATH,
	DURATION,
	SIZE,
	PROBLEM;
	
	public static String[] getStringArray()
	{
		return new String[]{"Unsortiert", 
							"Standard", 
							"Nach Position in der Liste",
							"Nach Index in der Hauptliste",
							"Nach Name",
							"Nach Pfad",
							"Nach Dauer",
							"Nach Dateigröße",
							"Nach Problem"};
	}
	
	public static int sortOrderToArrayIndex(final SortOrder sortOrder)
	{
		switch(sortOrder)
		{
		case NONE:
			return 0;
		case DEFAULT:
			return 1;
		case POSITION:
			return 2;
		case MASTERLISTINDEX:
			return 3;
		case NAME:
			return 4;
		case PATH:
			return 5;
		case DURATION:
			return 6;
		case SIZE:
			return 7;
		case PROBLEM:
			return 8;
		default:
			return 0;
		}
	}
	
	public static SortOrder arrayIndexToSortOrder(final int index)
	{
		switch(index)
		{
		case 1:
			return DEFAULT;
		case 2:
			return POSITION;
		case 3:
			return MASTERLISTINDEX;
		case 4:
			return NAME;
		case 5:
			return PATH;
		case 6:
			return DURATION;
		case 7:
			return SIZE;
		case 8:
			return PROBLEM;
		case 0:
		default:
			return NONE;
		}
	}
	
	public int toArrayIndex()
	{
		return sortOrderToArrayIndex(this);
	}
	
	@Override
	public String toString()
	{
		return getStringArray()[toArrayIndex()];
	}
}
