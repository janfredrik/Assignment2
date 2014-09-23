/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
package no.clap.assignment2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;


public class AudioRecord extends FragmentActivity
{
    private static final String LOG_TAG = "AudioRecord";
    private String mFileName = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayer   mPlayer = null;

    boolean mStartRecording = true;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
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
        Crouton.makeText(this, this.getString(R.string.recording_saved) + mFileName, Style.CONFIRM).show();
    }

    public AudioRecord() {

    }

    public String getNewFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + currentDateandTime + ".3gp";

        return mFileName;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_audio_record);

        mStartRecording = true;

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

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    // https://github.com/inmite/android-styled-dialogs
    public void help(View v) {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setMessage(R.string.recording_help).show();
    }
}
