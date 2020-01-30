package com.intcore.snapcar.ui.notification;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.constant.NotificationType;
import com.intcore.snapcar.store.model.notification.NotificationViewModel;
import com.intcore.snapcar.ui.confirmcommision.ConfirmCommissionActivity;
import com.intcore.snapcar.ui.coupon.CouponActivity;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@FragmentScope
class BodyRecyclerAdapter extends RecyclerView.Adapter<BodyRecyclerAdapter.BodyViewHolder> {

    private final NotificationPresenter presenter;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<NotificationViewModel> notificationViewModels;

    BodyRecyclerAdapter(Context context, List<NotificationViewModel> notificationViewModels, NotificationPresenter presenter) {
        this.notificationViewModels = notificationViewModels;
        this.layoutInflater = LayoutInflater.from(context);
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public BodyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BodyViewHolder(layoutInflater.inflate(R.layout.item_notification_body, parent, false));
    }

    @Override
    public void onBindViewHolder(BodyViewHolder holder, int position) {
        holder.bind(notificationViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationViewModels.size();
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    class BodyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.seen_indicator)
        View seenIndicator;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.body_container)
        ConstraintLayout notificationItem;
        private NotificationViewModel notificationViewModel;

        BodyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                presenter.onNotificationClicked(notificationViewModel.getId());
                switch (notificationViewModel.getDataViewModel().getType()) {
                    case NotificationType.ADMIN_NOTIFICATION:
                        break;
                    case NotificationType.DISCOUNT_NOTIFICATION:
                        context.startActivity(new Intent(context, CouponActivity.class));
                        break;
                    case NotificationType.EDIT_NOTIFICATION:
                        Intent i3 = new Intent(context, ViewCarActivity.class);
                        i3.putExtra("carId", notificationViewModel.getDataViewModel().getCarId());
                        context.startActivity(i3);
                        break;
                    case NotificationType.HOT_ZONE_NOTIFICATION:
                        presenter.shouldNavigateToProfile();
                        break;
                    case NotificationType.MATCH_INTEREST_NOTIFICATION:
                        Intent i = new Intent(context, ViewCarActivity.class);
                        i.putExtra("carId", notificationViewModel.getDataViewModel().getCarId());
                        context.startActivity(i);
                        break;
                    case NotificationType.NEARBY_INTEREST_NOTIFICATION:
                        Intent i2 = new Intent(context, ViewCarActivity.class);
                        i2.putExtra("carId", notificationViewModel.getDataViewModel().getCarId());
                        context.startActivity(i2);
                        break;
                    case NotificationType.PAYMENT_REMINDER_NOTIFICATION:
                        Intent i4 = new Intent(context, ConfirmCommissionActivity.class);
                        i4.putExtra("commission", notificationViewModel.getDataViewModel().getCommission());
                        i4.putExtra("carId", notificationViewModel.getDataViewModel().getCarId());
                        context.startActivity(i4);
                        break;
                    case NotificationType.VERIFICATION_NOTIFICATION:
                        presenter.shouldNavigateToProfile();
                        break;
                    case NotificationType.VIP_NOTIFICATION:
                        presenter.shouldNavigateToProfile();
                        break;
                    case NotificationType.CAR_VALIDITY_NOTIFICATION:
                        presenter.shouldNavigateToHome();
                        break;
                    case NotificationType.DISABLE_ADS_NOTIFICATION:
                        break;
                }
            });
        }

        void bind(NotificationViewModel notificationViewModel) {
            this.notificationViewModel = notificationViewModel;
            tvContent.setText(notificationViewModel.getDataViewModel().getMessage());
            notificationItem.setActivated(notificationViewModel.getReadAt() == null);
            seenIndicator.setActivated(notificationViewModel.getReadAt() == null);
            tvTitle.setText(notificationViewModel.getDataViewModel().getTitle());
            tvTime.setText(notificationViewModel.getDate());
            if (isEnglishLang()) {
                tvTime.setGravity(Gravity.LEFT);
                tvTitle.setGravity(Gravity.LEFT);
                tvContent.setGravity(Gravity.LEFT);
            } else {
                tvTime.setGravity(Gravity.RIGHT);
                tvTitle.setGravity(Gravity.RIGHT);
                tvContent.setGravity(Gravity.RIGHT);
            }
        }
    }
}