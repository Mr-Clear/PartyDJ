package network.remoteV2.beans;

/** Requests an InitialData answer. */
public class DataRequest extends Message
{
    @Override
    public Child getType()
    {
        return Child.DataRequest;
    }
}
