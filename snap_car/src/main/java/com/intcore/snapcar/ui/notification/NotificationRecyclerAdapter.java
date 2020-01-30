package com.intcore.snapcar.ui.notification;

import android.content.Context;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.notification.NotificationViewModel;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.widget.LinearLayoutManagerWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.LinearLayout.VERTICAL;

@FragmentScope
class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder> {

    private final NotificationPresenter presenter;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<List<NotificationViewModel>> notificationList;

    @Inject
    NotificationRecyclerAdapter(NotificationPresenter presenter, @ForFragment Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.notificationList = new ArrayList<>();
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(layoutInflater.inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.bind(notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateData(List<List<NotificationViewModel>> notificationModels) {
        this.notificationList.clear();
        this.notificationList.addAll(notificationModels);
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header)
        TextView headerTextView;
        @BindView(R.id.rv_notification_body)
        RecyclerView bodyRecyclerView;

        NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(List<NotificationViewModel> notificationViewModels) {
            if (notificationViewModels.size() > 0) {
                headerTextView.setText(notificationViewModels.get(0).getHeadTime());
            }
            bodyRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(context));
            bodyRecyclerView.setAdapter(new BodyRecyclerAdapter(context, notificationViewModels, presenter));
            DividerItemDecoration decoration = new DividerItemDecoration(context, VERTICAL);
            bodyRecyclerView.addItemDecoration(decoration);
        }
    }
}