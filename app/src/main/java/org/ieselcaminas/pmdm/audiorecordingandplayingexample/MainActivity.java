package org.ieselcaminas.pmdm.audiorecordingandplayingexample;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD = 123;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private String audioFilePath;
    private Button stopButton;
    private Button playButton;
    private Button recordButton;

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.playButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        if (!hasMicrophone()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        } else {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
        }

        // Internal storage
        // audioFilePath = getFilesDir() + "/myaudio.3gp";
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/myaudio.3gp";
    }

    protected boolean hasMicrophone() {
        PackageManager packageManager = this.getPackageManager();
        return packageManager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    public void reallyRecordAudio() {
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    public void recordAudio (View view) throws IOException {

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ||  ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            Toast.makeText(MainActivity.this, "You don't have permission to record", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_RECORD);

        } else {
            reallyRecordAudio();
        }

    }

    public void stopAudio (View view) {

        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if (isRecording) {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }

    public void playAudio (View view) throws IOException {

        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                recordButton.setEnabled(true);

            }
        });
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // mediaRecorder.start();
                } else {

                    Dialog d = new AlertDialog.Builder(MainActivity.this).setTitle("Error").
                            setMessage("I need permission to record").create();
                    d.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

