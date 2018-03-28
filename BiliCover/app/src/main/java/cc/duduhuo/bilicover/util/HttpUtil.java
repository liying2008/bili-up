package cc.duduhuo.bilicover.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * =======================================================
 * Author: liying - liruoer2008@yeah.net
 * Datetime: 2018/3/28 22:19
 * Description:
 * Remarks:
 * =======================================================
 */
public class HttpUtil {
    /**
     * 获取请求结果
     *
     * @param url url
     * @return
     * @throws IOException
     */
    public static String getData(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        InputStream inStream = conn.getInputStream();
        byte[] data = readInputStream(inStream);
        return new String(data, "utf-8");
    }

    private static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] byteArray = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return byteArray;
    }
}
