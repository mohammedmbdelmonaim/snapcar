package com.intcore.snapcar.ui.chatthread;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.jcminarro.roundkornerlayout.RoundKornerFrameLayout;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.chat.model.chatitem.LocationChatItem;
import com.intcore.snapcar.core.chat.model.chatitem.TextChatItem;
import com.intcore.snapcar.core.chat.model.constants.ChatItemType;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.message.MessageViewModel;
import com.intcore.snapcar.core.chat.model.user.UserViewModel;
import com.intcore.snapcar.core.chat.sdk.BaseChatPresenter;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.helper.SwitchCase;
import com.intcore.snapcar.core.util.MapUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final BaseChatPresenter presenter;
    private final ResourcesUtil resourcesUtil;
    private final Context context;

    RecyclerAdapter(Context context, BaseChatPresenter presenter, ResourcesUtil resourcesUtil) {
        this.layoutInflater = LayoutInflater.from(context);
        this.resourcesUtil = resourcesUtil;
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return presenter.getChatItems().get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatItemType.LOCATION_RECEIVER:
                return new LocationReceiverViewHolder(layoutInflater.inflate(R.layout.item_chat_recieved_location, new ConstraintLayout(context), false));
            case ChatItemType.LOCATION_SENDER:
                return new LocationSenderViewHolder(layoutInflater.inflate(R.layout.item_chat_sent_location, new ConstraintLayout(context), false));
            case ChatItemType.TEXT_RECEIVER:
                return new TextReceiverViewHolder(layoutInflater.inflate(R.layout.item_chat_recieved_message, new ConstraintLayout(context), false));
            case ChatItemType.TEXT_SENDER:
                return new TextSenderViewHolder(layoutInflater.inflate(R.layout.item_chat_sent_message, new ConstraintLayout(context), false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        new SwitchCase(viewHolder)
                .whenInstanceOf(TextSenderViewHolder.class, holder -> ((TextSenderViewHolder) holder).bind((TextChatItem) presenter.getChatItems().get(position)))
                .whenInstanceOf(TextReceiverViewHolder.class, holder -> ((TextReceiverViewHolder) holder).bind((TextChatItem) presenter.getChatItems().get(position)))
                .whenInstanceOf(LocationSenderViewHolder.class, holder -> ((LocationSenderViewHolder) holder).bind((LocationChatItem) presenter.getChatItems().get(position)))
                .whenInstanceOf(LocationReceiverViewHolder.class, holder -> ((LocationReceiverViewHolder) holder).bind((LocationChatItem) presenter.getChatItems().get(position)))
                .otherwise(o -> {
                });
    }

    @Override
    public int getItemCount() {
        return presenter.getChatItems().size();
    }

    class TextSenderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_message)
        TextView messageTextView;
        @BindView(R.id.iv_user)
        CircleImageView userAvatar;
        @BindView(R.id.tv_status)
        TextView statusTextView;
        private MessageViewModel viewModel;

        TextSenderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(TextChatItem textChatItem) {
            this.viewModel = textChatItem.getMessageViewModel();
            bindAvatar();
            int messageStatus = textChatItem.getMessageViewModel().getMessageStatus();
            Resources resources = context.getResources();
            int colorStatusNormal = resources.getColor(R.color.colorTextSecondary);
            int colorStatusError = resources.getColor(R.color.colorError);
            switch (messageStatus) {
                case MessageStatus.SENDING:
                    updateView(false, 0.5f, resources.getString(R.string.sending), colorStatusNormal);
                    break;
                case MessageStatus.SENT:
                    updateView(true, 1f, viewModel.getFriendlyTime(), colorStatusNormal);
                    break;
                case MessageStatus.FAILURE:
                    updateView(false, 0.5f, resources.getString(R.string.resend), colorStatusError);
                    break;
            }
        }

        private void bindAvatar() {
            UserViewModel currentUser = presenter.getCurrentUser();
            Glide.with(context)
                    .load(currentUser.getImageUrl())
                    .placeholder(R.drawable.block_user__material)
                    .centerCrop()
                    .into(userAvatar);
        }

        private void updateView(boolean activated, float alpha, String status, int statusColor) {
            messageTextView.setText(viewModel.getText().trim());
            statusTextView.setTextColor(statusColor);
            messageTextView.setActivated(activated);
            messageTextView.setAlpha(alpha);
            statusTextView.setText(status);
            userAvatar.setAlpha(alpha);
        }

        @OnClick(R.id.tv_status)
        void onResendClick() {
            if (viewModel.getMessageStatus() == MessageStatus.FAILURE) {
                presenter.resendMessage(viewModel);
            }
        }

        @OnClick(R.id.iv_user)
        void onAvatarClick() {
            if (viewModel.getUserViewModel() != null) {
                //TODO navigate to user profile
            }
        }
    }

    class TextReceiverViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_message)
        TextView messageTextView;
        @BindView(R.id.iv_user)
        CircleImageView userAvatar;
        @BindView(R.id.tv_status)
        TextView statusTextView;
        private MessageViewModel viewModel;

        TextReceiverViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(TextChatItem textChatItem) {
            this.viewModel = textChatItem.getMessageViewModel();
            bindAvatar();
            Resources resources = context.getResources();
            messageTextView.setText(viewModel.getText().trim());
            statusTextView.setTextColor(resources.getColor(R.color.colorTextSecondary));
            statusTextView.setText(viewModel.getFriendlyTime());
            messageTextView.setActivated(true);
            messageTextView.setAlpha(1f);
            userAvatar.setAlpha(1f);
        }

        private void bindAvatar() {
            if (viewModel.getUserViewModel() != null) {
                Glide.with(context)
                        .load(viewModel.getUserViewModel().getImageUrl())
                        .placeholder(R.drawable.block_user__material)
                        .centerCrop()
                        .into(userAvatar);
            }
        }

        @OnClick(R.id.iv_user)
        void onAvatarClick() {
            if (viewModel.getUserViewModel() != null) {
                new VisitorProfileActivityArgs(viewModel.getUserViewModel().getId())
                        .launch(context);
            }
        }
    }

    class LocationSenderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.map)
        MapView mapView;
        @BindView(R.id.iv_user)
        CircleImageView userAvatar;
        @BindView(R.id.tv_status)
        TextView statusTextView;
        @BindView(R.id.iv_photo)
        RoundKornerFrameLayout containerLayout;
        @BindView(R.id.tv_address)
        TextView addressTextView;
        private MessageViewModel messageViewModel;
        private PlaceDTO place;

        LocationSenderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                Intent intent = MapUtil.getIntent(place.getLatitude(), place.getLongitude(), place.getAddress());
                context.startActivity(intent);
            });
        }

        void bind(LocationChatItem locationChatItem) {
            messageViewModel = locationChatItem.getMessageViewModel();
            String message = messageViewModel.getAttachmentUrl();
            place = new Gson().fromJson(message, PlaceDTO.class);
            bindAvatar();
            bindMap();
            addressTextView.setText(place.getAddress());
            Resources resources = context.getResources();
            int colorStatusNormal = resources.getColor(R.color.colorTextSecondary);
            int colorStatusError = resources.getColor(R.color.colorError);
            int messageStatus = messageViewModel.getMessageStatus();
            switch (messageStatus) {
                case MessageStatus.SENDING:
                    updateView(0.5f, resources.getString(R.string.sending), colorStatusNormal);
                    break;
                case MessageStatus.SENT:
                    updateView(1f, messageViewModel.getFriendlyTime(), colorStatusNormal);
                    break;
                case MessageStatus.FAILURE:
                    updateView(0.5f, resources.getString(R.string.resend), colorStatusError);
                    break;
            }
        }

        private void updateView(float alpha, String status, int statusColor) {
            statusTextView.setTextColor(statusColor);
            statusTextView.setText(status);
            containerLayout.setAlpha(alpha);
            userAvatar.setAlpha(alpha);
        }

        private void bindAvatar() {
            UserViewModel currentUser = presenter.getCurrentUser();
            Glide.with(context)
                    .load(currentUser.getImageUrl())
                    .placeholder(R.drawable.block_user__material)
                    .centerCrop()
                    .into(userAvatar);
        }

        private void bindMap() {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(googleMap -> {
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory
                        .fromBitmap(resourcesUtil
                                .loadBitmapFromView(resourcesUtil.getLayoutInflater().inflate(R.layout.marker, null))));
                googleMap.clear();
                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            });
        }

        @OnClick(R.id.tv_address)
        void onMapClick() {
            Intent intent = MapUtil.getIntent(place.getLatitude(), place.getLongitude(), place.getAddress());
            context.startActivity(intent);
        }

        @OnClick(R.id.tv_status)
        void onResendClicked() {
            if (messageViewModel.getMessageStatus() == MessageStatus.FAILURE) {
                presenter.resendMessage(messageViewModel);
            }
        }
    }

    class LocationReceiverViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.map)
        MapView mapView;
        @BindView(R.id.iv_user)
        CircleImageView userAvatar;
        @BindView(R.id.tv_address)
        TextView addressTextView;
        @BindView(R.id.tv_status)
        TextView statusTextView;
        private PlaceDTO place;
        private MessageViewModel viewModel;

        LocationReceiverViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                Intent intent = MapUtil.getIntent(place.getLatitude(), place.getLongitude(), place.getAddress());
                context.startActivity(intent);
            });
        }

        void bind(LocationChatItem locationChatItem) {
            this.viewModel = locationChatItem.getMessageViewModel();
            String message = locationChatItem.getMessageViewModel().getAttachmentUrl();
            place = new Gson().fromJson(message, PlaceDTO.class);
            bindAvatar();
            bindMap();
            addressTextView.setText(place.getAddress());
            statusTextView.setText(locationChatItem.getMessageViewModel().getFriendlyTime());
        }

        private void bindAvatar() {
            if (viewModel.getUserViewModel() != null) {
                Glide.with(context)
                        .load(viewModel.getUserViewModel().getImageUrl())
                        .placeholder(R.drawable.block_user__material)
                        .centerCrop()
                        .into(userAvatar);
            }
        }

        private void bindMap() {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(googleMap -> {
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory
                        .fromBitmap(resourcesUtil
                                .loadBitmapFromView(resourcesUtil.getLayoutInflater().inflate(R.layout.marker, null))));
                googleMap.clear();
                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            });
        }

        @OnClick(R.id.iv_user)
        void onAvatarClick() {
            if (viewModel.getUserViewModel() != null) {
                new VisitorProfileActivityArgs(viewModel.getUserViewModel().getId())
                        .launch(context);
            }
        }

        @OnClick(R.id.tv_address)
        void onMapClick() {
            Intent intent = MapUtil.getIntent(place.getLatitude(), place.getLongitude(), place.getAddress());
            context.startActivity(intent);
        }
    }
}