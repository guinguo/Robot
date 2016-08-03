package top.guinguo.worker;

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
import org.apache.log4j.Logger;
import top.guinguo.utils.HttpUtil;


import javax.net.ssl.*;
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
public class RobotWorker {

    String indexUrl = "https://www.pzea.com/cart.php?a=add&pid=11";//https://www.pzea.com/buy/11";//https://www.pzea.com/cart.php?a=confproduct&i=0";


    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public static void main(String[] args) throws Exception {
        RobotWorker r = new RobotWorker();
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
        CloseableHttpClient client = buildSSLCloseableHttpClient();//test2();
        String getUrl = indexUrl;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(new HttpGet(getUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        //获取html内容
        if (context != "") {
            /**
             * 筛选出需要的div，有20条记录对应20个div
             */
            System.out.println(context);
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
