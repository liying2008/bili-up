package cc.duduhuo.bilicover.task;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.bilicover.bean.BiliVideo;
import cc.duduhuo.bilicover.listener.OnGetVideoListener;

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
        biliVideos.clear();
        try {
            Document doc = Jsoup.connect("http://search.bilibili.com/all?keyword=" + params[0]
                + "&page=" + params[1] + "&order=totalrank").get();
            Elements elements = doc.select(".video").select(".list").select(".av");
            if (!elements.isEmpty()) {
                Element select = elements.get(0);
                Element mainHrefEle = select.getElementsByTag("a").get(0);
                // 得到视频链接
                String href = mainHrefEle.attr("href");
                href = "http:" + href.substring(0, href.lastIndexOf('?'));
                // 得到视频标题
                String title = mainHrefEle.attr("title");
                // 得到视频AV号
                String av = getAVNum(href);
                // 得到图片地址
                String coverUrl = select.getElementsByTag("img").get(0).attr("data-src");
                coverUrl = "http:" + coverUrl;
                // 得到播放时间
                String time = select.getElementsByTag("span").get(0).text();

                Elements mainInfo = select.getElementsByClass("so-icon");
                // 得到播放数
                String play = mainInfo.get(0).text();

                // 得到UP主姓名
                String up = mainInfo.get(3).getElementsByTag("a").text();

                BiliVideo biliVideo = new BiliVideo(coverUrl, av, title, up, play, time);
                Log.d("bili", biliVideo.toString());
                biliVideos.add(biliVideo);
            }
            elements = doc.select(".video").select(".matrix");
            for (Element ele : elements) {
                Element hrefEle = ele.getElementsByTag("a").get(0);
                // 得到视频链接
                String href = hrefEle.attr("href");
                href = "http:" + href.substring(0, href.lastIndexOf('?'));
                // 得到视频标题
                String title = hrefEle.attr("title");
                // 得到视频AV号
                String av = getAVNum(href);
                Element img = ele.getElementsByTag("img").get(0);
                // 得到图片地址
                String coverUrl = img.attr("data-src");
                coverUrl = "http:" + coverUrl;
                // 得到播放时间
                String time = ele.getElementsByTag("span").get(0).text();

                Elements info = ele.getElementsByClass("so-icon");
                // 得到播放数
                String watchInfo = info.get(0).text();

                Element upInfo = info.get(3);
                // 得到UP主姓名
                String up = upInfo.getElementsByTag("a").text();
                BiliVideo biliVideo = new BiliVideo(coverUrl, av, title, up, watchInfo, time);
                Log.d("bili", biliVideo.toString());
                // 保存
                biliVideos.add(biliVideo);
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

    /**
     * 得到AV号
     *
     * @param href
     * @return
     */
    private String getAVNum(String href) {
        return href.replace("http://www.bilibili.com/video/av", "").replace("/", "");
    }
}
