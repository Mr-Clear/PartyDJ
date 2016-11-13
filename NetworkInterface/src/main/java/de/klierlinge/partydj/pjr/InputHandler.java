package de.klierlinge.partydj.pjr;

import de.klierlinge.partydj.pjr.beans.Message;

/**
 * Empfängt Nachrichten aus einer Netzwerkverbindung.
 */
public interface InputHandler
{
	/**
	 * @param message Empfangene Nachricht.
	 */
	void messageReceived(Message message);
	
	/**
	 * InputHandler is closed.
	 * @param externalReason true, if connection was closed.
	 */
	void inputHandlerClosed(boolean externalReason);
}
