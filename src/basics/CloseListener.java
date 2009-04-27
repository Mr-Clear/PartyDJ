package basics;

/**
 * Klassen die von diesem Interface abgeleitet sind
 * und beim Controller über addCloseListener registriert sind
 * bekommen eine Nachricht bevor der PartyDJ geschlossen wird.
 * 
 * @author Eraser
 * 
 * @see Controller
 */
public interface CloseListener
{
	/**Wird vom Controller aufgerufen, wenn der PartyDJ geschlossen werden soll.*/
	void closing();
}
