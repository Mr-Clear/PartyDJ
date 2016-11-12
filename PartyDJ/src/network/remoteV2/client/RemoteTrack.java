package network.remoteV2.client;

import data.IData;
import lists.data.DbTrack;
import network.remoteV2.beans.Track;

public class RemoteTrack extends DbTrack
{
    private static final long serialVersionUID = 1175684016432865508L;

    public RemoteTrack(final IData data, final int index, final Track message)
    {
        super(data, index, "", message.name, message.duration, message.size, message.problem, message.info);
    }
}
