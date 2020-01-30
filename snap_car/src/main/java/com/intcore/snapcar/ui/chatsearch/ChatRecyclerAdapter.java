package com.intcore.snapcar.ui.chatsearch;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.ui.chatthread.ChatThreadActivityArgs;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private final ChatSearchPresenter chatPresenter;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<ChatViewModel> chatViewModels;

    @Inject
    ChatRecyclerAdapter(@ForActivity Context context, ChatSearchPresenter chatPresenter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.chatViewModels = new ArrayList<>();
        this.chatPresenter = chatPresenter;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(layoutInflater.inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(chatViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return chatViewModels.size();
    }

    public void updateData(List<ChatViewModel> chatModels) {
        this.chatViewModels.clear();
        this.chatViewModels.addAll(chatModels);
        notifyDataSetChanged();
    }

    public void notifyRemovedItem(int position) {
        if (position > -1 && position < chatViewModels.size()) {
            chatViewModels.remove(position);
            notifyItemRemoved(position);
        }
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

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> {
                int messageId = 0;
                if (chatViewModel.getMessageViewModel() != null) {
                    messageId = Integer.parseInt(chatViewModel.getMessageViewModel().getServerId());
                }
                new ChatThreadActivityArgs(chatViewModel.getId(), messageId, chatViewModel.getUserViewModel().getFirstName(), null)
                        .launch(context);
            });
        }

        void bind(ChatViewModel chatViewModel) {
            setTopBottomMargins();
            this.chatViewModel = chatViewModel;
            if (chatViewModel.getMessageViewModel() != null) {
                lastMessageTextView.setText(chatViewModel.getMessageViewModel().getText());
            } else {
                lastMessageTextView.setText(R.string.sending_new_message);
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
            } else if (getAdapterPosition() == chatViewModels.size() - 1) {
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
            ((ChatSearchActivity) context).getMenuInflater().inflate(R.menu.menu_chat, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this::onMoreFlowMenuItemClicked);
            popupMenu.show();
        }

        private boolean onMoreFlowMenuItemClicked(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.item_report) {
                chatPresenter.onReportClicked((int) chatViewModel.getUserViewModel().getId());
                return true;
            } else if (menuItem.getItemId() == R.id.item_block) {
                chatPresenter.onBlockClicked((int) chatViewModel.getUserViewModel().getId(), getAdapterPosition());
                return true;
            }
            return false;
        }
    }
}