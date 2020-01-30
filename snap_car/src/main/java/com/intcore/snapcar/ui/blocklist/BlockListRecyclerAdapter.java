package com.intcore.snapcar.ui.blocklist;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.OnBottomReachedListener;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
class BlockListRecyclerAdapter extends RecyclerView.Adapter<BlockListRecyclerAdapter.BlockListViewHolder> {

    private final List<DefaultUserModel> blockListItems;
    private final LayoutInflater layoutInflater;
    private final BlockListPresenter presenter;
    private final Context context;
    private OnBottomReachedListener onBottomReachedListener;

    @Inject
    BlockListRecyclerAdapter(@ForActivity Context context, BlockListPresenter presenter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.blockListItems = new ArrayList<>();
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public BlockListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlockListViewHolder(layoutInflater.inflate(R.layout.item_block_list, parent, false));
    }

    @Override
    public void onBindViewHolder(BlockListViewHolder holder, int position) {
        if (position == blockListItems.size() - 1 && onBottomReachedListener != null) {
            onBottomReachedListener.loadMore();
        }
        holder.bind(blockListItems.get(position));
    }

    @Override
    public int getItemCount() {
        return blockListItems.size();
    }

    void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        Preconditions.checkNonNull(onBottomReachedListener);
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setBlockList(List<DefaultUserModel> blockList) {
        this.blockListItems.clear();
        this.blockListItems.addAll(blockList);
        notifyDataSetChanged();
    }

    public void appendBlockList(List<DefaultUserModel> newBlockList) {
        int i = blockListItems.size();
        blockListItems.addAll(newBlockList);
        notifyItemRangeInserted(i, newBlockList.size());
    }

    void removeItemAt(int blockItemPosition) {
        if (blockItemPosition > -1 && blockItemPosition < blockListItems.size()) {
            blockListItems.remove(blockItemPosition);
            notifyItemRemoved(blockItemPosition);
            notifyDataSetChanged();
        }
    }

    class BlockListViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.user_avatar)
        CircleImageView userAvatar;
        @BindView(R.id.tv_user_name)
        TextView usernameTextView;
        @BindView(R.id.tv_country)
        TextView countryTextView;
        private DefaultUserModel defaultUserModel;

        BlockListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
        }

        public void bind(DefaultUserModel defaultUserModel) {
            this.defaultUserModel = defaultUserModel;
            setTopBottomMargins();
            if (defaultUserModel.getCountryModel() != null) {
                countryTextView.setText(defaultUserModel.getCountryModel().getNameEn().concat(" - ").concat(defaultUserModel.getCityModel().getNameEn()));
            }
            usernameTextView.setText(defaultUserModel.getFirstName());
            if (defaultUserModel.getImageUrl() != null) {
                Glide.with(itemView)
                        .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                        .centerCrop()
                        .into(userAvatar);
            }
            if (!isEnglishLang()) {
                countryTextView.setGravity(Gravity.RIGHT);
                usernameTextView.setGravity(Gravity.RIGHT);
            } else {
                countryTextView.setGravity(Gravity.LEFT);
                usernameTextView.setGravity(Gravity.LEFT);
            }
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == blockListItems.size() - 1) {
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

        @OnClick(R.id.tv_unblock)
        void onUnBlockClicked() {
            presenter.onUnBlockClicked(getAdapterPosition(), defaultUserModel.getId());
        }

        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }
    }
}
