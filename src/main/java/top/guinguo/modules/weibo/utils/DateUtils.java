package top.guinguo.modules.weibo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @描述: 时间转换工具类
 * @作者: gzguoguinan
 * @日期: 2017-07-19 15:16
 * @版本: v1.0
 */
public class DateUtils {
    public static final String PATTERN = "yyyy-MM-dd HH:mm";

    private static SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

    public static final String GENERAL_REG = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}";
    public static final String CURRENT_YEAR_REG = "\\d{2}-\\d{2}\\s\\d{2}:\\d{2}";
    public static final String TODAY_REG = "今天\\s\\d{2}:\\d{2}";

    public static void main(String[] args) throws Exception {
        String[] ds = {"今天 01:20", "07-18 10:49", "2016-05-27 09:31", "2015-07-14"};
        for (String s : ds) {
            if (s.matches(GENERAL_REG)) {
                Date date = parse(s);
                System.out.println(format(date));
            } else if (s.matches(CURRENT_YEAR_REG)) {
                Calendar c = Calendar.getInstance();
                Date date = parse(c.get(Calendar.YEAR) + "-" + s);
                System.out.println(format(date));
            } else if (s.matches(TODAY_REG)) {
                Calendar c = Calendar.getInstance();
                Date date = parse(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + s.split(" ")[1]);
                System.out.println(format(date));
            }
        }
    }

    public static Date parse(String str) {
        if (str.matches(CURRENT_YEAR_REG)) {
            Calendar c = Calendar.getInstance();
            str = c.get(Calendar.YEAR) + "-" + str;
        } else if (str.matches(TODAY_REG)) {
            Calendar c = Calendar.getInstance();
            str = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + str.split(" ")[1];
        }
        Date result = null;
        synchronized (sdf) {
            try {
                result = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String format(Date date) {
        String result = "";
        synchronized (sdf) {
            try {
                result = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String parseAndFormat(String str) {
        return format(parse(str));
    }
}