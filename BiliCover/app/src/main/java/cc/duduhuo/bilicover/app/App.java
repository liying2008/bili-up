package cc.duduhuo.bilicover.app;

import android.app.Application;

import cc.duduhuo.applicationtoast.AppToast;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/22 8:33
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppToast.init(this);
    }
}
