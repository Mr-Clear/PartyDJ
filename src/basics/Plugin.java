package basics;

/** Plugins werden von dieser Schnittstelle abgeleitet
 * 
 * @author Eraser
 */
public interface Plugin
{
	/** Name des Plugins */
	String getName();
	/** Beschreibung des Plugins */
	String getDescription();
	/** Startet das Plugin */
	void start();
	/** Beendet das Plugin */
	void stop();
}
