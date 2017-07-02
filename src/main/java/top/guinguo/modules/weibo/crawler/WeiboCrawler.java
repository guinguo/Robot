package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * 微博抓取器
 */
public class WeiboCrawler {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    public static void main(String[] args) throws Exception {
        WeiboCrawler r = new WeiboCrawler();
        r.work();
    }

    private void work() throws Exception {
        CloseableHttpClient client = HttpUtil.httpClient;
        CloseableHttpResponse response = null;
        String mainUrl = "http://weibo.com/p/100505";
        for (long i = 5;i<6;i++) {
            String uid = "565255738" + i;
            try {
                HttpGet get = generateGet(mainUrl + uid);
                response = client.execute(get);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (response.getFirstHeader("Content-Type").getValue().startsWith("application/json;")) {
                System.out.println("[404]user not fund");
                continue;
            }
            String context0 = HttpUtil.getWeiboMainResp(response);
            int wbcount = getUserInfo(context0);
            crawlerWeibo(wbcount, uid);
        }
    }

    private int getUserInfo(String html) {
        //1. id昵称等
        String regex = "<script type=\"text/javascript\">\\s+var \\$CONFIG = \\{\\};([\\s\\S]*?)?</script>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html.toString());
        String result = "";
        User user = new User();
        JSONObject meta = new JSONObject();
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        String[] ss = result.split("\\n");
        for (String s : ss) {
            s = s.trim();
            if (!s.isEmpty()) {
                if (s.startsWith("$CONFIG['oid']")) {
                    String id = s.split("=")[1];
                    System.out.println("oid: " + id);
                    user.setId(id);
                } else if(s.startsWith("$CONFIG['page_id']")) {
                    System.out.println("page_id: "+s.split("=")[1]);
                } else if(s.startsWith("$CONFIG['onick']")) {
                    String nickname = s.split("=")[1];
                    System.out.println("昵称: " + nickname);
                    user.setNickname(nickname);
                }
            }
        }
        //1.2 性别是否会员
        regex = "<script>FM.view\\(\\{\"ns\":\"pl.header.head.index\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        JSONObject json = JSON.parseObject("{"+result+"}");
        String htmldata = json.getString("html");
        Document document = Jsoup.parse(htmldata);
        String photo = document.select("img[class=photo]").get(0).attr("src");
        user.setAvatar(photo);
        System.out.println("头像：" + photo);
        String intro = document.select(".pf_intro").first().html();
        user.setIntro(intro);
        System.out.println("简介：" + intro);
        Element div = document.select(".pf_username").get(0);
        String username = div.select(".username").get(0).html();
        System.out.println("用户名：" + username);
        user.setUsername(username);
        String sex = (div.select("a").get(0).select("i").get(0).hasClass("icon_pf_male") ? "男" : "女");
        System.out.println("性别： " + sex);
        user.setSex(sex);
        Elements wbmenber = div.select("em[class^=W_icon icon_member]");
        if (wbmenber.size() > 0) {
            Set<String> clzz = wbmenber.first().classNames();
            int menlevel = 0;
            for (String c : clzz) {
                if (c.startsWith("icon_member")) {
                    menlevel = Integer.parseInt(c.substring(11));
                }
            }
            System.out.println("微博会员：" + menlevel);
            user.setMember(menlevel);
        }
        //2. 粉丝数等
        regex = "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        json = JSON.parseObject("{"+result+"}");
        htmldata = json.getString("html");
        document = Jsoup.parse(htmldata);
        Elements as = document.select(".t_link");
        int wbcount = 0;
        for (Element a : as) {
            Element cnd = a.select(".S_txt2").get(0);
            String cn = cnd.html();
            int number = Integer.parseInt(cnd.previousElementSibling().html());
            System.out.println(cn+": "+number);
            if (cn.equals("关注")) {
                user.setFocus(number);
            }
            if (cn.equals("粉丝")) {
                user.setFans(Long.valueOf(wbcount));
            }
            if (cn.equals("微博")) {
                wbcount = number;
                user.setBlogNumber(Long.valueOf(wbcount));
            }
        }
        //3个人信息等
        regex = "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__6\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        json = JSON.parseObject("{"+result+"}");
        htmldata = json.getString("html");
        document = Jsoup.parse(htmldata);
        as = document.select(".W_icon_level span");
        String level = as.get(0).html().substring(3);
        System.out.println("level: " + level);
        user.setLevel(Integer.parseInt(level));
        String addr = document.select(".ficon_cd_place").get(0).parent().nextElementSibling().html();
        System.out.println("place: "+addr);
        user.setAddress(addr);
        String birthDay = document.select(".ficon_constellation").get(0).parent().nextElementSibling().html();
        System.out.println("birthday: "+birthDay);
        try {
            user.setBirthDate(sdf.parse(birthDay));
        } catch (ParseException e) {
        }
        as = document.select(".ficon_link");
        if (as.size() > 0) {
            String link = as.get(0).parent().nextElementSibling().html();
            meta.put("link", link);
            user.setJSONMeta(meta);
            System.out.println("link: " + link);
        }
        as = document.select(".ficon_cd_coupon");
        if (as.size() > 0) {
            String tags = as.get(0).parent().nextElementSibling().select("a").get(0).html();
            System.out.println("tags: " + tags);
            user.setTags(tags);
        }
        return wbcount;
    }

    public void crawlerWeibo(int n, String uid) {
        try {
            HashMap<String, List> wbsmap = new HashMap<>();
            int i = 0;
            for (; i < n / 45 - 1; i++) {
                String prepage = i + "";
                String page = (i + 1) + "";
                System.out.println(prepage + "\t" + page + "\t" + 0);
                wbsmap.put(prepage + page + "0", getWb(uid, prepage, page, 0));
                System.out.println(page + "\t" + page + "\t" + 0);
                wbsmap.put(prepage + page + "0", getWb(uid, page, page, 0));
                System.out.println(page + "\t" + page + "\t" + 1);
                wbsmap.put(prepage + page + "0", getWb(uid, page, page, 1));
                System.out.println("---------");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int mod = n % 45;
            int div = mod / 15;
            if (mod == 0) div = 2;
            div++;
            List<Weibo> last = new ArrayList<>(45);
            switch (div) {
                case 3:
                    System.out.println(i + 1 + "\t" + (i + 1) + "\t" + 1);//31-45;array.reverse();
                    wbsmap.put((i + 1) +""+ (i + 1) + "1", getWb(uid, (i + 1) +"", (i + 1) +"", 1));
                    Collections.reverse(last);
                case 2:
                    System.out.println(i + 1 + "\t" + (i + 1) + "\t" + 0);//16-30;array.reverse();
                    wbsmap.put((i + 1) +""+ (i + 1) + "0", getWb(uid, (i + 1) +"", (i + 1) +"", 0));
                    Collections.reverse(last);
                case 1:
                    System.out.println(i + "\t" + (i + 1) + "\t" + 0);//1-15;array.reverse();
                    wbsmap.put((i) +""+ (i + 1) + "0", getWb(uid, (i) +"", (i + 1) +"", 0));
                    Collections.reverse(last);
            }
            Collections.reverse(last);
            System.out.println("---------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Weibo> getWb(String uid, String prepage, String page, int pagebar) throws IOException {
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
        String respStr = getRespStr(client, "http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&id=100505"+
                uid + "&pre_page=" + prepage + "&page=" + page + "&pagebar=" + pagebar);
        client.close();
        JSONObject json = JSON.parseObject(respStr);
        String html = json.getString("data");
        Document wbs = Jsoup.parse(html);
        Elements feedlist = wbs.select("div[action-type=feed_list_item]");
        List<Weibo> list = new ArrayList<>(15);
        for (Element feed : feedlist) {
            Weibo weibo = new Weibo();
            String id = feed.attr("mid");
            System.out.println("id："+id);
            weibo.setId(id);
            boolean isforward = "1".equals(feed.attr("isforward"));
            weibo.setForward(isforward);
            System.out.println("转发：" + isforward);
            if (isforward) {
                String minfo = feed.attr("minfo");
                String ru = getValue(minfo, "ru");
                String rm = getValue(minfo, "rm");
                System.out.println("原up主：" + ru);
                System.out.println("原id：" + rm);
                weibo.setForwarduid(ru);
                weibo.setForwardmid(rm);
            }
            String tbinfo = feed.attr("tbinfo");
            System.out.println("ouid：" + (isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
            weibo.setUid((isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
            String opt = feed.select(".WB_feed_detail .opt").get(0).attr("action-data");
            System.out.println("昵称：" + opt.split("&")[1].split("=")[1]);
            weibo.setNickname(opt.split("&")[1].split("=")[1]);

            Element datea = feed.select(".WB_detail a[name]").get(0);
            System.out.println("发布时间: "+datea.html()+"  "+datea.attr("date"));
            weibo.setCreateDate(new Date(Long.parseLong(datea.attr("date"))));
            System.out.println("来源：" + datea.nextElementSibling().html());
            weibo.setSource(datea.nextElementSibling().html());

            Element content = feed.select("div[node-type=feed_list_content]").get(0);
//                System.out.println(content.html());
            Elements unfold = content.select("a[action-type=fl_unfold]");
            if (unfold.size() > 0) {
                if (unfold.get(0).text().startsWith("展开全文")) {
                    System.out.print("展开全文：");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Element newContent = getLongText(weibo.getId());
                    if (newContent != null) {
                        content = newContent;
                    }
                }
            }
            weibo.setHtmlContent(content.html());
            String contentText = content.text();
            weibo.setContent(contentText);
            System.out.println("内容：" + contentText);
            if (isforward) {
                JSONObject originWeibo = new JSONObject();
                Element forwardContentElem = feed.select(".WB_feed_expand div[node-type=feed_list_forwardContent]").get(0);
                Elements titleA = forwardContentElem.select(".WB_info a[node-type=feed_list_originNick]");
                String originNick = "";
                if (titleA.size() > 0) {
                    originNick = titleA.get(0).attr("title");
                }
                originWeibo.put("originNick", originNick);
                Element feedReasonElem = forwardContentElem.select("div[node-type=feed_list_reason]").get(0);
                Elements unfoldA = feedReasonElem.select("a[action-type=fl_unfold]");
                if (unfoldA.size() > 0) {
                    if (unfoldA.get(0).text().startsWith("展开全文")) {
                        System.out.print("原微博 展开全文：");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Element newElem = getLongText(weibo.getForwardmid());
                        if (newElem != null) {
                            feedReasonElem = newElem;
                        }
                    }
                }
                String originContent = feedReasonElem.text();
                String originContentHtml = feedReasonElem.html();
                System.out.println("   原内容 " + originContent);
                originWeibo.put("originContent", originContent);
                originWeibo.put("originContentHtml", originContentHtml);
                Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                String originPicIds = "";
                if (pics != null) {
                    if (!(pics.attr("node-type").isEmpty())) {
                        originPicIds = getValue(pics.attr("action-data"), "pic_ids");
                        System.out.println("原来微博的图片：" + originPicIds);
                    } else {
                        pics = pics.select(".WB_pic").first();
                        if (pics != null) {
                            originPicIds = getValue(pics.attr("action-data"), "pic_ids");
                            System.out.println("原来微博的图片：" + originPicIds);
                        }
                    }
                }
                originWeibo.put("picids", originPicIds);

                Element WB_func = forwardContentElem.select(".WB_func").first();
                Elements lis = WB_func.select("ul a");
                if (lis.size() == 3) {
                    Element forwardA = lis.get(0);
                    String forwardCommandLink = forwardA.attr("href");
                    forwardCommandLink = forwardCommandLink.substring(0, forwardCommandLink.indexOf("?"));
                    String forwardCount = forwardA.select("em").get(1).html();
                    if (!StringUtils.isNumeric(forwardCount)) {
                        forwardCount = "0";
                    }
                    originWeibo.put("forwardNumber", (Long.parseLong(forwardCount)));
                    originWeibo.put("forwardUrl", (forwardCommandLink + "?type=repost"));
                    System.out.println("原转发：" + forwardCount + "  链接：" + forwardCommandLink+"?type=repost");

                    Element commentA = lis.get(1);
                    String commentCount = commentA.select("em").get(1).html();
                    if (!StringUtils.isNumeric(commentCount)) {
                        commentCount = "0";
                    }
                    originWeibo.put("commentNumber", Long.parseLong(commentCount));
                    originWeibo.put("forwardUrl", forwardCommandLink + "?type=comment");
                    System.out.println("原评论：" + commentCount + "  链接：" + forwardCommandLink+"?type=comment");

                    Element likeA = lis.get(2);
                    String likeCount = likeA.select("em").get(1).html();
                    if (!StringUtils.isNumeric(likeCount)) {
                        likeCount = "0";
                    }
                    originWeibo.put("likeNumber", Long.parseLong(likeCount));
                    System.out.println("原赞：" + likeCount + "  链接：" + forwardCommandLink+"?type=like");
                }
                weibo.setJSONMeta(originWeibo);
            }
            String topicreg = "#([\\S|\\s]*?)#";
            Pattern pattern = Pattern.compile(topicreg);
            Matcher m = pattern.matcher(contentText);
            String tps = "";
            while (m.find()) {
                tps += "#";
                tps += m.group(1);
            }
            System.out.println("话题：" + tps.replaceFirst("#", ""));
            weibo.setTopics(tps.replaceFirst("#", ""));
            Elements video = feed.select(".WB_detail .WB_video");
            if (video.size() > 0) {
                String actionData = video.attr("action-data");
                String video_src = getValue(actionData, "video_src");
                String url = video_src.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                String urlStr = URLDecoder.decode(url, "UTF-8");
                System.out.println("视频地址：" + urlStr);
                weibo.setVideo_src(urlStr);
            }
            Element WB_row_line = feed.select(".WB_row_line").first();
            Element forwardA = WB_row_line.select("li a[action-type=fl_forward]").first();
            String forwardCommandLink = getValue(forwardA.attr("action-data"), "url");
            String forwardCount = forwardA.select("em").get(1).html();
            if (!StringUtils.isNumeric(forwardCount)) {
                forwardCount = "0";
            }
            weibo.setForwardNumber(Long.parseLong(forwardCount));
            weibo.setForwardUrl(forwardCommandLink + "?type=repost");
            System.out.println("转发：" + forwardCount + "  链接：" + forwardCommandLink+"?type=repost");

            Element commentA = WB_row_line.select("li a[action-type=fl_comment]").first();
            String commentCount = commentA.select("em").get(1).html();
            if (!StringUtils.isNumeric(commentCount)) {
                commentCount = "0";
            }
            weibo.setCommentNumber(Long.parseLong(commentCount));
            weibo.setForwardUrl(forwardCommandLink + "?type=comment");
            System.out.println("评论：" + commentCount + "  链接：" + forwardCommandLink+"?type=comment");

            Element likeA = WB_row_line.select("li span[node-type=like_status]").first();
            String likeCount = likeA.select("em").get(1).html();
            if (!StringUtils.isNumeric(likeCount)) {
                likeCount = "0";
            }
            weibo.setLikeNumber(Long.parseLong(likeCount));
            System.out.println("赞：" + likeCount + "  链接：" + forwardCommandLink+"?type=like");

            Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
            if (pics != null) {
                if (!(pics.attr("node-type").isEmpty())) {
                    String pic_ids = getValue(pics.attr("action-data"), "pic_ids");
                    System.out.println("图片：" + pic_ids);
                    weibo.setPicids(pic_ids);
                } else {
                    pics = pics.select(".WB_pic").first();
                    if (pics != null) {
                        String pic_ids = getValue(pics.attr("action-data"), "pic_ids");
                        weibo.setPicids(pic_ids);
                        System.out.println("图片：" + pic_ids);
                    }
                }
            }
            System.out.println("====================");
            list.add(weibo);
        }
        return list;
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
    public static Element getLongText(String mid) throws IOException {
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
        String respStr = getRespStr(client, "http://weibo.com/p/aj/mblog/getlongtext?mid=" + mid);
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

    /**
     * 生成get请求
     * @param url
     * @return
     */
    private HttpGet generateGet(String url) {
        HttpGet get = new HttpGet(url);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Cookie",
                "SUB=_2A250XKz3DeRhGedL41EY8y3Izz2IHXVXK5k_rDV8PUJbmtBeLWLWkW87om_DvkyAa65_MazbbG0jP - sdoA..;" +
                        "SUBP=0033WrSXqPxfM72wWs9jqgMF55529P9D9W5BdzJ9n8xZjH6_MgpLKU.S5JpX5K2hUgL.Fo2f1he4e0eXSh22dJLoIEWGdcvadcvadcvaqg7_TCH8SEHW1C;");
        return get;
    }
    /**
     * http get response --> string
     * @param url
     * @return
     * @throws IOException
     */
    private static String getRespStr(CloseableHttpClient client, String url) throws IOException {
        HttpGet get = new HttpGet(url);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");

        CloseableHttpResponse response = client.execute(get);
        return HttpUtil.getRespString(response);
    }
}
