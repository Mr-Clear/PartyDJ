package de.klierlinge.partydjremote;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.client.Client;
import de.klierlinge.partydj.pjr.client.ClientConnection;

public class MainActivity extends AppCompatActivity implements Client {
    private static final String TAG = "MainActivity";

    private ClientConnection connection;
    private FragmentControls fragmentControls;

    public MainActivity()
    {
        connection = new ClientConnection(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connection.connect(getString(R.string.default_host));
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        fragmentControls = (FragmentControls) getSupportFragmentManager().findFragmentById(R.id.controls);
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof  FragmentControls) {
            fragmentControls = (FragmentControls) fragment;
            fragmentControls.setConnection(connection);
        }
    }

    @Override
    public void messageReceived(Message message) {

    }

    @Override
    public void connectionOpened() {
        Log.i(TAG, "Connection opened");
    }

    @Override
    public void connectionClosed(boolean b) {
        Log.i(TAG, "Connection closed");
    }
}
