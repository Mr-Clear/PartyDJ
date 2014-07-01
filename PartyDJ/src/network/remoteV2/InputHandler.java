package network.remoteV2;

import network.remoteV2.beans.Message;

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
	 */
	void inputHandlerClosed();
}
