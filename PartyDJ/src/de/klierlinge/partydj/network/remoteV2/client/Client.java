package de.klierlinge.partydj.network.remoteV2.client;

import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.SplashWindow;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.ListProvider;
import de.klierlinge.partydj.network.remoteV2.JsonEncoder;
import de.klierlinge.partydj.network.remoteV2.beans.DataRequest;
import de.klierlinge.partydj.network.remoteV2.beans.InitialData;
import de.klierlinge.partydj.network.remoteV2.beans.Message;
import de.klierlinge.partydj.network.remoteV2.beans.Setting;
import de.klierlinge.partydj.network.remoteV2.beans.Test;
import de.klierlinge.partydj.players.IPlayer;

public class Client extends Controller
{
    final ClientData data;
    final ClientPlayer player;
    final ClientConnection clientConnection;
    JsonEncoder jsonEncoder;

    protected Client(final String[] args)
    {
        super(args);
        
        /* Splash Window laden. */
        final SplashWindow splash = new SplashWindow();
        
        data = new ClientData(this);
        player = new ClientPlayer();
        try
        {
            listProvider = new ListProvider();
        }
        catch(final ListException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        /* closeListenThread starten um Schließen-Ereignisse abzufangen. */
        closeListenThread = new Thread()
        {
            @Override public void run()
            {
                closePartyDJ();
            }
        };
        Runtime.getRuntime().addShutdownHook(closeListenThread);

        /* Open connection. */
        splash.setInfo("Öffne Verbindung");
        clientConnection = new ClientConnection(this);
        clientConnection.connect(); //TODO: Hostname

        splash.setInfo("Lade Look And Feel");
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
        }
        
        splash.setInfo("Lade Fenster");
        splash.setOpacity(1);
        {
            loadWindow(de.klierlinge.partydj.gui.ClassicWindow.class.getName());
        }

        splash.close();
    }

    @Override
    public IData getData()
    {
        return data;
    }

    @Override
    public IPlayer getPlayer()
    {
        return player;
    }
    
    synchronized void setJsonEncoder(final JsonEncoder jsonEncoder)
    {
        this.jsonEncoder = jsonEncoder;
        if(jsonEncoder == null)
        {
            data.connectionClosed();
        }
        else
        {
            try
            {
                send(new DataRequest());
            }
            catch(final IOException e)
            {
                logError(IMPORTANT_ERROR, e);
            }
        }
    }
    
    synchronized void send(final Message message) throws IOException
    {
        if(jsonEncoder != null)
        {
            jsonEncoder.write(message);
        }
    }
    

    public void messageReceived(final Message message)
    {
        try
        {
            switch(message.getType())
            {
            case Setting:
                data.updateSetting((Setting)message);
                break;
            case Test:
                if(((Test)message).echo)
                    send(new Test(false, ((Test)message).content));
                break;
            case TrackList:
                break;
            case InitialData:
                data.initialData((InitialData)message);
                break;
            case PdjCommand:
            case DataRequest:
                /* Only for server. */
                Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Should not be received by client: " + message);
                break;
            case Track:
                /* No stand alone. */
                Controller.getInstance().logError(Controller.INERESTING_INFO, this, null, "Should not be received by client: " + message);
                break;
            }
        }
        catch(final IOException e)
        {
            logError(IMPORTANT_ERROR, this, e);
        }
        
    }

    public static void main(final String[] args)
    {
        new Client(args);
    }
}
