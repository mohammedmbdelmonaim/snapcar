package com.intcore.snapcar.ui.chat;

import android.app.AlertDialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.chat.ChatViewModel;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@FragmentScope
class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private final LayoutInflater layoutInflater;
    private final ChatPresenter chatPresenter;
    private final Context context;
    private UiUtil uiUtil;

    @Inject
    ChatRecyclerAdapter(@ForFragment Context context, ChatPresenter chatPresenter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.chatPresenter = chatPresenter;
        this.context = context;
        this.uiUtil = new UiUtil(context);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(layoutInflater.inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(chatPresenter.getChatList().get(position));
    }

    @Override
    public int getItemCount() {
        return chatPresenter.getChatList().size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.user_avatar)
        CircleImageView userAvatar;
        @BindView(R.id.tv_name)
        TextView nameTextView;
        @BindView(R.id.tv_last_message)
        TextView lastMessageTextView;
        @BindView(R.id.tv_date)
        TextView dateTextView;
        @BindView(R.id.tv_counter)
        TextView counterTextView;
        @BindView(R.id.divider)
        View divider;
        private ChatViewModel chatViewModel;
        private String review;

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> {
                int messageId = 0;
                if (chatViewModel.getMessageViewModel() != null) {
                    messageId = Integer.parseInt(chatViewModel.getMessageViewModel().getServerId());
                }
                chatPresenter.onChatClicked(chatViewModel.getId(),
                        messageId,
                        chatViewModel.getUserViewModel().getFirstName());
                chatPresenter.onPause();
            });
        }

        boolean isEnglishLang() {
            return Locale.getDefault().getLanguage().equals("en");
        }

        void bind(ChatViewModel chatViewModel) {
            setTopBottomMargins();
            this.chatViewModel = chatViewModel;
            if (chatViewModel.getMessageViewModel() != null) {
                lastMessageTextView.setText(chatViewModel.getMessageViewModel().getText());
            } else {
                lastMessageTextView.setText(R.string.send_message);
            }
            if (isEnglishLang()) {
                nameTextView.setGravity(Gravity.LEFT);
                lastMessageTextView.setGravity(Gravity.LEFT);
            } else {
                nameTextView.setGravity(Gravity.RIGHT);
                lastMessageTextView.setGravity(Gravity.RIGHT);
            }
            lastMessageTextView.setActivated(chatViewModel.getSeen() == 0);
            nameTextView.setText(chatViewModel.getUserViewModel().getFirstName());
            dateTextView.setText(chatViewModel.getLastMessageTime());
            Glide.with(itemView)
                    .load(chatViewModel.getUserViewModel().getImageUrl())
                    .centerCrop()
                    .into(userAvatar);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_extra),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        0);
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == chatPresenter.getChatList().size() - 1) {
                divider.setVisibility(View.INVISIBLE);
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }

        @OnClick(R.id.iv_more)
        void onMoreClicked(View view) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            ((HostActivity) context).getMenuInflater().inflate(R.menu.menu_chat, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this::onMoreFlowMenuItemClicked);
            popupMenu.show();
        }

        private boolean onMoreFlowMenuItemClicked(MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.item_report:
                    chatPresenter.onReportClicked((int) chatViewModel.getUserViewModel().getId());
                    return true;

                case R.id.item_block:
                    uiUtil.getDialogBuilder(context, R.layout.rate_block_reason)
                            .editText(R.id.et_review, text -> this.review = text)
                            .clickListener(R.id.tv_submit, (dialog, view) -> {
                                chatPresenter.onBlockClicked((int) chatViewModel.getUserViewModel().getId(), review);
                                dialog.dismiss();
                            })
                            .background(R.drawable.inset_bottomsheet_background)
                            .gravity(Gravity.CENTER)
                            .cancelable(true)
                            .build()
                            .show();
                    return true;

                case R.id.item_delete:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getString(R.string.delete_chat));
                    dialog.setMessage(context.getString(R.string.question_delete_chat));
                    dialog.setPositiveButton(context.getString(R.string.delete),(dialog1, which) -> {
                        chatPresenter.deleteChat(chatViewModel.getId());
                    });
                    dialog.setNegativeButton(context.getString(R.string.cancel),null);
                    dialog.show();
                    break;
            }

            return false;
        }
    }
}