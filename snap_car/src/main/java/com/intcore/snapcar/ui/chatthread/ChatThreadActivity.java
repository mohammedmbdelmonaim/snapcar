package com.intcore.snapcar.ui.chatthread;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.chat.ChatSDKManager;
import com.intcore.snapcar.core.chat.config.ChatConfig;
import com.intcore.snapcar.core.chat.config.ChatConfigBuilder;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.message.MessageMapper;
import com.intcore.snapcar.core.chat.model.payload.PayloadModel;
import com.intcore.snapcar.core.chat.model.socket.SocketDTO;
import com.intcore.snapcar.core.chat.model.socket.SocketModel;
import com.intcore.snapcar.core.chat.sdk.BaseChatPresenter;
import com.intcore.snapcar.core.chat.sdk.BaseChatScreen;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatModel;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.widget.LinearLayoutManagerWrapper;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/*
 *This class is responsible for showing chat thread using roomId
 */
@ActivityScope
public class ChatThreadActivity extends BaseActivity implements BaseChatScreen {

    private static final int PLACE_PICKER_REQUEST = 101;
    @BindView(R.id.tb_chat)
    Toolbar toolbar;
    @BindView(R.id.rc_chat)
    RecyclerView chatRecyclerView;
    @BindView(R.id.iv_add)
    ImageView addAttachmentImageView;
    @BindView(R.id.et_message)
    RxEditText messageRxEditText;
    @BindView(R.id.snackbar_container)
    CoordinatorLayout snackBarContainer;
    @BindView(R.id.iv_down_arrow)
    ImageView downArrowImageView;
    @BindView(R.id.btn_action)
    Button actionButton;
    @BindView(R.id.tv_toolbar_title)
    TextView titleTextView;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @Inject
    TextUtil textUtil;
    @Inject
    @ForActivity
    CompositeDisposable disposable;
    @Inject
    UserManager userManager;
    private RecyclerAdapter adapter;
    private BaseChatPresenter presenter;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        setupChat();
    }


    /*
     * this method for setup ChatSDK to initialize socket and do the logic using reusable component
     */
    private void setupChat() {
        ChatThreadActivityArgs chatActivityArgs = ChatThreadActivityArgs.deserializeFrom(getIntent());
        int id = chatActivityArgs.getRoomId();
        ChatModel.isChatAlive = id;
        titleTextView.setText(chatActivityArgs.getUserName());
        setupChatSdk();
        presenter = new BaseChatPresenter(this, id);
        presenter.onCreate();
        MessageMapper messageMapper = new MessageMapper(this);
        SocketDTO socketDTO = messageMapper.toDataTransferObject(
                new SocketModel(SocketDTOType.SUBSCRIBE, new PayloadModel(null, id)));
        presenter.setLastMessageId(chatActivityArgs.getMessageId());
        presenter.setSubscribeRoomData(socketDTO);
        if (chatActivityArgs.getPlaceDTO() != null) {
            presenter.onLocationPicked(chatActivityArgs.getPlaceDTO());
        }
//        setupBackIcon();
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backImageView.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected void onResume() {
        presenter.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onPause();
        super.onStop();
    }

    @Override
    protected void onPause() {
        ChatModel.isChatAlive = 0;
        super.onPause();
    }

    private void setupChatSdk() {
        String apiToken = userManager.getCurrentUser().getApiToken();
        ChatConfig chatConfig = new ChatConfigBuilder()
                .baseUrl(ApiUtils.BASE_URL)
                .socketUrl(ApiUtils.SOCKET_URL + "?api_token=" + apiToken)
                .screen(this)
                .uploadUrl(ApiUtils.UPLOAD_URL)
                .currentUSer(DefaultUserMapper.toUserModel(userManager.getCurrentUser()))
                .build();
        ChatSDKManager.init(chatConfig);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_chat_thread;
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        ChatSDKManager.tearDown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            VanillaAddress place = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
            String address = place.getFormattedAddress();
            PlaceDTO placeDTO = new PlaceDTO(address, place.getLatitude(), place.getLongitude());
            presenter.onLocationPicked(placeDTO);
        }
    }

    @Override
    public void setupEditText() {
        messageRxEditText.setValidityListener(presenter::onAfterTextChange);
    }

    @Override
    public void setupRecyclerView() {
        LinearLayoutManager layout = new LinearLayoutManagerWrapper(this);
        layout.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layout);
        adapter = new RecyclerAdapter(this, presenter, getResourcesUtil());
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(0)) {
                    downArrowImageView.setVisibility(View.VISIBLE);
                } else {
                    downArrowImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void enableInputs() {
        actionButton.setEnabled(true);
        messageRxEditText.setEnabled(true);
        addAttachmentImageView.setEnabled(true);
    }

    @Override
    public void disableInputs() {
        actionButton.setEnabled(false);
        messageRxEditText.setEnabled(false);
        addAttachmentImageView.setEnabled(false);
    }

    @Override
    public void showNetworkConnected() {
        getUiUtil().getSnackBarBuilder(this, snackBarContainer)
                .layout(R.layout.layout_network_connected)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
    }

    @Override
    public void showNetworkConnecting() {
        getUiUtil().getSnackBarBuilder(this, snackBarContainer)
                .layout(R.layout.layout_network_connecting)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
        getUiUtil().hideKeyboard(this);
    }

    @Override
    public void showNetworkDisconnected() {
        getUiUtil().getSnackBarBuilder(this, snackBarContainer)
                .layout(R.layout.layout_network_disconnected)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
        getUiUtil().hideKeyboard(this);
    }

    private void pickLocation(BottomSheetDialog dialog, View view) {
        dialog.dismiss();
        startActivityForResult(SnapCarApplication.getPickerIntent(this),PLACE_PICKER_REQUEST);
    }

    @Override
    public void closeChatScreen() {
        finish();
    }

    @Override
    public void updateUi(DiffUtil.DiffResult result) {
        if (adapter == null) {
            adapter = new RecyclerAdapter(this, presenter, getResourcesUtil());
            chatRecyclerView.setAdapter(adapter);
        }
        result.dispatchUpdatesTo(adapter);
        chatRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void clearInputs() {
        messageRxEditText.setText("");
    }

    @OnClick(R.id.iv_back)
    void onCloseClicked() {
        presenter.onCloseClicked();
    }

    @OnClick(R.id.btn_action)
    void onActionButtonClicked() {
        presenter.onSendClicked(messageRxEditText.getText().toString());
    }

    @OnClick(R.id.iv_add)
    void onAddClicked() {
        getUiUtil().getBottomSheetBuilder(this, R.layout.layout_attachment_chooser)
                .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .clickListener(R.id.tv_location, this::pickLocation)
                .transparentBackground(true)
                .cancelable(true)
                .build()
                .show();
    }

    @OnClick(R.id.iv_down_arrow)
    void onDownArrowClick() {
        if (adapter == null) {
            adapter = new RecyclerAdapter(this, presenter, getResourcesUtil());
            chatRecyclerView.setAdapter(adapter);
        }
        if (adapter.getItemCount() > 0)
            chatRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}