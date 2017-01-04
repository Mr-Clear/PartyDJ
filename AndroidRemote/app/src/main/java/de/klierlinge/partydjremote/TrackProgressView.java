package de.klierlinge.partydjremote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrackProgressView extends RelativeLayout {
    private double duration;
    private double position;

    private ProgressBar progressBar;
    private TextView timeLeft;
    private TextView timeTotal;
    private TextView timeRemaining;

    public TrackProgressView(Context context) {
        super(context);
        initializeViews(context);
    }

    public TrackProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public TrackProgressView(Context context,
                       AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.track_progress_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        timeLeft = (TextView) findViewById(R.id.timeLeft);
        timeTotal = (TextView) findViewById(R.id.timeTotal);
        timeRemaining = (TextView) findViewById(R.id.timeRemaining);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @SuppressWarnings("unused")
    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
        progressBar.setMax((int) duration);
        timeTotal.setText(String.valueOf(duration));
        timeRemaining.setText(String.valueOf(duration - position));
    }

    @SuppressWarnings("unused")
    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
        progressBar.setProgress((int) position);
        timeLeft.setText(String.valueOf(position));
        timeRemaining.setText(String.valueOf(duration - position));
    }
}