package common;

/**
 * Bekommt Daten von einer Funktion.
 * <p> �blicherweise ruft der Reporter eine Funktion auf, und �bergibt sich selbst,
 * um die Daten zu bekommen.
 * 
 * @author Eraser
 *
 * @param <T> Typ der Daten die dem Reoprter mitgeteilt werden.
 */
public interface Reporter<T>
{
	/**
	 * �bergibt Daten an den Reporter.
	 * 
	 * @param content �bergebene Daten.
	 * @return Ob die Daten gez�hlt werden.
	 */
	boolean report(T content);
	/** @return Gibt true zur�ck, wenn die arbeitende Funktion abbrechen soll.
	 */
	boolean isStopped();
}
