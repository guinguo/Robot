package org.gux.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
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
            /*int n = 538;
            HashMap<String, List> wbsmap = new HashMap<>();
            System.out.println("prepage\tpage\tpagebar");
            int i = 0;
            for (; i < 538 / 45 - 1; i++) {
                String prepage = i + "";
                String page = (i + 1) + "";
                System.out.println(prepage + "\t" + page + "\t" + 0);
                System.out.println(page + "\t" + page + "\t" + 0);
                System.out.println(page + "\t" + page + "\t" + 1);
                System.out.println("---------");
            }
            int mod = n % 45;
            int div = mod / 15;
            if (mod == 0) div = 2;
            div++;
            switch (div) {
                case 3:
                    System.out.println(i + 1 + "\t" + (i + 1) + "\t" + 1);//31-45;array.reverse();
                case 2:
                    System.out.println(i + 1 + "\t" + (i + 1) + "\t" + 0);//16-30;array.reverse();
                case 1:
                    System.out.println(i + "\t" + (i + 1) + "\t" + 0);//1-15;array.reverse();
            }
            System.out.println("---------");*/
            //array.reverse();


            Connection.Response document = Jsoup.connect("http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&id=1005055652557385&pre_page=0&page=1&pagebar=0")
                    .header("Cache-Control", "max-age=0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xmlq=0.9,image/webp,*/*q=0.8")
                    .header("Accept-Language", "zh-CN,zhq=0.8")
                    .header("Accept-Encoding", "gzip, deflate, sdch")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("DNT", "1")
                    .header("Connection", "keep-alive")
                    .cookie("YF-Page-G0", "abc")
                    .cookie("SUBP", "abc")
                    .cookie("SUB", "abc")
                    .ignoreContentType(true).execute();
            JSONObject json = JSON.parseObject(document.body());
            String html = json.getString("data");
            Document wbs = Jsoup.parse(html);
            Elements feedlist = wbs.select("div[action-type=feed_list_item]");
            for (Element feed : feedlist) {
                System.out.println("id："+feed.attr("mid"));
                boolean isforward = "1".equals(feed.attr("isforward"));
                System.out.println("转发：" + isforward);
                if (isforward) {
                    String minfo = feed.attr("minfo");
                    System.out.println("原up主：" + minfo.split("&")[0].split("=")[1]);
                    System.out.println("原id：" + minfo.split("&")[1].split("=")[1]);
                }
                String tbinfo = feed.attr("tbinfo");
                System.out.println("ouid：" + (isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
                String opt = feed.select(".WB_feed_detail .opt").get(0).attr("action-data");
                System.out.println("昵称：" + opt.split("&")[1].split("=")[1]);

                Element datea = feed.select(".WB_detail a[name]").get(0);
                System.out.println("发布时间: "+datea.html()+"  "+datea.attr("date"));
                System.out.println("来源：" + datea.nextElementSibling().html());

                Element content = feed.select("div[node-type=feed_list_content]").get(0);
//                System.out.println(content.html());
                String contentText = content.text();
                System.out.println("内容：" + contentText);
                String topicreg = "#(\\S*)#";
                Pattern pattern = Pattern.compile(topicreg);
                Matcher m = pattern.matcher(contentText);
                System.out.print("话题：");
                while (m.find()) {
                    System.out.print(m.group(1)+"#");
                }
                System.out.println();

                Elements video = feed.select(".WB_detail .WB_video");
                if (video.size() > 0) {
                    String actionData = video.attr("action-data");
                    String video_src = getValue(actionData, "video_src");
                    String url = video_src.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    String urlStr = URLDecoder.decode(url, "UTF-8");
                    System.out.println("视频地址：" + urlStr);
                }
                Element WB_row_line = feed.select(".WB_row_line").first();
                Element forwardA = WB_row_line.select("li a[action-type=fl_forward]").first();
                String forwardCommandLink = getValue(forwardA.attr("action-data"), "url");
                System.out.println("转发：" + forwardA.select("em").get(1).html() + "  链接：" + forwardCommandLink+"?type=repost");

                Element commentA = WB_row_line.select("li a[action-type=fl_comment]").first();
                System.out.println("评论：" + commentA.select("em").get(1).html() + "  链接：" + forwardCommandLink+"?type=comment");

                Element likeA = WB_row_line.select("li span[node-type=like_status]").first();
                System.out.println("赞：" + likeA.select("em").get(1).html() + "  链接：" + forwardCommandLink+"?type=like");

                Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                if (!(pics.attr("node-type").isEmpty())) {
                    String pic_ids = getValue(pics.attr("action-data"), "pic_ids");
                    System.out.println("图片：" + pic_ids);
                } else {
                    pics = pics.select(".WB_pic").first();
                    if (pics != null) {
                        String pic_ids = getValue(pics.attr("action-data"), "pic_ids");
                        System.out.println("图片：" + pic_ids);
                    }
                }
                System.out.println("====================");

            }

//            System.out.println(wbs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String source, String key) {
        String[] kvs = source.split("&");
        for (String s : kvs) {
            if (s.startsWith(key)) {
                return s.substring(key.length() + 1);
            }
        }
        return "";
    }
}


