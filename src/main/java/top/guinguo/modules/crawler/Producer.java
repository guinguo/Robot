package top.guinguo.modules.crawler;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @描述: 生产出35个视频url
 * @作者: guin_guo
 * @日期: 2017-04-14 10:12
 * @版本: v1.0
 */
public class Producer implements Runnable {

    private BlockingQueue<Video> queue;

    public static void main(String[] args) {
        new Producer().run();
    }

    public Producer() {
    }

    public Producer(BlockingQueue<Video> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        CloseableHttpClient client = HttpUtil.httpClient;
        CloseableHttpResponse response = null;
        //视频列表
        String baseURL = "http://video.chaoxing.com/";
        String getUrl1 = baseURL + "play360app_400004003_30662.shtml";
        try {
            HttpGet get = new HttpGet(getUrl1);
            get.addHeader("Cache-Control","max-age=0");
            get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            get.addHeader("Accept-Encoding","gzip, deflate, sdch");
            get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            response = client.execute(get);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        //获取html内容
        if (context != "") {
            /**
             * 筛选出需要的ul li，有35条记录对应35个li
             */
            Document doc = Jsoup.parse(context);
            Elements ul = doc.getElementsByTag("ul").first().getElementsByTag("li");
            for (Element li : ul) {
                Elements as = li.getElementsByTag("a");
                Element a = as.first();
                String href = getVideoSrc(a.attr("href"));
                if (href == null) {
                    continue;
                }
                String filename = a.html();
                String title = a.attr("title");
                Video v = new Video(filename, title, href);
                queue.add(v);
            }
        }
    }

    private String getVideoSrc(String href) {
        CloseableHttpClient client = HttpUtil.httpClient;
        CloseableHttpResponse response = null;
        String ajaxURL = "http://video.chaoxing.com/videourl" + (href).substring(4);
        try {
            HttpGet get = new HttpGet(ajaxURL);
            get.addHeader("Cache-Control","max-age=0");
            get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            get.addHeader("Accept-Encoding","gzip, deflate, sdch");
            get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            response = client.execute(get);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        //获取html内容
        if (context != "") {
            /**
             * 得到视频 下载url
             */
            JSONObject jsonObject = JSONObject.parseObject(context);
            Object vs = jsonObject.get("videoUrl");
            return vs != null ? vs.toString() : null;
        }
        return null;
    }
}
