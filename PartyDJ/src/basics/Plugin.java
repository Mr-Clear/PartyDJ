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
	/** Wird bei Start des PartyDJ aufgerufen. */
	void initialise();
	/** Das Plugin soll die Arbeit aufnahemen. */
	void start();
	/** Das Plugin soll die Arbeit beenden. */
	void stop();
	/** @return True, wenn das Plugin läuft. */
	boolean isRunning();
}
