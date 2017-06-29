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
        String mainUrl = "http://weibo.com/p/1005055652557385";
        try {
            HttpGet get = generateGet(mainUrl);
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context0 = HttpUtil.getResponseString(response);
        if (context0.length() < 100) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONObject.parseObject(context0);
            } catch (Exception e) {
                //TODO continue
                e.printStackTrace();
                System.exit(0);
            }
            if ("100001".equals(jsonObject.get("code"))) {
                //TODO continue
                System.exit(0);
            }
        }
        System.out.println(context0);
        /*String dataUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&id=1005055652557385&pagebar=0&page=1&pre_page=0";
        try {
            HttpGet get = generateGet(dataUrl);
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        if (context != "") {
            JSONObject jsonObject = JSONObject.parseObject(context);
            System.out.println(jsonObject.get("data"));
        }*/
    }

    private HttpGet generateGet(String url) {
        HttpGet get = new HttpGet(url);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");
        return get;
    }
}
