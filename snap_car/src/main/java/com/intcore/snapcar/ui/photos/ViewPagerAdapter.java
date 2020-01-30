package com.intcore.snapcar.ui.photos;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<String> imagesArrayList;
    private LayoutInflater inflater;
    private Context context;

    ViewPagerAdapter(Context context, ArrayList<String> imagesArrayList) {
        inflater = LayoutInflater.from(context);
        this.imagesArrayList = imagesArrayList;
        this.context = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imagesArrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.photo_layout, view, false);
        assert imageLayout != null;
        final PhotoView imageView = imageLayout
                .findViewById(R.id.image);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.update();
        String s = imagesArrayList.get(position);
        Glide.with(context)
                .load(ApiUtils.BASE_URL.concat(s))
                .thumbnail(0.1f)
                .into(imageView);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}