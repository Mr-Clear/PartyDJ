package common;

/**
 * Bekommt Daten von einer Funktion.
 * <p> Üblicherweise ruft der Reporter eine Funktion auf, und übergibt sich selbst,
 * um die Daten zu bekommen.
 * 
 * @author Eraser
 *
 * @param <T> Typ der Daten die dem Reoprter mitgeteilt werden.
 */
public interface Reporter<T>
{
	/**
	 * Übergibt Daten an den Reporter.
	 * 
	 * @param content Übergebene Daten.
	 * @return Ob die Daten gezählt werden.
	 */
	boolean report(T content);
	/** @return Gibt true zurück, wenn die arbeitende Funktion abbrechen soll.
	 */
	boolean isStopped();
}
