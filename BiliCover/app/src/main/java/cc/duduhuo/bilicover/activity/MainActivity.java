package cc.duduhuo.bilicover.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.duduhuo.bilicover.R;
import cc.duduhuo.bilicover.adapter.BiliVideoAdapter;
import cc.duduhuo.bilicover.bean.BiliVideo;
import cc.duduhuo.bilicover.listener.OnGetVideoListener;
import cc.duduhuo.bilicover.task.BiliVideoTask;
import cc.duduhuo.bilicover.view.DSwipeRefresh;

import static cc.duduhuo.applicationtoast.AppToast.showToast;

public class MainActivity extends AppCompatActivity implements OnGetVideoListener {
    private EditText mEtKeyword;
    private ImageView mIvSearch;
    private DSwipeRefresh mSwipeRefresh;
    private RecyclerView mRvVideo;
    private BiliVideoAdapter mAdapter;
    /** 搜索的关键词 */
    private String mKeyword;
    /** 当前页数 */
    private int mPage;
    /** 是刷新还是继续加载 */
    private boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setListener();
    }

    private void setListener() {
        mAdapter.setFooterInfo("");
        mEtKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                startSearch();
                return true;
            }
        });
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BiliVideoTask task = new BiliVideoTask(MainActivity.this);
                mPage = 1;
                task.execute(mKeyword, String.valueOf(mPage));
                mSwipeRefresh.setRefreshing(true);
                isLoad = false;
            }
        });

        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                mPage++;
                BiliVideoTask task = new BiliVideoTask(MainActivity.this);
                task.execute(mKeyword, String.valueOf(mPage));
                isLoad = true;
            }
        });
    }

    private void findView() {
        mEtKeyword = (EditText) findViewById(R.id.etKeyword);
        mIvSearch = (ImageView) findViewById(R.id.ivSearch);
        mSwipeRefresh = (DSwipeRefresh) findViewById(R.id.swipeRefresh);
        mRvVideo = (RecyclerView) findViewById(R.id.rvVideos);

        mAdapter = new BiliVideoAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvVideo, mAdapter);
    }

    private void startSearch() {
        mKeyword = mEtKeyword.getText().toString().trim();
        if ("".equals(mKeyword)) {
            showToast("搜索关键词不能为空。");
            return;
        }
        BiliVideoTask task = new BiliVideoTask(MainActivity.this);
        mPage = 1;
        task.execute(mKeyword, String.valueOf(mPage));
        mSwipeRefresh.setRefreshing(true);
        isLoad = false;
    }

    @Override
    public void getVideos(List<BiliVideo> videos) {
        mSwipeRefresh.setRefreshing(false);
        if (isLoad) {
            mAdapter.addData(videos);
        } else {
            mAdapter.setData(videos);
        }
        mAdapter.setFooterInfo("上拉加载更多...");
    }

    @Override
    public void onFailure() {
        mSwipeRefresh.setRefreshing(false);
        showToast("获取数据失败");
    }

    @Override
    public void noMore() {
        mSwipeRefresh.setRefreshing(false);
        showToast("没有更多数据了");
        mAdapter.setFooterInfo("没有更多数据了");
    }
}
