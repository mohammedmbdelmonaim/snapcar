package com.intcore.snapcar.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;

import com.intcore.snapcar.core.qualifier.ForActivity;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

public class AudioUtil {

    private MediaRecorder mRecorder = null;
    private MediaPlayer mediaPlayer;
    private String mFileName = null;

    @Inject
    AudioUtil(@ForActivity Context context) {
        File tempDir = new File(context.getExternalCacheDir(), "tempDir");
        tempDir.mkdir();
        mFileName = tempDir.getAbsolutePath().concat("/").concat(String.valueOf(SystemClock.currentThreadTimeMillis())).concat(".3gp");
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.reset();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public void playAudioFile(String url) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPlayerProgress() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }
}