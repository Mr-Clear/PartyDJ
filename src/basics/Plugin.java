package basics;

/** Plugins werden von dieser Schnittstelle abgeleitet.
 *  <p>
 *  Das Plugin sollte nicht im Konstruktor, sondern in der Methode start() gestartet werden.
 * 
 *  @author Eraser
 */
public interface Plugin
{
	/** Name des Plugins. */
	String getName();
	/** Beschreibung des Plugins. */
	String getDescription();
	/** Startet das Plugin. */
	void start();
	/** Beendet das Plugin und gibt alle Ressourcen frei. */
	void stop();
	/** Gibt an, ob das Plugin läuft. */
	boolean isRunning();
}
