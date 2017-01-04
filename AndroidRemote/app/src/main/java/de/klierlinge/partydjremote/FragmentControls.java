package de.klierlinge.partydjremote;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.client.ClientConnection;

public class FragmentControls extends Fragment {
    private static final String TAG = "FragmentControls";
    private ClientConnection connection;

    public FragmentControls() {
        // Required empty public constructor
    }

    public void setConnection(final ClientConnection connection)
    {
        this.connection = connection;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createListener(R.id.play, PdjCommand.Command.Play);
        createListener(R.id.pause, PdjCommand.Command.Pause);
        createListener(R.id.previous, PdjCommand.Command.Previous);
        createListener(R.id.next, PdjCommand.Command.Next);
    }

    private void createListener(int id, final PdjCommand.Command command)
    {
        getActivity().findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection != null) {
                    try {
                        connection.send(new PdjCommand(command));
                    } catch (IOException e) {
                        Log.e("TAG", "Failed to send " + command + " command.", e);
                    }
                }
                else {
                    Log.e("TAG", command + " pressed while no connection.");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
