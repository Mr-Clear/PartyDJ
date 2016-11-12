package network.remoteV2.server;

import basics.Controller;
import data.IData;
import data.SettingListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lists.ListException;
import lists.data.DbClientListModel;
import lists.data.DbTrack;
import lists.data.ListProvider;
import network.remoteV2.InputHandler;
import network.remoteV2.JsonDecoder;
import network.remoteV2.JsonEncoder;
import network.remoteV2.beans.InitialData;
import network.remoteV2.beans.Message;
import network.remoteV2.beans.Setting;
import network.remoteV2.beans.Track;

public class ServerHandler implements InputHandler, SettingListener
{
	private final Server server;
	private final Socket socket;
	private final JsonDecoder jsonDecoder;
	private final JsonEncoder jsonEncoder;

	public ServerHandler(final Server server, final Socket socket) throws IOException
	{
		this.server = server;
		this.socket = socket;

		server.addServerHandler(this);
		jsonEncoder = new JsonEncoder(socket.getOutputStream());
		jsonDecoder = new JsonDecoder(socket.getInputStream(), this);
	}

	public void stop()
	{
		jsonDecoder.stop();
		try
		{
			socket.close();
		}
		catch(final IOException ignore)
		{
			/* Ignore */
		}
	}

	@Override
	public void messageReceived(final Message message)
	{
		switch(message.getType())
		{
        case DataRequest:
            sendData();
            break;
        case PdjCommand:
            break;
        case Setting:
            break;
        case Test:
            break;
        case TrackList:
            break;
        case InitialData:
            /* Only for client. */
            Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Should not be received by Server: " + message);
            break;
        case Track:
            /* No stand alone. */
            Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Should not be received by client: " + message);
            break;
		}
	}

	@Override
	public void inputHandlerClosed(final boolean externalReason)
	{
		server.removeServerHandler(this);
	}

	public static void main(final String... args) throws InterruptedException
	{
		System.out.println("Server: Start");
		new Server().start();
		Thread.sleep(5000);
		System.out.println("Server: End");
	}

    @Override
    public void settingChanged(final String name, final String value)
    {
        try
        {
            jsonEncoder.write(new Setting(name, value));
        }
        catch(final IOException e)
        {
            Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
        }
    }
    
    void sendData()
    {
        Controller.getInstance().getExecutor().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final IData data = Controller.getInstance().getData();
                    final ListProvider listProvider = Controller.getInstance().getListProvider();
                    final Map<String, String> settings = data.readAllSettings();
    
                    final List<common.Track> allTracks = listProvider.getMasterList().getValues();
                    final List<Track> tracks = new ArrayList<>(allTracks.size());
                    for(final common.Track track : allTracks)
                        tracks.add(new Track(track));
                    
                    final Map<String, List<Integer>> lists = new HashMap<>();
                    final List<String> listList = data.getLists();
                    for(final String listName : listList)
                    {
                        final DbClientListModel dbClientListModel = listProvider.getDbList(listName);
                        final List<Integer> list = new ArrayList<>(dbClientListModel.getSize());
                        for(final common.Track track : dbClientListModel.getValues())
                        {
                            if(track instanceof DbTrack)
                            {
                                final DbTrack dbTrack = (DbTrack)track;
                                list.add(dbTrack.getIndex());
                            }
                        }
                        lists.put(listName, list);
                    }
                    
                    final InitialData initialData = new InitialData(settings, tracks, lists);
                    jsonEncoder.write(initialData);
                }
                catch(final ListException e)
                {
                    Controller.getInstance().logError(Controller.NORMAL_ERROR, ServerHandler.this, e);
                }
                catch(final IOException e)
                {
                    Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
                }
            }
        });
    }
}
