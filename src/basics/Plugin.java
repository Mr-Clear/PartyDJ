package basics;

/** Plugins werden von dieser Schnittstelle abgeleitet.
 *  <p>
 *  Das Plugin sollte nicht im Konstruktor, sondern in der Methode start() gestartet werden.
 * 
 *  @author Eraser
 */
public interface Plugin
{
	/** @return Name des Plugins.*/
	String getName();
	/** @return Beschreibung des Plugins. */
	String getDescription();
	/** Startet das Plugin. */
	void start();
	/** Beendet das Plugin und gibt alle Ressourcen frei. */
	void stop();
	/** @return True, wenn das Plugin läuft. */
	boolean isRunning();
}
