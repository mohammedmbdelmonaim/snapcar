package com.intcore.snapcar.ui.photos;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;

import java.util.ArrayList;

public class PhotosActivityArgs implements ActivityArgs {

    private static final String TAG = "tag";
    private static final String POSITION = "position";
    private ArrayList<String> photo;
    private int position;

    public PhotosActivityArgs(ArrayList<String> photo, int position) {
        this.photo = photo;
        this.position = position;
    }

    static PhotosActivityArgs deserializeFrom(Intent intent) {
        return new PhotosActivityArgs(
                intent.getStringArrayListExtra(TAG),
                intent.getIntExtra(POSITION, 0));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, PhotosActivity.class);
        intent.putStringArrayListExtra(TAG, photo);
        intent.putExtra(POSITION, position);
        return intent;
    }

    ArrayList<String> getPhotosList() {
        return photo;
    }

    int getPosition() {
        return position;
    }
}
