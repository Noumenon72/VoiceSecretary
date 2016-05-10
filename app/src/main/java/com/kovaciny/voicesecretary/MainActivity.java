package com.kovaciny.voicesecretary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mFilename;
    private MediaRecorder mRecorder;
    private RecordButton mRecordButton = null;
    private PlayButton mPlayButton = null;
    private Button mAlarmListButton;
    private TimePicker mTimePicker = null;
    protected AudioRecordTest mAudioRecord = new AudioRecordTest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int appFlags = this.getApplicationInfo().flags;
        final boolean DEBUG = (appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.btnSetAlarm);
        if (button != null) {
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setOnClickListener(this);
        }

        mAlarmListButton = (Button) findViewById(R.id.btnViewAlarms);
        if (mAlarmListButton != null) {
            mAlarmListButton.setOnClickListener(this);
        }

        mRecordButton = new RecordButton(this);
        mPlayButton = new PlayButton(this);
        LinearLayout ll = ((LinearLayout) findViewById(R.id.audiorecord));
        if (ll != null) {
            ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
            ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        }

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);

        if (DEBUG) {
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
            mp.start();
        }
    }

    public void SetAlarm()
    {
        final Button button = (Button) findViewById(R.id.btnSetAlarm);
        if (button != null) {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent _) {
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
                    mp.start();
                    mAudioRecord.onPlay(true);
                    context.unregisterReceiver(this); // this == BroadcastReceiver, not Activity
                }
            };
            this.registerReceiver(receiver, new IntentFilter("com.kovaciny.reminder"));
        }

        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.kovaciny.reminder"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        int hour, minute;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            hour = mTimePicker.getCurrentHour();
            minute = mTimePicker.getCurrentMinute();
        } else {
            hour = mTimePicker.getHour();
            minute = mTimePicker.getMinute();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long triggerAtMillis = calendar.getTimeInMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a EEEE, MMMM d");
        String formattedDateString = formatter.format(calendar.getTime());

        Toast.makeText(this, "setting alarm for " + formattedDateString, Toast.LENGTH_LONG)
                .show();
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pintent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videorecord);
                mp.start();
                break;
            case R.id.btnSetAlarm:
                SetAlarm();
                break;
            case R.id.btnViewAlarms:
                Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
                MainActivity.this.startActivity(intent);
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
            setText("Start recording!");
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
