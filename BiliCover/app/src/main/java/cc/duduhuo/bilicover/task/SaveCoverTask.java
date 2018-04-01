package cc.duduhuo.bilicover.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.bilicover.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/22 11:18
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class SaveCoverTask extends AsyncTask<String, Void, Boolean> {
    private WeakReference<Context> mContextRef;

    public SaveCoverTask(Context context) {
        this.mContextRef = new WeakReference<>(context);
    }

    /**
     * @param params 第一个参数是封面URL
     * @return
     */
    @Override
    protected Boolean doInBackground(String... params) {
        FutureTarget<File> target = Glide.with(mContextRef.get()).load(params[0]).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        String coverPath = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DOWNLOADS +
            File.separator + "BILI_" + System.currentTimeMillis() + ".jpg";
        Log.d("bili", coverPath);
        try {
            File file = target.get();
            if (file.exists()) {
                // 拷贝下载的头像文件到SD卡下的应用工作目录
                FileUtils.copyFile(file.getAbsolutePath(), coverPath);
                file.delete();
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            AppToast.showToast("封面已保存至 " + Environment.DIRECTORY_DOWNLOADS + " 目录下");
        } else {
            AppToast.showToast("封面保存失败");
        }
    }
}
