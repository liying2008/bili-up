package cc.duduhuo.bilicover.listener;

import java.util.List;

import cc.duduhuo.bilicover.bean.BiliVideo;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/22 9:53
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public interface OnGetVideoListener {
    void getVideos(List<BiliVideo> videos);

    void onFailure();

    void noMore();
}
