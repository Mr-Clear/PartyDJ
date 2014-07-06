package network.remoteV2.server;

import basics.Controller;

import data.IData;
import data.SettingListener;

import lists.data.ListProvider;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private JsonEncoder jsonEncoder;

	public ServerHandler(Server server, Socket socket) throws IOException
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
		catch(IOException ignore)
		{
			/* Ignore */
		}
	}

	@Override
	public void messageReceived(Message message)
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
	public void inputHandlerClosed(boolean externalReason)
	{
		server.removeServerHandler(this);
	}

	public static void main(String... args) throws InterruptedException
	{
		System.out.println("Server: Start");
		new Server().start();
		Thread.sleep(5000);
		System.out.println("Server: End");
	}

    @Override
    public void settingChanged(String name, String value)
    {
        try
        {
            jsonEncoder.write(new Setting(name, value));
        }
        catch(IOException e)
        {
            Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
        }
    }
    
    void sendData()
    {
        //TODO: Nebenläufig
        final IData data = Controller.getInstance().getData();
        final ListProvider listProvider = Controller.getInstance().getListProvider();
        final Map<String, String> settings = data.readAllSettings();

        List<common.Track> allTracks = listProvider.getMasterList().getValues();
        final List<Track> tracks = new ArrayList<>(allTracks.size());
        for(common.Track track : allTracks)
            tracks.add(new Track(track));
        InitialData initialData = new InitialData(settings, tracks);
        try
        {
            jsonEncoder.write(initialData);
        }
        catch(IOException e)
        {
            Controller.getInstance().logError(Controller.NORMAL_ERROR, e);
        }
    }
}
