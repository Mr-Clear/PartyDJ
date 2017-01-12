package de.klierlinge.partydj.pjr.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track.Problem;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.SettingListener;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbClientListModel;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.lists.data.ListProvider;
import de.klierlinge.partydj.pjr.InputHandler;
import de.klierlinge.partydj.pjr.JsonDecoder;
import de.klierlinge.partydj.pjr.JsonEncoder;
import de.klierlinge.partydj.pjr.beans.InitialData;
import de.klierlinge.partydj.pjr.beans.LiveData;
import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.beans.Setting;
import de.klierlinge.partydj.pjr.beans.Track;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.system.LinuxSleep;

public class ServerHandler implements InputHandler, SettingListener
{
	private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);
	private final Server server;
	private final Socket socket;
	private final JsonDecoder jsonDecoder;
	private final JsonEncoder jsonEncoder;
	private final Timer liveDataTimer = new Timer(true);
	final Controller controller;
	
	public ServerHandler(final Server server, final Socket socket) throws IOException
	{
		controller = Controller.getInstance();
		this.server = server;
		this.socket = socket;

		server.addServerHandler(this);
		jsonEncoder = new JsonEncoder(socket.getOutputStream());
		jsonDecoder = new JsonDecoder(socket.getInputStream(), this);
		liveDataTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					jsonEncoder.write(new LiveData(makeTrak(controller.getPlayer().getCurrentTrack()), controller.getPlayer().getPlayState(), controller.getPlayer().getPosition()));
				}
				catch (IOException e)
				{
					log.error("Failed to send live data.", e);
				}
			}
		}, 0, 1000);
	}

	public void stop()
	{
		liveDataTimer.cancel();
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
    		IPlayer p = controller.getPlayer();
        	switch(((PdjCommand)message).commmand)
        	{
        	case Play:
        		p.play();
        		break;
        	case Stop:
        		p.stop();
        		break;
        	case Pause:
        		p.pause();
        		break;
        	case Next:
        		p.playNext();
        		break;
        	case Previous:
        		p.playPrevious();
        		break;
			case FadeIn:
				p.fadeIn();
				break;
			case FadeInOut:
				p.fadeInOut();
				break;
			case FadeOut:
				p.fadeOut();
				break;
			case PlayPause:
				p.playPause();
				break;
			case Start:
				p.start();
				break;
			case Sleep:
				try
				{
					LinuxSleep.sleep();
				}
				catch (IOException | InterruptedException e)
				{
					log.error("Send computer so S3 sleep failed.", e);
				}
				break;
			default:
				log.error("Unknown command: " + ((PdjCommand)message).commmand);
				break;
        	}
            break;
        case Setting:
            break;
        case Test:
            break;
        case TrackList:
            break;
        case InitialData:
		case LiveData:
        case Track:
        	log.warn("Message should not be received by client: " + message);
            break;
		default:
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
        	log.error("Failed to send changed setting.", e);
        }
    }
    
    void sendData()
    {
    	controller.getExecutor().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final IData data = controller.getData();
                    final ListProvider listProvider = controller.getListProvider();
                    final Map<String, String> settings = data.readAllSettings();
    
                    final List<de.klierlinge.partydj.common.Track> allTracks = listProvider.getMasterList().getValues();
                    final List<Track> tracks = new ArrayList<>(allTracks.size());
                    for(final de.klierlinge.partydj.common.Track track : allTracks)
                        tracks.add(makeTrak(track));
                    
                    final Map<String, List<Integer>> lists = new HashMap<>();
                    final List<String> listList = data.getLists();
                    for(final String listName : listList)
                    {
                        final DbClientListModel dbClientListModel = listProvider.getDbList(listName);
                        final List<Integer> list = new ArrayList<>(dbClientListModel.getSize());
                        for(final de.klierlinge.partydj.common.Track track : dbClientListModel.getValues())
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
                catch(final ListException | IOException e)
                {
                	log.error("Failed to establish connection.", e);
                }
            }
        });
    }
    
    private static Track makeTrak(de.klierlinge.partydj.common.Track track)
    {
    	return new Track(track.getName(), track.getInfo(), track.getDuration(), track.getSize(), track.getProblem() != Problem.NONE);
    }
}
