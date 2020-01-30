package com.intcore.snapcar.ui.hotzone;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.coupon.CouponViewModel;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@FragmentScope
class HotZoneAdapter extends RecyclerView.Adapter<HotZoneAdapter.HotZoneViewHolder> {

    private final LayoutInflater layoutInflater;
    private final HotZonePresenter presenter;
    private final Context context;
    private List<CouponViewModel> couponViewModelList;

    @Inject
    HotZoneAdapter(@ForFragment Context context, HotZonePresenter presenter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.couponViewModelList = new ArrayList<>();
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public HotZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HotZoneViewHolder(layoutInflater.inflate(R.layout.item_hotzone_coupon, parent, false));
    }

    @Override
    public void onBindViewHolder(HotZoneViewHolder holder, int position) {
        holder.bind(couponViewModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return couponViewModelList.size();
    }

    public void updateData(ArrayList<CouponViewModel> newCouponViewModels) {
        this.couponViewModelList.clear();
        this.couponViewModelList.addAll(newCouponViewModels);
        notifyDataSetChanged();
    }

    class HotZoneViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.tv_hot_zone_name)
        TextView hotZoneNameTextView;
        @BindView(R.id.tv_sale)
        TextView saleTextView;
        @BindView(R.id.tv_coupon)
        TextView couponTextView;
        @BindView(R.id.tv_date)
        TextView dateTextView;
        @BindView(R.id.user_avatar)
        CircleImageView userAvatar;
        @BindView(R.id.tv_sale_to)
        TextView saleToTextView;
//        @BindView(R.id.tv_hot_zone_name_two)
//        TextView getHotZoneNameTwoTextView;
        private CouponViewModel couponViewModel;

        HotZoneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
        }

        void bind(CouponViewModel couponViewModel) {
            setTopBottomMargins();
            this.couponViewModel = couponViewModel;
            if (isEnglishLang()) {
                String name = couponViewModel.getNameEn();
                if (couponViewModel.getHotZoneViewModel() != null) {
                    name = name.concat(" | ").concat(couponViewModel.getHotZoneViewModel().getName());
                }
                hotZoneNameTextView.setText(name);
                saleTextView.setText(couponViewModel.getDescriptionEn());
            } else {
                String name = couponViewModel.getNameAr();
                if (couponViewModel.getHotZoneViewModel() != null) {
                    name = name.concat(" | ").concat(couponViewModel.getHotZoneViewModel().getName());
                }
                hotZoneNameTextView.setText(name);
                saleTextView.setText(couponViewModel.getDescriptionAr());
            }

            if (couponViewModel.getHotZoneViewModel() != null) {
                Glide.with(itemView)
                        .load(ApiUtils.BASE_URL.concat(couponViewModel.getHotZoneViewModel().getImage()))
                        .centerCrop()
                        .into(userAvatar);
            }

//            if (couponViewModel.getHotZoneViewModel() != null) {
//                if (isEnglishLang()) {
//                    getHotZoneNameTwoTextView.setText(" | ".concat(couponViewModel.getHotZoneViewModel().getName()));
//                } else {
//                    getHotZoneNameTwoTextView.setText(couponViewModel.getHotZoneViewModel().getName().concat(" | "));
//
//                }
//            }
            couponTextView.setText(couponViewModel.getCoupon());
            saleToTextView.setText(itemView.getContext().getString(R.string.sale_up_to).concat(" ").concat(couponViewModel.getAmount()).concat(" %"));
            dateTextView.setText(itemView.getContext().getString(R.string.valid_to).concat(" ").concat(couponViewModel.getExpireAt().substring(0, 10)));
            if (!isEnglishLang()) {
                hotZoneNameTextView.setGravity(Gravity.RIGHT);
                couponTextView.setGravity(Gravity.RIGHT);
            } else {
                hotZoneNameTextView.setGravity(Gravity.LEFT);
                couponTextView.setGravity(Gravity.LEFT);
            }
        }

        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == couponViewModelList.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }

        @OnClick(R.id.tv_location)
        void onLocationClicked() {
            double latitude = 0;
            double longitude = 0;
            if (couponViewModel.getHotZoneViewModel() != null) {
                if (!TextUtils.isEmpty(couponViewModel.getHotZoneViewModel().getLatitude())) {
                    latitude = Double.parseDouble(couponViewModel.getHotZoneViewModel().getLatitude());
                }
            }
            if (couponViewModel.getHotZoneViewModel() != null) {
                if (!TextUtils.isEmpty(couponViewModel.getHotZoneViewModel().getLongitude())) {
                    longitude = Double.parseDouble(couponViewModel.getHotZoneViewModel().getLongitude());
                }
            }
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude;
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            context.startActivity(mapIntent);
        }

        @OnClick(R.id.tv_copy)
        void onCopyClicked() {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("HotZone name", couponViewModel.getCoupon());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            presenter.showSuccessMessage(context.getString(R.string.text_copied));
        }
    }
}