package cc.duduhuo.bilicover.util;

import java.util.Locale;

/**
 * =======================================================
 * Author: liying - liruoer2008@yeah.net
 * Datetime: 2018/4/1 20:03
 * Description:
 * Remarks:
 * =======================================================
 */
public class NumberFormatter {
    /**
     * 格式化数字
     *
     * @param count 数字
     * @return
     */
    public static String formatNumber(int count) {
        int base = 10000;
        if (count < base) {
            return String.valueOf(count);
        } else if (count < 100000000) {
            double d = count / 10000.0;
            return String.format(Locale.getDefault(), "%.1f", d) + "万";
        } else {
            double d = count / 100000000.0;
            return String.format(Locale.getDefault(), "%.2f", d) + "亿";
        }
    }
}
