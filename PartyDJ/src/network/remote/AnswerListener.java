package network.remote;

/**
 * Empfängt Antworten die der PartyDJ über Netzwerk sendet.
 * 
 * @author Eraser
 */
public interface AnswerListener
{
	/**
	 * Wird ausgelöst wenn ServerListener eine Antwort empfängt.
	 * @param answer Empfangene Antwort.
	 */
	void answerArrived(final Answer answer);
}
