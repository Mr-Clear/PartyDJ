package de.klierlinge.partydjremote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public MainActivity() {
        connection = new ClientConnection(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackProgress = (TrackProgressView) findViewById(R.id.track_progress_view);
        trackName = (TextView) findViewById(R.id.track_name);

        mainHandler = new Handler(getMainLooper());

        createButtonListener(R.id.play, Command.Play);
        createButtonListener(R.id.pause, Command.Pause);
        createButtonListener(R.id.previous, Command.Previous);
        createButtonListener(R.id.next, Command.Next);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
        connection.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        connection.connect(p.getString(getString(R.string.PrefPdjHostKey), getString(R.string.PrefPdjHosDefault)));
    }

    @Override
    public void messageReceived(Message message) {
        Log.v(TAG, "Message received: " + message);
        switch (message.getType()) {
            case LiveData:
                final LiveData data = (LiveData) message;
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
        switch (item.getItemId()) {
            case R.id.settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.wake_up:
                // TODO: Make parameters configurable.
                final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
                WakeOnLan.WakeUp(p.getString(getString(R.string.PrefWolIpKey), getString(R.string.PrefWolIpDefault)),
                        p.getString(getString(R.string.PrefWolMacKey), getString(R.string.PrefWolMacDefault)),
                        () -> {
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

    private void send(Message message) {
        try {
            connection.send(message);
        } catch (IOException e) {
            Log.e(TAG, "Failed to send message: " + message, e);
        }
    }

    private void createButtonListener(int id, final Command command) {
        findViewById(id).setOnClickListener(v -> {
            try {
                connection.send(new PdjCommand(command));
            } catch (IOException e) {
                Log.e(TAG, "Failed to send " + command + " command.", e);
            }
        });
    }
}
