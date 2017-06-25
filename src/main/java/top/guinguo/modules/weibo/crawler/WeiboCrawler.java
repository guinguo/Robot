package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;

/**
 * 微博抓取器
 */
public class WeiboCrawler {

    public static void main(String[] args) throws Exception {
        WeiboCrawler r = new WeiboCrawler();
        r.work();
    }

    private void work() throws Exception {
        CloseableHttpClient client = HttpUtil.httpClient;
        CloseableHttpResponse response = null;
        String getUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&id=1005055652557385&pagebar=0&page=1&pre_page=0";
        try {
            HttpGet get = new HttpGet(getUrl);
            get.addHeader("Cache-Control","max-age=0");
            get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            get.addHeader("Accept-Encoding","gzip, deflate, sdch");
            get.addHeader("X-Requested-With","XMLHttpRequest");
            get.addHeader("DNT","1");
            get.addHeader("Connection","keep-alive");
            get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        if (context != "") {
            JSONObject jsonObject = JSONObject.parseObject(context);
            System.out.println(jsonObject.get("data"));
        }
    }
}
