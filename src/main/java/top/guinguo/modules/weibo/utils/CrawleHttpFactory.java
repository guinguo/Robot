package top.guinguo.modules.weibo.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;


/**
 * @描述: Http工厂方法
 * @作者: guin_guo
 * @日期: 2017-07-07 23:25
 * @版本: v1.0
 */
public class CrawleHttpFactory {
    public CrawleHttpFactory (){}

    private static class DefaultInstance {
        public static CrawleHttpFactory instance = new CrawleHttpFactory();
    }

    public static CrawleHttpFactory getInstance(){
        return DefaultInstance.instance;
    }

    /**
     * 生成 me 的get请求
     * @param url
     * @return
     */
    public HttpGet generateGet(String url) {
        HttpGet get = new HttpGet(url);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","close");
        get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");
        /*get.addHeader("Cookie",
                "SUB=_2A250Z3PRDeRhGeBN6lES9inKzTuIHXVXFeIZrDV8PUJbmtAKLRndkW9tjhR50Qn_CJDGpqtoDLuMieltPg..;" +
                        "SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWnRAMTAgdR7ymOnxOqQmIq5JpX5K2hUgL.Foq0eKe0SoMcSoM2dJLoI0nLxKBLBonL1h5LxKML1 - zLBoBLxK - L12BL1K - LxKnLB.2LB - zLxKMLB.zL1KeLxKqL1--L1 - S31Btt;");*/
        return get;
    }

    /**
     * 根据url获取请求内容并返回
     * http get response --> string
     * @param url
     * @return
     * @throws IOException
     */
    public String getRespStr(CloseableHttpClient client, String url) throws IOException {
        HttpGet get = new HttpGet(url);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","close");
        get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");

        CloseableHttpResponse response = client.execute(get);
        String respStr = HttpUtil.getRespString(response);
        response.close();
        return respStr;
    }

}
