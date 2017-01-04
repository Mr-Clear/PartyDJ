package de.klierlinge.partydjremote;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import de.klierlinge.partydj.pjr.beans.LiveData;
import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.client.Client;
import de.klierlinge.partydj.pjr.client.ClientConnection;

public class MainActivity extends AppCompatActivity implements Client {
    private static final String TAG = "MainActivity";

    private ClientConnection connection;
    private Handler mainHandler;
    private TrackProgressView trackProgress;

    public MainActivity()
    {
        connection = new ClientConnection(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackProgress = (TrackProgressView) findViewById(R.id.track_progress_view);

        mainHandler = new Handler(getMainLooper());
        connection.connect(getString(R.string.default_host));
        createButtonListener(R.id.play, PdjCommand.Command.Play);
        createButtonListener(R.id.pause, PdjCommand.Command.Pause);
        createButtonListener(R.id.previous, PdjCommand.Command.Previous);
        createButtonListener(R.id.next, PdjCommand.Command.Next);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void messageReceived(Message message) {
        Log.v(TAG, "Message received: " + message);
        switch (message.getType())
        {
            case LiveData:
                final LiveData data = (LiveData)message;
                mainHandler.post(() -> {
                    trackProgress.setDuration(data.track.duration);
                    trackProgress.setPosition(data.position);
                });
                break;
            default:
                Log.w(TAG, "Unhandled message: " + message);
        }
    }

    @Override
    public void connectionOpened() {
        Log.i(TAG, "Connection opened");
    }

    @Override
    public void connectionClosed(boolean b) {
        Log.i(TAG, "Connection closed");
    }

    private void createButtonListener(int id, final PdjCommand.Command command)
    {
        findViewById(id).setOnClickListener(v -> {
            if(connection != null) {
                try {
                    connection.send(new PdjCommand(command));
                } catch (IOException e) {
                    Log.e(TAG, "Failed to send " + command + " command.", e);
                }
            }
            else {
                Log.e(TAG, command + " pressed while no connection.");
            }
        });
    }
}
