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
public class PhoneRobotWorker {

    String indexUrl = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100505&from=page_100505_profile&wvr=6&mod=data&is_all=1&pagebar=0&pl_name=Pl_Official_MyProfileFeed__23&id=1005051583933441&script_uri=/p/1005051583933441/home&feed_type=0&page=1&pre_page=1&domain_op=100505&__rnd=1481506032655";//"http://m.weibo.cn/p/index?containerid=102803//";//http:d.weibo.com/102803";//https://www.pzea.com/buy/11";//https://www.pzea.com/cart.php?a=confproduct&i=0";


    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public static void main(String[] args) throws Exception {
        PhoneRobotWorker r = new PhoneRobotWorker();
        while (true) {
            r.work2();
        }
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
        String getUrl = "http://ord01.vmall.com/order/pwm86t/createOrder.do?" +
                "sign=F54B029A6CED7BE63193AB3BEB1C10E62BE8D3DC22849BACEC093F51D55C749E" +
                "&data=1I15+g8H9Od+BkaOCJfSt+JDSHPeYdQ97PeYjtWdcBEQvehsGSJPo7Vak9rGIO+GUtvjK9N2ujXqDlfHlJLFjPBx0s37dPvOAjTELJ/3pSEiWejUgp+mL95K/9P7I8gFDwHiIRwZab1tBxjd3tkRQWCeaDPuRqnPn06QtEroMbkHgwX0fuTsoCsXHjBaQ8D6ZZiCSV8Z8CJknHVJt0rnmsqNGvrTySTZWn472/iUyFQJDbgaZlUV4neqpt+zyC4lVlDjo3lxHIxShDmAknWXz8komVJRa9t3ck53HqNYK/zvj3QjnZGSywFkEy/Ccwg/eXI9YPeRpXuBVL/bOskez4KQLv5HiLEotmJ/N/koYzgcE2pV/P9V6iIZXVmhbDZLVL7KpkAmaSFz+7ny/NLh3PefrqTiUWBDZYPr237Cus9jjnuP6iJwojkT2ztBxn3pmaQsTWIhyhz2kQOUXc914Af3s7P0obwBX8GRLO56VkFOJXzhNd/7m5oeZmURKxQJPI3T+XqYPRqysnjcxwn3fWiGcNat9h1+0FeAFcKeXCKM+OAoL+dRD3OoimM5buBsvm8gqbPnOZzhZdS9VJ4/9/VjbquVUvSo54RQw/mfu7eipVl+VADOZyO7+SlQ1clfmWXUV1I/rQVROZ4nlO27fGsmwWpqkWb4JqVJZ86eyDt3z2pwrQXYwFo1EkWz8mu79v5c8f9Sbhf4oSkgUUC+yNPKPbQccZggJXWTFsw=" +
                "&uid=80086000132362233&skuId=138565392&skuIds=138565392&callback=callback1487043272042&t="+System.currentTimeMillis();

        try {
            /**
             * GET /order/pwm86t/createOrder.do?sign=85CAB322DB21AC40A1C77F45CF73EABD4FBDF3C4020718D024DB227EE7F12136&data=DA%2BfZe3Rb6s0iEjOf7WvXPnjx2tl%2BmhJDIEU0fkaetbOYWn2ry72jbnwt6d5MeAHSg0XfJJDZPQm2doKIg1eCu8Diwxkts56hgF0hmzl3oZpn78J9Npiweo%2FNaYCzUVlwGpX7xiXQdqg9gcFeDVIrdnyytkpo0RFcmHgf8qR5kY7Hxc0d5KAK7WCafj7Psz8v1N6e2MWBPKLN2CwH4V8oKUOtoY8k1xicPjt9F3D%2FCD602wUWrZ3XaZeyea0N7mwawwExnCNOl3p%2Fjik4%2FezSqRw5b4jFpdwjSVvT5e%2FQEPOEliRKA%2BYa3HJ9EFhgA1DKI7i%2BP%2F3VG%2B0BwRNRJms0MulAPQ2%2BKEwaU4b7BxIyRT2Ju%2Fpo046DeMJ%2B9ZeBO37RWiKcYjklMpetjK82JKxhOLuYo0gofQ%2FmYC%2FbJ2TJA760%2Bt2IXSaS8Fk%2Foa0bgwNTGvHE9bs7Ii2v2uaSGL%2Bp61z8LHupPrGlKUsadasqvxfCMV6CRpn%2BYoa2sqKaD8AyAL549tQF4GlQJDG3gd5bSZqbIHiZsYHFLEb2wT%2BSbWeCsRtvW5fydFTs7GKRaICFvuX1E14zoh6sgM5Z6JK%2FlTg%2BrxxYdonOQjL99JURryVo%2BNp1Q7FUrUncPV2IrM95hhGooQh6gFH4kgXeEFRxhzLKjAONRaQO0ZazhtRp1OzcvEc6p0oxa5VVtetlqicNh3oVVvE1qLECDmM%2FoBxXqjSm9wMzi58qdwYcEs%3D&uid=80086000132362233&skuId=138565392&skuIds=138565392&callback=callback1486999840823&t=1486999895845
             //             Host: ord01.vmall.com
             //             User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36
             //             Accept: * / *
             //            Referer: http://sale.vmall.com/cw6x.html
             //            Accept-Encoding: gzip, deflate, sdch
             //            Accept-Language: zh-CN,zh;q=0.8
             //            Cookie: _pk_hi_ssid=efc21ee7e9b146dc92551065b9f9d341; __gahuawei=GA1.2.1968616562.1486608137; _dmpa_ref=%5B%22%22%2C%22%22%2C1486997164%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3Dgm-RpYlx5qS0JwMaDTd09PNaS40QJsrCSGEpisWSoVQ232V7m6Q828SLQgCP_CAuYg8nW1HsW7jATcubKcF9hq%26wd%3D%26eqid%3Dc648b47c0000ebab00000006589aaa68%22%5D; isValCas=true; isAuthByUid=true; hasImg=1; isqueue-4880=2; queueSign-4880=10000999-f73f-7597-e3ac-a59c0bc7919b-24783291; queueTS-4880=24783290; cps_source=duomai; cps_channel=duomai; cps_id=6564; cps_wi=222430_387_0__1; CASTGC=TGT-530100-bVMAxY1zJGrrmACabRZYsIUBLBcYyZcolebxmqbJIx4dbtqWUI-cas; CASLOGIN=true; CASLOGINSITE=1; LOGINACCSITE=1; uid=80086000132362233; name=%E6%89%93%E7%AE%97%E5%85%A5%E6%89%8B%E5%BF%83%E6%9C%BA; user=%E6%89%93%E7%AE%97%E5%85%A5%E6%89%8B%E5%BF%83%E6%9C%BA; ts=1486998420901; valid=1; sign=85CAB322DB21AC40A1C77F45CF73EABD4FBDF3C4020718D024DB227EE7F12136; ticket=1ST-666892-shqHnf0CdSm6KKR7NMpQ-cas; hasphone=0; hasmail=1; logintype=1; euid=962a7e327dea4bdea56b13ff1f780120; isAuthCust=false; ac_li=true; ac_cp=1|0000000003|0000000004; __ukmc=f9de68b1ba2cffda8ec076fb333f41ca80086000132362233; ac_loNa=guin****%40***; ac_lel="guin****@***"; ac_lgc=0; ac_ltp=0; ac_lus=1; Hm_lvt_a08b68724dd89d23017170634e85acd8=1486532004,1486607909,1486997159,1486997806; Hm_lpvt_a08b68724dd89d23017170634e85acd8=1486999725; __uxmd=1486999840782-4e8cf3b8c4b09f319d63165c4ba000d0; _dmpa_ses_time=1487001640853; _dmpa_id=97567d6c9aad375424a1c200549241486529410106.1486532766.2.1486999841.1486609707.; _dmpa_ses=9201d16d6ad98e8070d470f95aba038ce0d02282

             */

            HttpGet get = new HttpGet(getUrl);
            get.addHeader("Host", "ord01.vmall.com");
            get.addHeader("Accept", "*/*");
            get.addHeader("Referer", "http://sale.vmall.com/cw6x.html");
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            get.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            get.addHeader("Connection", "keep-alive");
            get.addHeader("Cookie", "_pk_hi_ssid=efc21ee7e9b146dc92551065b9f9d341; __gahuawei=GA1.2.1968616562.1486608137; cps_source=duomai; cps_channel=duomai; cps_id=6564; cps_wi=222430_387_0__1; isqueue-4892=2; queueSign-4892=10000999-0ee2-d59c-c22e-d1f7a796c670-24783959; queueTS-4892=24783958; _dmpa_ref=%5B%22%22%2C%22%22%2C1487042844%2C%22http%3A%2F%2Fwww.vmall.com%2Fproduct%2F988240628.html%22%5D; CASTGC=TGT-531194-melDNX1IJCddvhLfI0XZ9mCw6ChjeW5SQIxktU41Sp3RIL9bAR-cas; CASLOGIN=true; CASLOGINSITE=1; LOGINACCSITE=1; hasImg=1; uid=80086000132362233; name=%E6%89%93%E7%AE%97%E5%85%A5%E6%89%8B%E5%BF%83%E6%9C%BA; user=%E6%89%93%E7%AE%97%E5%85%A5%E6%89%8B%E5%BF%83%E6%9C%BA; ts=1487043041400; valid=1; sign=F54B029A6CED7BE63193AB3BEB1C10E62BE8D3DC22849BACEC093F51D55C749E; ticket=1ST-679395-1oBNhB9BUHblRYO9tRtN-cas; hasphone=0; hasmail=1; logintype=1; ac_loNa=guin****%40***; ac_lel=\"guin****@***\"; ac_lgc=0; ac_ltp=0; ac_lus=1; ac_li=true; ac_cp=1|0000000003|0000000004; isAuthCust=false; isAuthByUid=true; Hm_lvt_a08b68724dd89d23017170634e85acd8=1487030433,1487031342,1487032676,1487042820; Hm_lpvt_a08b68724dd89d23017170634e85acd8=1487043218; __uxmd=1487043272003-543b42dfad5ac28aa1e3dd7d368b2da6; _dmpa_ses_time=1487045072080; _dmpa_id=97567d6c9aad375424a1c200549241486529410106.1486532766.5.1487043272.1487040493.; _dmpa_ses=0024903260c69bd50617707fee97e3636e8249cb");
            response = client.execute(get);
            Thread.sleep(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String context = HttpUtil.getResponseString(response);
        //获取html内容
        if (context != "") {
            System.out.println(context);
            String s = context.substring(context.indexOf(":")+1, context.length() - 2);
            if (s.equals("true")) {
                System.out.println("booooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooom");
            }
        }

    }
}
