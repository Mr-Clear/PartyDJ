package de.klierlinge.partydj.pjr.client;

import de.klierlinge.partydj.pjr.beans.Message;

public interface Client
{
    public void messageReceived(Message message);
    public void connectionOpened();
    public void connectionClosed(boolean externalReason);
}
