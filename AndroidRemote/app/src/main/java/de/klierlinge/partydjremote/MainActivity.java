package de.klierlinge.partydjremote;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import de.klierlinge.partydj.pjr.beans.LiveData;
import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.beans.PdjCommand.Command;
import de.klierlinge.partydj.pjr.client.Client;
import de.klierlinge.partydj.pjr.client.ClientConnection;

public class MainActivity extends AppCompatActivity implements Client {
    private static final String TAG = MainActivity.class.getName();

    private final ClientConnection connection;
    private Handler mainHandler;
    private TrackProgressView trackProgress;
    private TextView trackName;

    public MainActivity()
    {
        connection = new ClientConnection(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackProgress = (TrackProgressView) findViewById(R.id.track_progress_view);
        trackName = (TextView) findViewById(R.id.track_name);

        mainHandler = new Handler(getMainLooper());
        connection.connect(getString(R.string.default_host));

        createButtonListener(R.id.play, Command.Play);
        createButtonListener(R.id.pause, Command.Pause);
        createButtonListener(R.id.previous, Command.Previous);
        createButtonListener(R.id.next, Command.Next);
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
                    trackName.setText(data.track.name);
                });
                break;
            default:
                Log.w(TAG, "Unhandled message: " + message);
                break;
        }
    }

    @Override
    public void connectionOpened() {
        Log.i(TAG, "Connection opened");
    }

    @Override
    public void connectionClosed(boolean externalReason) {
        Log.i(TAG, "Connection closed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                break;
            case R.id.wake_up:
                // TODO: Make parameters configurable.
                WakeOnLan.WakeUp("192.168.5.113", "24:5E:BE:05:3F:5A", () -> {
                    Log.i(TAG, "Magic packet sent.");
                    // TODO: Notify user.
                }, (e) -> {
                    Log.e(TAG, "Magic packet not send.", e);
                    // TODO: Notify user.
                });
                break;
            case R.id.sleep:
                Log.i(TAG, "Sending sleep command...");
                send(new PdjCommand(Command.Sleep));
                break;
            default:
                Log.e(TAG, "Unknown menu button pressed: " + item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send(Message message)
    {
        try {
            connection.send(message);
        } catch (IOException e) {
            Log.e(TAG, "Failed to send message: " + message, e);
        }
    }

    private void createButtonListener(int id, final Command command)
    {
        findViewById(id).setOnClickListener(v -> {
            try {
                connection.send(new PdjCommand(command));
            } catch (IOException e) {
                Log.e(TAG, "Failed to send " + command + " command.", e);
            }
        });
    }
}
