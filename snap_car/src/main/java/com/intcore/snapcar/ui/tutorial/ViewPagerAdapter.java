package com.intcore.snapcar.ui.tutorial;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
class ViewPagerAdapter extends PagerAdapter {

    private CircleImageView circleImageView;
    private ImageView walkThroughImageView;
    private TextView descriptionTextView;
    private TextView tutorialTextView;
    private LayoutInflater inflater;
    private Context context;
    private List<String> titleList;
    private List<String> contentList;
    private List<String> contentList2;
    private List<String> contentList3;
    private List<String> contentList4;
    private List<Integer> iconsList;

    @Inject
    ViewPagerAdapter(@ForActivity Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.titleList = new ArrayList<>();
        this.contentList = new ArrayList<>();
        this.contentList4 = new ArrayList<>();
        this.contentList2 = new ArrayList<>();
        this.contentList3 = new ArrayList<>();
        this.iconsList = new ArrayList<>();
        titleList.add(context.getString(R.string.car_quickly_and_easily));
        titleList.add(context.getString(R.string.sell_your_car_four_step));
        titleList.add(context.getString(R.string.live_tracking_on_map));
        titleList.add(context.getString(R.string.hot_zone_service));
        contentList.add(context.getString(R.string.content_one));
        contentList.add(context.getString(R.string.content_two));
        contentList.add(context.getString(R.string.content_three));
        contentList.add(context.getString(R.string.content_four));
        contentList2.add(context.getString(R.string.content_one_two));
        contentList2.add(context.getString(R.string.content_two_two));
        contentList2.add(context.getString(R.string.content_three_two));
        contentList2.add(context.getString(R.string.content_four_two));
        contentList3.add(context.getString(R.string.content_one_three));
        contentList3.add(context.getString(R.string.content_two_three));
        contentList3.add(context.getString(R.string.content_three_three));
        contentList3.add(context.getString(R.string.content_three_four));
        contentList4.add(context.getString(R.string.content_one_four));
        contentList4.add(context.getString(R.string.content_two_four));
        contentList4.add(context.getString(R.string.content_three_four));
        contentList4.add(null);
        iconsList.add(R.drawable.buy_car);
        iconsList.add(R.drawable.sell_car);
        iconsList.add(R.drawable.tracking);
        iconsList.add(R.drawable.hotzone);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.tutorial_view_pager, view, false);
        assert imageLayout != null;
        descriptionTextView = imageLayout.findViewById(R.id.tv_description_tutorial);
        TextView descriptionTextView2 = imageLayout.findViewById(R.id.tv_description_tutorial_two);
        TextView descriptionTextView3 = imageLayout.findViewById(R.id.tv_description_tutorial_three);
        TextView descriptionTextView4 = imageLayout.findViewById(R.id.tv_description_tutorial_four);
        walkThroughImageView = imageLayout.findViewById(R.id.iv_walkthrough);
        circleImageView = imageLayout.findViewById(R.id.civ_tutorial);
        tutorialTextView = imageLayout.findViewById(R.id.tv_tutorial);
        Glide.with(context)
                .load(iconsList.get(position))
                .placeholder(R.drawable.group)
                .into(circleImageView);
        tutorialTextView.setText(titleList.get(position));
        descriptionTextView.setText(contentList.get(position));
        descriptionTextView2.setText(contentList2.get(position));
        descriptionTextView3.setText(contentList3.get(position));
        if (contentList4.get(position) != null) {
            descriptionTextView4.setText(contentList4.get(position));
        } else {
            descriptionTextView4.setVisibility(View.GONE);
        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    void animate() {
//        YoYo.with(Techniques.SlideInRight).playOn(circleImageView);
//        YoYo.with(Techniques.SlideInRight).playOn(tutorialTextView);
//        YoYo.with(Techniques.SlideInRight).playOn(descriptionTextView);
//        YoYo.with(Techniques.SlideInRight).playOn(walkThroughImageView);
    }
}