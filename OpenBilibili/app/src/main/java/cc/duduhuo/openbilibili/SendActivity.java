package cc.duduhuo.openbilibili;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.openbilibili.config.Const;

public class SendActivity extends AppCompatActivity {
    private TextView mTvMsg, mTvLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        mTvMsg = (TextView) findViewById(R.id.tvMsg);
        mTvLink = (TextView) findViewById(R.id.tvLink);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // 处理发送来的文字
                handleText(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }

    /**
     * 处理文本
     *
     * @param text
     */
    private void handleText(String text) {
        if (text != null) {
            // 找到所有连续数字
            List<String> nums = findNums(text);

            if (nums.isEmpty()) {   // 字符串中没有数字
                mTvMsg.setText("没有检测到AV号，请选择其他分享方式");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(intent, "分享文本"));
            } else {
                // 在TextView中显示URL
                showUrl(nums);
                // 默认打开第一条URL
                String url = "https://www.bilibili.com/video/av" + nums.get(0);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                if (nums.size() > 1) {
                    // AV号多于一个，不直接打开哔哩哔哩
                    startActivity(Intent.createChooser(intent, "打开哔哩哔哩视频(" + nums.get(0) + ")"));
                } else {
                    // 只检测到一个AV号
                    SharedPreferences sp = getSharedPreferences(Const.PREFERENCES_NAME, MODE_PRIVATE);
                    int id = sp.getInt(Const.ID, Const.ID_WEBVIEW);
                    if (id == Const.ID_BILI) {
                        ComponentName cmp = new ComponentName("tv.danmaku.bili", "tv.danmaku.bili.ui.intent.IntentHandlerActivity");
                        intent.setComponent(cmp);
                    } else if (id == Const.ID_BILI_BLUE) {
                        ComponentName cmp = new ComponentName("com.bilibili.app.blue", "tv.danmaku.bili.ui.intent.IntentHandlerActivity");
                        intent.setComponent(cmp);
                    }
                    startActivity(Intent.createChooser(intent, "打开哔哩哔哩视频(" + nums.get(0) + ")"));
                }
            }
        }
    }

    /**
     * 将所有检测到的URL显示在TextView上
     *
     * @param urls
     */
    private void showUrl(List<String> urls) {
        mTvMsg.setText("检测到AV号");
        String av = "";
        for (int i = 0; i < urls.size(); i++) {
            av += "https://www.bilibili.com/video/av" + urls.get(i) + "\n\n";
        }
        mTvLink.setText(av);
    }

    /**
     * 找到字符串中所有连续的数字
     *
     * @param text
     * @return
     */
    private static List<String> findNums(String text) {
        int length = text.length();
        List<String> nums = new ArrayList<>();  // 用来存储检测到的连续的数字

        boolean lastIsDigit = false;    // 上个字符是否是数字
        String curStr = null;           // 当前拼接的字符串
        char ch;                        // 当前字符
        for (int i = 0; i < length; i++) {
            ch = text.charAt(i);
            if (ch >= '0' && ch <= '9') {
                if (lastIsDigit) {
                    curStr += String.valueOf(ch);
                } else {
                    curStr = String.valueOf(ch);
                }
                if (i == length - 1) {
                    nums.add(curStr);
                }
                lastIsDigit = true;
            } else {
                if (lastIsDigit) {
                    nums.add(curStr);
                }
                lastIsDigit = false;
            }
        }
        return nums;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
