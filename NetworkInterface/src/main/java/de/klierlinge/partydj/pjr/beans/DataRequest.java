package de.klierlinge.partydj.pjr.beans;

/** Requests an InitialData answer. */
public class DataRequest extends Message
{
    @Override
    public MessageType getType()
    {
        return MessageType.DataRequest;
    }
}
