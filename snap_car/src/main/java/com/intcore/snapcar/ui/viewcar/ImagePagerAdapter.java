package com.intcore.snapcar.ui.viewcar;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.ui.photos.PhotosActivityArgs;

import java.util.ArrayList;
import java.util.List;

class ImagePagerAdapter extends PagerAdapter {

    private List<String> images = new ArrayList<>();
    private Context context;

    ImagePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.item_image_pager, container, false);
        ImageView im = v.findViewById(R.id.pager_image);
        Glide.with(context)
                .load(ApiUtils.BASE_URL.concat(images.get(position)))
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .into(im);
        container.addView(v);
        im.setOnClickListener(view ->
                new PhotosActivityArgs((ArrayList<String>) images, 0).launch(context));
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    public void swapData(List<String> im) {
        images.clear();
        images.addAll(im);
        notifyDataSetChanged();
    }
}