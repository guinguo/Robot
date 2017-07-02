package test.netease;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @描述:
 * 图片服务器
 * /thumb150/ ：缩略图
 * /orj360/   ：中等大小
 * /mw690/    ：大图
 * @作者: gzguoguinan
 * @日期: 2017-06-27 14:53
 * @版本: v1.0
 */
public class Test2 {
    public static void main(String[] args) {
        try {
            int n = 540;
            HashMap<String, List> wbsmap = new HashMap<>();
            int i = 0;
            for (; i < n / 45 - 1; i++) {
                String prepage = i + "";
                String page = (i + 1) + "";
                System.out.println(prepage + "\t" + page + "\t" + 0);
                wbsmap.put(prepage + page + "0", getWb(prepage, page, 0));
                System.out.println(page + "\t" + page + "\t" + 0);
                wbsmap.put(prepage + page + "0", getWb(page, page, 0));
                System.out.println(page + "\t" + page + "\t" + 1);
                wbsmap.put(prepage + page + "0", getWb(page, page, 1));
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
                    wbsmap.put((i + 1) +""+ (i + 1) + "1", getWb((i + 1) +"", (i + 1) +"", 1));
                    Collections.reverse(last);
                case 2:
                    System.out.println(i + 1 + "\t" + (i + 1) + "\t" + 0);//16-30;array.reverse();
                    wbsmap.put((i + 1) +""+ (i + 1) + "0", getWb((i + 1) +"", (i + 1) +"", 0));
                    Collections.reverse(last);
                case 1:
                    System.out.println(i + "\t" + (i + 1) + "\t" + 0);//1-15;array.reverse();
                    wbsmap.put((i) +""+ (i + 1) + "0", getWb((i) +"", (i + 1) +"", 0));
                    Collections.reverse(last);
            }
            Collections.reverse(last);
            System.out.println("---------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Weibo> getWb(String prepage, String page, int pagebar) throws IOException {
        HttpGet get = new HttpGet("http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&id=1005055652557385&" +
                "pre_page=" + prepage + "&page=" + page + "&pagebar=" + pagebar);
        get.addHeader("Cache-Control","max-age=0");
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate, sdch");
        get.addHeader("X-Requested-With","XMLHttpRequest");
        get.addHeader("DNT","1");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Cookie","YF-Page-G0=abc; SUBP=abc; SUB=abc;");

        CloseableHttpClient client = HttpUtil.httpClient;
        CloseableHttpResponse response = client.execute(get);
        JSONObject json = JSON.parseObject(HttpUtil.getRespString(response));
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
        Connection.Response document = Jsoup.connect(
                "http://weibo.com/p/aj/mblog/getlongtext?mid=" + mid)
                .header("Cache-Control", "max-age=0")
                .header("Accept", "text/html,application/xhtml+xml,application/xmlq=0.9,image/webp,*/*q=0.8")
                .header("Accept-Language", "zh-CN,zhq=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("DNT", "1")
                .header("Connection", "keep-alive")
                .cookie("YF-Page-G0", "abc")
                .cookie("SUBP", "abc")
                .cookie("SUB", "abc")
                .ignoreContentType(true).execute();
        if (!document.body().isEmpty()) {
            JSONObject json = JSON.parseObject(document.body());
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
        }
        return null;
    }
}


