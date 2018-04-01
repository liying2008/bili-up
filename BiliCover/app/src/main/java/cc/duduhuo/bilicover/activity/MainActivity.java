package cc.duduhuo.bilicover.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.bilicover.R;
import cc.duduhuo.bilicover.adapter.BiliVideoAdapter;
import cc.duduhuo.bilicover.bean.BiliVideo;
import cc.duduhuo.bilicover.listener.OnGetVideoListener;
import cc.duduhuo.bilicover.task.BiliVideoTask;
import cc.duduhuo.bilicover.task.SaveCoverTask;
import cc.duduhuo.bilicover.view.DSwipeRefresh;

public class MainActivity extends AppCompatActivity implements OnGetVideoListener, BiliVideoAdapter.OnClickPicListener {
    // 写外部存储的权限
    private static final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION = 0x0000;
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
    private static String sUrl;

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
                mAdapter.setFooterInfo("正在加载...");
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
        mAdapter.setOnClickPicListener(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvVideo, mAdapter);
    }

    private void startSearch() {
        mKeyword = mEtKeyword.getText().toString().trim();
        if ("".equals(mKeyword)) {
            AppToast.showToast("搜索关键词不能为空。");
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
        AppToast.showToast("获取数据失败");
    }

    @Override
    public void noMore() {
        mSwipeRefresh.setRefreshing(false);
        AppToast.showToast("没有更多数据了");
        mAdapter.setFooterInfo("没有更多数据了");
    }

    @Override
    public void onClickPicListener(String url) {
        // 检查权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                SaveCoverTask task = new SaveCoverTask(this);
                task.execute(url);
            } else {
                // 申请权限
                sUrl = url;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                    Snackbar.make(mRvVideo, R.string.permission_write_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{PERMISSION}, REQUEST_PERMISSION);
                            }
                        }).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, REQUEST_PERMISSION);
                }
            }
        } else {
            SaveCoverTask task = new SaveCoverTask(this);
            task.execute(url);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean granted = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                SaveCoverTask task = new SaveCoverTask(this);
                task.execute(sUrl);
            } else {
                AppToast.showToast(R.string.write_permission_not_granted);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
