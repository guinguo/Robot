package top.guinguo.modules.weibo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.Cell;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * @描述: 爬取工具类
 * @作者: guin_guo
 * @日期: 2017-07-07 23:23
 * @版本: v1.0
 */
public class CrawleUtils {

    public static final String WB_LONGTEXT_URL = "wb.longtext.url";
    private static String wbLongTextURL;

    static {
        Configurator configurator = Configurator.getInstance();
        wbLongTextURL = configurator.get(WB_LONGTEXT_URL);
    }
    private CrawleHttpFactory crawleHttpFactory = CrawleHttpFactory.getInstance();

    public CrawleUtils() {
    }
    private static class DefaultInstance {
        public static CrawleUtils instance = new CrawleUtils();
    }
    public static CrawleUtils getInstance() {
        return DefaultInstance.instance;
    }

    /**
     * 从类似httpget请求参数中获取name=key的值
     * @param source a=1&b=4
     * @param key a
     * @return 1
     */
    public static String getValue(String source, String key) {
        String[] kvs = source.split("&");
        for (String s : kvs) {
            if (s.startsWith(key)) {
                return s.substring(key.length() + 1);
            }
        }
        return "";
    }

    /**
     * 获取微博id为mid的全文，组装成一个div节点返回
     * @param mid
     * @return
     * @throws IOException
     */
    public Element getLongText(String mid) throws IOException {
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
        String respStr = crawleHttpFactory.getRespStr(client, String.format(wbLongTextURL, mid));
        client.close();
        JSONObject json = JSON.parseObject(respStr);
        /**
         * "100000" -->ok
         * "100001" -->error
         */
        if ("100000" .equals(json.getString("code"))) {
            String html = json.getJSONObject("data").getString("html");
            Attributes attrs =  new Attributes();
            attrs.put("node-type", "feed_list_reason_full");
            Element div = new Element(Tag.valueOf("div"),"",attrs);
            div.append(html);
            return div;
        }
        return null;
    }

    public static void dealCell(Map<String, Object> resultMap, Cell cell) throws UnsupportedEncodingException {
        String rowKey = new String(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength(), "UTF-8");
        String family = new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength(), "UTF-8");
        String qualifier = new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength(),
                "UTF-8");
        String value = new String(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength(), "UTF-8");
         System.out.print("RowKey=>"+rowKey+"->"+family+":"+qualifier+"="+value+"||");
        resultMap.put("rowKey", rowKey);
        resultMap.put("family", family);
        resultMap.put(qualifier, value);
    }

}
