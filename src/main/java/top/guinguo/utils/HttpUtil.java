package top.guinguo.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class HttpUtil {
    // 定义script的正则表达式
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    // 定义style的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    // 定义HTML标签的正则表达式
    public static final String regEx_html = "<[^>]+>";
    // 定义空格回车换行符的正则表达式
    public static final String regEx_space = "\t|\r|\n";
    // 获取div的正则表达式
    public static final String regEx_div = "<div class=\"job_list_box\">.*?</div>";
//    public static String USER_AGEN = "Mozilla/5.0 (Linux; U; Android 2.1-update1; de-de; HTC Desire 1.19.161.5 Build/ERE27) " +
//    "AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
    public static String USER_AGEN = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

    // 定义script的正则表达式
    private static final String wb_script = "<script[^>]*?>!(^(\\s+))<\\/script>";
    /**
     * 自定义HttpClient，可以模拟手机请求
     */
    public static CloseableHttpClient httpClient = HttpClients
            .custom()
            .setUserAgent(USER_AGEN)
            .setDefaultRequestConfig(
                    RequestConfig.custom()
                            .setCookieSpec(
                                    CookieSpecs.BROWSER_COMPATIBILITY
                            ).build()).build();
    /**
     * 自定义wb HttpClient
     */
    public static CloseableHttpClient wbClient = HttpClients.custom().setUserAgent(USER_AGEN).build();

    /**
     * 自定义longtext wb HttpClient
     */
    public static CloseableHttpClient wbLongTextClient = HttpClients.custom().setUserAgent(USER_AGEN).build();

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        return sendGet(url, param, "UTF-8");
    }
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param charSet
     *             读取响应的编码格式，null时会抛异常
     * @return URL 所代表远程资源的响应结果
     * @throws java.io.UnsupportedEncodingException
     */
    public static String sendGet(String url, String param,String charSet) {
        StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Linux; U; Android 2.1-update1; de-de; HTC Desire 1.19.161.5 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),charSet));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * 登录后返回带登录状态的HttpClient，以便抓取登录权限才拥有的数据
     *
     * @param url
     *              action地址，
     * @param params
     *              参数
     * @return
     */
    public static CloseableHttpClient getLoginClient(String url, List<NameValuePair> params) {
        try {
            HttpPost post = new HttpPost(url);
            //准备参数
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(params);
            //设置参数
            post.setEntity(postEntity);
            CloseableHttpResponse response = httpClient.execute(post);
            System.out.println(url+"--->"+response.getStatusLine());
            /*System.out.println(getResponseString(response));*/
        } catch (Exception e) {
            System.out.println("登录出现异常！");
            e.printStackTrace();
            return null;
        }
        return httpClient;
    }

    /**
     * 从response中抽取出内容返回，分离出css跟js
     *
     * @param response
     *          HttpResponse
     * @return
     */
    public static String getResponseString(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        //判断相应是否为空
        if (entity != null) {
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity);//.replaceAll("\r\n", "");
                responseString = replaceHtml(regEx_script, responseString,"");
                responseString = replaceHtml(regEx_style, responseString,"");
                return responseString;
            } catch (IOException e) {
                System.out.println("出现IO异常");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     * 用正则过滤掉html的一部分后返回结果
     * @param regex
     *              正则表达式
     * @param html
     *              html内容
     * @param replaceBy
     *              替换内容
     * @return
     */
    public static String replaceHtml(String regex, String html,String replaceBy) {
        Pattern p_script = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(html);
        html = m_script.replaceAll(replaceBy); // 过滤标签
        return html;
    }

    /**
     *
     * 从字符串匹配出同类字符串
     * @param regex
     *              正则表达式
     * @param s
     *              源字符串
     * @return
     */
    public static List<String> getContext(String regex, final String s) {
        final List<String> list = new ArrayList<>();
        //regex2 =  "(href=[\"\']*)([^\"\']*[\"\'])"; -----href
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group().trim());
        }
        return list;
    }


    /**
     * 从response中抽取出内容返回，分离出css跟js
     *
     * @param response
     *          HttpResponse
     * @return
     */
    public static String getWeiboMainResp(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        //判断相应是否为空
        if (entity != null) {
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity);
                responseString = replaceHtml(regEx_style, responseString,"");
                responseString = replaceHtml(wb_script, responseString,"");
                return responseString;
            } catch (IOException e) {
                System.out.println("出现IO异常");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从response中抽取出内容返回，分离出css跟js
     *
     * @param response
     *          HttpResponse
     * @return
     */
    public static String getRespString(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        //判断相应是否为空
        if (entity != null) {
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity);//.replaceAll("\r\n", "");
                return responseString;
            } catch (IOException e) {
                System.out.println("出现IO异常");
                e.printStackTrace();
            }
        }
        return null;
    }
}
