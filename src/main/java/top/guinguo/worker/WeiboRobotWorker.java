package top.guinguo.worker;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import top.guinguo.utils.HttpUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

/**
 *
 * http://410063005.iteye.com/blog/1751243 ---->x509m
 * http://stackoverflow.com/questions/23201648/httpclient-4-3-x-fixing-deprecated-code-to-use-current-httpclient-implementatio  -->main
 * http://www.linuxidc.com/Linux/2016-04/130090.htm  -->nothing
 *
 */
public class WeiboRobotWorker {

    String indexUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005051583933441&script_uri=/p/1005051583933441/home&feed_type=0&page=1&pre_page=1&domain_op=100505&__rnd=1481506032655";//"http://m.weibo.cn/p/index?containerid=102803//";//http:d.weibo.com/102803";//https://www.pzea.com/buy/11";//https://www.pzea.com/cart.php?a=confproduct&i=0";


    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public static void main(String[] args) throws Exception {
        WeiboRobotWorker r = new WeiboRobotWorker();
        r.work2();
    }

    public static CloseableHttpClient test2() throws NoSuchAlgorithmException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLContext context = SSLContext.getInstance("SSL");

        X509TrustManager x509m = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };

        try {
            context.init(null, new TrustManager[] { x509m },
                    new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        builder.setSSLSocketFactory(sslConnectionFactory);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .build();

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

        builder.setConnectionManager(ccm);

        return builder
                .setUserAgent(HttpUtil.USER_AGEN)
                .build();
    }

    private static CloseableHttpClient buildSSLCloseableHttpClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            //信任所有
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        //ALLOW_ALL_HOSTNAME_VERIFIER:这个主机名验证器基本上是关闭主机名验证的,实现的是一个空操作，并且不会抛出javax.net.ssl.SSLException异常。
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return HttpClients.custom()
                .setUserAgent(HttpUtil.USER_AGEN)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setCookieSpec(
                                        CookieSpecs.BROWSER_COMPATIBILITY
                                ).build())
        .setSSLSocketFactory(sslsf).build();
    }

    /**
     */
    private void work2() throws Exception {
        CloseableHttpClient client = HttpUtil.httpClient;//buildSSLCloseableHttpClient();//test2();
        CloseableHttpResponse response = null;
//        String getUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1&page=2&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&pre_page=2&domain_op=100505&__rnd="+System.currentTimeMillis();
        String getUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=1&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&page=5&pre_page=5&domain_op=100505&__rnd="+System.currentTimeMillis();
        /**
         * 首页
         http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&page=1&pre_page=1&domain_op=100505&__rnd=1481511039151
         http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=1&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&page=1&pre_page=1&domain_op=100505&__rnd=1481511066806

         http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&page=1&pre_page=1&domain_op=100505&__rnd=1481511066806

         非首页
         http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1&page=2&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&pre_page=2&domain_op=100505&__rnd=1481511225451
         http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1&page=2&pagebar=1&pl_name=Pl_Official_MyProfileFeed__23&id=1005055652557385&script_uri=/p/1005055652557385/home&feed_type=0&pre_page=2&domain_op=100505&__rnd=1481511225451
         * */
        try {
            HttpGet get = new HttpGet(getUrl);
            get.addHeader("Cache-Control","max-age=0");
            get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            get.addHeader("Accept-Encoding","gzip, deflate, sdch");
            get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            get.addHeader("X-Requested-With","XMLHttpRequest");
            get.addHeader("DNT","1");
            get.addHeader("Connection","keep-alive");
            //get.addHeader("Cookie","SINAGLOBAL=6906646124383.204.1470301683033; login_sid_t=c3e96e2074d5f187ef7c91699ed56c1e; YF-Ugrow-G0=5b31332af1361e117ff29bb32e4d8439; YF-V5-G0=731b77772529a1f49eac82a9d2c2957f; _s_tentry=www.baidu.com; UOR=sishuok.com,widget.weibo.com,www.baidu.com; Apache=8252986808197.762.1481504666682; ULV=1481504666688:35:4:1:8252986808197.762.1481504666682:1481292173938; YF-Page-G0=091b90e49b7b3ab2860004fba404a078; WBStorage=2c466cc84b6dda21|undefined; WBtopGlobal_register_version=7e41aace8a002161; SCF=AhqCMKNqoxiYNPEEllzp-9mtSYiJLr0yxYYuwWPeEIGV5fMKpONRnransdVIqCRIksGBPhtjqXApXuZaiUQaokI.; SUB=_2A251SnboDeRxGedL41EY8y3Izz2IHXVWPu8grDV8PUJbmtBeLU3MkW8dle4y7SVGO_6e6JJMG7cxFDJ8zA..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5BdzJ9n8xZjH6_MgpLKU.S5JpX5K2hUgL.Fo2f1he4e0eXSh22dJLoIEWGdcvadcvadcvaqg7_TCH8SEHW1C-RSbH8SC-RBb-4entt; SUHB=0ftoRo1ySeMtT5; SSOLoginState=1481508536; un=1035935757@qq.com");
            //get.addHeader("Cookie","SINAGLOBAL=6906646124383.204.1470301683033; login_sid_t=c3e96e2074d5f187ef7c91699ed56c1e; YF-Ugrow-G0=5b31332af1361e117ff29bb32e4d8439; YF-V5-G0=731b77772529a1f49eac82a9d2c2957f; _s_tentry=www.baidu.com; Apache=8252986808197.762.1481504666682; ULV=1481504666688:35:4:1:8252986808197.762.1481504666682:1481292173938; YF-Page-G0=091b90e49b7b3ab2860004fba404a078; WBtopGlobal_register_version=7e41aace8a002161; SCF=AhqCMKNqoxiYNPEEllzp-9mtSYiJLr0yxYYuwWPeEIGV5fMKpONRnransdVIqCRIksGBPhtjqXApXuZaiUQaokI.; SUHB=0ftoRo1ySeMtT5; SUB=_2AkMvEoIjdcPhrAJUkPscz2jmaIxH-jzEiebBAn7uJhMyAxgv7nQeqSV99C3UvHofliW-WpO8_3LQr-uVxg..; SUBP=0033WrSXqPxfM72wWs9jqgMF55529P9D9W5BdzJ9n8xZjH6_MgpLKU.S5JpV2K27e0-4e0-NSKU5MP2Vqcv_; WBStorage=2c466cc84b6dda21|undefined; UOR=sishuok.com,widget.weibo.com,login.sina.com.cn");
            get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");
            response = client.execute(get);
            Thread.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        //获取html内容
        if (context != "") {
            /**
             * 筛选出需要的div，有20条记录对应20个div
             */
            JSONObject jsonObject = JSONObject.parseObject(context);
            System.out.println(jsonObject.get("data"));
            //List<String> l = HttpUtil.getContext(HttpUtil.regEx_div, context);
            /*for (int i = l.size()-1; i >= 0; i--) {
                //从每一页底部开始处理数据
                String div = l.get(i);
                long createTime = 0L;
                try {
                    String createTime0 = div.substring(div.indexOf("job_date\">") + 10
                            , div.indexOf("</span></as"));
                    *//**
             * 由于它的时间被处理过，需要重新语义化
             *//*
                    Date now = new Date();//现在的时间
                    if (createTime0.contains("天")) {
                        createTime0 = createTime0.substring(0, createTime0.indexOf("天"));
                        createTime = now.getTime()-(86400000 * Integer.parseInt(createTime0));
                    } else if (createTime0.contains("小时")) {
                        createTime0 = createTime0.substring(0, createTime0.indexOf("小时"));
                        createTime = now.getTime()-(3600000 * Integer.parseInt(createTime0));
                    } else if (createTime0.contains("分钟")) {
                        createTime0 = createTime0.substring(0, createTime0.indexOf("分钟"));
                        createTime = now.getTime()-(60000 * Integer.parseInt(createTime0));
                    } else if (createTime0.contains("刚刚")) {
                        createTime = now.getTime()-60000;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (createTime <= lastTime2 ) {
                    continue;
                } else {
                    lastTime2 = createTime;
                }
                String title = div.substring(div.indexOf("<h3>") + 4, div.indexOf("</h3>"));
                String wage = div.substring(div.indexOf("<strong>") + 8, div.indexOf("</strong>"));
                if (wage.contains("-")) {
                    String[] m = wage.split("-");
                    *//**
             * 6000 - 7999 月薪转日新
             * +，除以2，除以25天
             *//*
                    wage = (Integer.parseInt(m[0].trim()) + Integer.parseInt(m[1].trim())) / 50 + "元";
                } else wage = "面议";
                if (title.contains("<font")) {
                    title = title.substring(title.indexOf(">") + 1, title.indexOf("</font>"));
                }
                *//**
             * 每一条数据的详情地址，用于获取其他信息
             *//*
                String detailUrl = div.substring(div.indexOf("href=\"") + 6, div.indexOf(".html")) + ".html";
                *//**
             * 用GET请求获取页面数据
             *//*
                String detail = null;
                try {
                    detail = HttpUtil.getResponseString(client.execute(new HttpGet(detailUrl)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String startDate = detail.substring(detail.indexOf("com_show_time\">") + 15
                        , detail.indexOf("</span>"));
                String address = detail.substring(detail.indexOf("\"user_map fa fa-map-marker\"></i>") + 32
                        , detail.indexOf("<i class=\"user_map fa fa-jpy\""));
                String activity_category = detail.substring(detail.indexOf("性质：</span>") + 10
                        , detail.indexOf("<span class=\"user_contnet_info_n\" "));
                String remarks = detail.substring(detail.indexOf("职位描述</span></div>") + 17
                        , detail.indexOf("联系方式</span>"));
                remarks = HttpUtil.replaceHtml(HttpUtil.regEx_html, remarks, "");
                String remark = HttpUtil.replaceHtml(HttpUtil.regEx_space, remarks, "");
                *//**
             * 去除掉空格
             *//*
                if (remark.contains("&nbsp;")) remark = remark.replaceAll("&nbsp;", "");
                if (remark.startsWith("      ")) remark = remark.replaceAll("      ", "");

                String contact1 = detail.substring(detail.indexOf("[联系人]") + 5
                        , detail.indexOf("[联系人]") + 8);
                if (contact1.contains("<")) contact1 = contact1.replace("<", "");
                String phone = detail.substring(detail.indexOf("[联系手机]") + 6
                        , detail.indexOf("[联系手机]") + 17);
                String contact = contact1 + " " + phone;
                logger.debug("抓取：" + title + activity_category + activity_category + address + startDate + contact + phone);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }

    }
}
