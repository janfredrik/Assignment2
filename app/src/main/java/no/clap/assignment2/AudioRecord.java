package no.clap.assignment2;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.media.MediaRecorder;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

// Source for the AudioRecord-code: http://developer.android.com/guide/topics/media/audio-capture.html
// From source: Record the audio including the start and stop functions
// Self made: Get unique filename function

public class AudioRecord extends FragmentActivity {
    private static final String LOG_TAG = "AudioRecord";
    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    boolean mStartRecording = true;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_audio_record);

        final Button button = (Button) findViewById(R.id.RecordButton);
        button.setText(getResources().getString(R.string.start_recording));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFileName = getNewFileName();                   // Get new unique file name
                onRecord(mStartRecording);
                if (mStartRecording) {
                    button.setText(getResources().getString(R.string.stop_recording));
                } else {
                    button.setText(getResources().getString(R.string.start_recording));
                }
                mStartRecording = !mStartRecording;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public AudioRecord() {

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        TextView storageInfo = (TextView)findViewById(R.id.StorageLocation);
        storageInfo.setText(getString(R.string.recording_saved) + mFileName);
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public String getNewFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + currentDateandTime + ".3gp";

        return mFileName;
    }

    // https://github.com/inmite/android-styled-dialogs
    public void help(View v) {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setTitle(R.string.recording_help_button).setMessage(R.string.recording_help).show();
    }
}