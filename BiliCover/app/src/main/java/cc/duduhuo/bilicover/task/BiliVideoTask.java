package cc.duduhuo.bilicover.task;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.bilicover.bean.BiliVideo;
import cc.duduhuo.bilicover.listener.OnGetVideoListener;
import cc.duduhuo.bilicover.util.HttpUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/22 8:36
 * 版本：1.0
 * 描述：获取Bili视频列表的任务类
 * 备注：
 * =======================================================
 */
public class BiliVideoTask extends AsyncTask<String, Void, Integer> {
    private static final int ON_SUCCESS = 0x0000;
    private static final int ON_FAILURE = 0x0001;

    private OnGetVideoListener mListener;
    private List<BiliVideo> biliVideos = new ArrayList<>(1);

    public BiliVideoTask(OnGetVideoListener listener) {
        this.mListener = listener;
    }

    /**
     * @param params params的第一个参数是搜索关键词；第二个参数是页码
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        // https://api.bilibili.com/x/web-interface/search/all?jsonp=jsonp&highlight=1&keyword=aaa&page=2
        String url = "https://api.bilibili.com/x/web-interface/search/all?jsonp=jsonp&highlight=1&keyword=" +
            URLEncoder.encode(params[0]) + "&page=" + params[1];
        Log.i("url ", url);
        biliVideos.clear();
        try {
            String jsonStr = HttpUtil.getData(url);
            JSONObject root = (JSONObject) JSON.parse(jsonStr);
            JSONObject data = (JSONObject) root.get("data");
            JSONObject result = (JSONObject) data.get("result");
            JSONArray videos = result.getJSONArray("video");
            if (videos != null) {
                int size = videos.size();
                if (size > 0) {
                    for (Object obj : videos) {
                        JSONObject jsonObj = (JSONObject) obj;
                        // 得到图片地址
                        String coverUrl = "http:" + jsonObj.getString("pic");
                        // 得到播放时间
                        String time = jsonObj.getString("duration");
                        // 得到视频AV号
                        String av = String.valueOf(jsonObj.getLong("id"));
                        // 得到视频标题
                        String title = jsonObj.getString("title");
                        title = title.replace("<em class=\"keyword\">", "<font color=\"#f25d8e\">")
                            .replace("</em>", "</font>");
                        // 得到UP主姓名
                        String up = jsonObj.getString("author");
                        // 得到播放数
                        int play = jsonObj.getIntValue("play");
                        BiliVideo biliVideo = new BiliVideo(coverUrl, av, title, up, play, time);
                        Log.d("bili", biliVideo.toString());
                        biliVideos.add(biliVideo);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ON_FAILURE;
        }
        return ON_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        if (status == ON_FAILURE) {
            if (mListener != null) {
                mListener.onFailure();
            }
        } else if (status == ON_SUCCESS) {
            if (biliVideos.size() == 0) {
                if (mListener != null) {
                    mListener.noMore();
                }
            } else {
                if (mListener != null) {
                    mListener.getVideos(biliVideos);
                }
            }
        }
    }
}
