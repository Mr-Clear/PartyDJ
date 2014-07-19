package network.remoteV2;

import java.util.concurrent.Executor;

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
	 * @param externalReason true, if connection was closed.
	 */
	void inputHandlerClosed(boolean externalReason);
	
	Executor getExecutor();
}
