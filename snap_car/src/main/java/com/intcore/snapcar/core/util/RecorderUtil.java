package com.intcore.snapcar.core.util;

import android.os.Environment;

import com.jakewharton.rxrelay2.BehaviorRelay;

import java.io.File;
import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class RecorderUtil {

    private final BehaviorRelay<Boolean> successfulRecordRelay;
    private final BehaviorRelay<Boolean> deletedRecordRelay;
    private final BehaviorRelay<File> outputFileRelay;
    private final CompositeDisposable disposable;

    private RecorderUtil() {
        this.successfulRecordRelay = BehaviorRelay.createDefault(false);
        this.deletedRecordRelay = BehaviorRelay.createDefault(false);
        this.outputFileRelay = BehaviorRelay.create();
        this.disposable = new CompositeDisposable();
    }

    public static RecorderUtil getInstance() {
        return new RecorderUtil();
    }

    public void deleteLastRecordedFile() {
        if (successfulRecordRelay.getValue()) {
            boolean deleted = outputFileRelay.getValue().delete();
            deletedRecordRelay.accept(deleted);
        } else {
            deletedRecordRelay.accept(false);
        }
    }

    public File getLastRecordedFile() {
        return outputFileRelay.getValue();
    }

    public boolean isLastRecordAttemptSuccessful() {
        return successfulRecordRelay.getValue();
    }

    public boolean isLastDeleteAttemptSuccessful() {
        return deletedRecordRelay.getValue();
    }

    public void tearDown() {
        disposable.clear();
    }

    private void createNewFileAndStartRecording() {
        File file = new File(generateOutputFilePath());
        try {
            file.createNewFile();
        } catch (IOException e) {
            Timber.e(e);
        }
        outputFileRelay.accept(file);
    }

    private String generateOutputFilePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "voice_recorder_"
                + "audio_"
                + System.nanoTime()
                + ".aac";
    }
}
