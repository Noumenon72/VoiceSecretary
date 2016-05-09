package com.kovaciny.voicesecretary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mFilename;
    private MediaRecorder mRecorder;
    private RecordButton mRecordButton = null;
    private PlayButton mPlayButton = null;
    private TimePicker mTimePicker = null;
    protected AudioRecordTest mAudioRecord = new AudioRecordTest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.btnSetAlarm);
        if (button != null) {
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor( Color.RED );
                    SetAlarm();
                }
            });
        }

        final Button

        mRecordButton = new RecordButton(this);
        LinearLayout ll = ((LinearLayout) findViewById(R.id.audiorecord));
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
        mp.start();
    }

    public void SetAlarm()
    {
        final Button button = (Button) findViewById(R.id.btnSetAlarm);
        assert button != null;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive( Context context, Intent _ )
            {
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
                mp.start();
                mAudioRecord.onPlay(true);
                context.unregisterReceiver( this ); // this == BroadcastReceiver, not Activity
            }
        };

        this.registerReceiver(receiver, new IntentFilter("com.kovaciny.reminder"));

        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.kovaciny.reminder"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        // set alarm to fire 5 sec (1000*5) from now (SystemClock.elapsedRealtime())

        int hour = mTimePicker.getCurrentHour();
        int minute = mTimePicker.getCurrentMinute();
        Toast.makeText(this, String.format("setting alarm for %1d:%2d", hour, minute), Toast.LENGTH_SHORT)
                .show();
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * 2, pintent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
                mp.start();
                break;
        }
    }

    public class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                try {
                    mAudioRecord.onRecord(mStartRecording);
                } catch (RuntimeException e) {
                    Toast.makeText(getApplicationContext(), "Mic not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                mAudioRecord.onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mAudioRecord.onPause();
    }
}
