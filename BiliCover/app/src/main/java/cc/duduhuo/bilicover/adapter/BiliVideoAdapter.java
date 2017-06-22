package cc.duduhuo.bilicover.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.bilicover.R;
import cc.duduhuo.bilicover.activity.WebActivity;
import cc.duduhuo.bilicover.bean.BiliVideo;
import cc.duduhuo.bilicover.task.SaveCoverTask;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/22 8:50
 * 版本：1.0
 * 描述：bili视频列表的适配器
 * 备注：
 * =======================================================
 */
public class BiliVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOTER = 0x0001;
    private Activity mActivity;
    private List<BiliVideo> mVideos = new ArrayList<>(1);
    private String mFooterInfo = "";

    public BiliVideoAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void setData(List<BiliVideo> videos) {
        mVideos.clear();
        if (videos != null && !videos.isEmpty()) {
            mVideos.addAll(videos);
        }
        notifyDataSetChanged();
    }

    public void addData(List<BiliVideo> videos) {
        int start = mVideos.size();
        if (videos != null && !videos.isEmpty()) {
            mVideos.addAll(videos);
            notifyItemRangeInserted(start, videos.size());
        }
    }

    /**
     * 设置列表尾部信息
     *
     * @param footerInfo
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bili, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final BiliVideo video = mVideos.get(position);
            Glide.with(mActivity).load(video.getCoverUrl()).asBitmap().centerCrop().into(itemHolder.mIvCover);
            itemHolder.mTvTitle.setText(video.getTitle());
            itemHolder.mTvUp.setText(video.getUp());
            itemHolder.mTvPlay.setText(video.getPlay());
            itemHolder.mTvTime.setText(video.getTime());
            itemHolder.mLlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到内置浏览器播放视频
                    Intent intent = new Intent(mActivity, WebActivity.class);
                    String url =  "http://m.bilibili.com/video/av" + video.getAv() + ".html";
                    intent.putExtra("web", url);
                    mActivity.startActivity(intent);
                }
            });
            itemHolder.mIvCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("下载封面");
                    builder.setMessage("是否下载视频封面？");
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // no op
                        }
                    });
                    builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 下载视频封面
                            SaveCoverTask task = new SaveCoverTask(mActivity);
                            task.execute(video.getCoverUrl());
                        }
                    });
                    builder.create().show();
                }

            });
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            ((FooterViewHolder) holder).mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mVideos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCover;
        private TextView mTvTitle;
        private TextView mTvUp, mTvPlay, mTvTime;
        private LinearLayout mLlVideo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mIvCover = (ImageView) itemView.findViewById(R.id.ivCover);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvUp = (TextView) itemView.findViewById(R.id.tvUp);
            mTvPlay = (TextView) itemView.findViewById(R.id.tvPlay);
            mTvTime = (TextView) itemView.findViewById(R.id.tvTime);
            mLlVideo = (LinearLayout) itemView.findViewById(R.id.llVideo);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mTvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
        }
    }
}
