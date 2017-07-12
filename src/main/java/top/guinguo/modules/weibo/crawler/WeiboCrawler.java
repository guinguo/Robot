package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.WeiboService;
import top.guinguo.modules.weibo.utils.Configurator;
import top.guinguo.modules.weibo.utils.Contants;
import top.guinguo.modules.weibo.utils.CrawleHttpFactory;
import top.guinguo.modules.weibo.utils.CrawleUtils;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * 微博抓取器
 */
public class WeiboCrawler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    public static final String MAIN_URL = "wb.main.url";
    public static final String WB_URL = "wb.url";
    public static final String TO_CRAWL_WB_NUMBER = "to.crawl.wb.number";
    public static final String MIAN_THREAD_NUMBER = "mian.thread.number";
    public static final String MIAN_EACH_SIZE = "mian.each.size";
    public static final String EACH_USER_SLEEP_INTERVAL = "each.user.sleep.interval";
    public static final String MIAN_THREAD_INDEX = "mian.thread.index";
    private String mainUrl;
    private String wbUrl;
    private int toCrawlWbNumber;
    private int mainThreadNumber;
    private int mainEachSize;
    private long eachUserSleepInterval;
    private long mianThreadIndex;


    private CrawleHttpFactory crawleHttpFactory;
    private CrawleUtils crawleUtils;
    private IWeiboService weiboService;

    public WeiboCrawler() {
        crawleHttpFactory = CrawleHttpFactory.getInstance();
        crawleUtils = CrawleUtils.getInstance();
        weiboService = WeiboService.getInstance();
        Configurator configurator = Configurator.getInstance();
        this.mainUrl = configurator.get(MAIN_URL);
        this.wbUrl = configurator.get(WB_URL);
        this.toCrawlWbNumber = configurator.getInt(TO_CRAWL_WB_NUMBER);
        this.mainThreadNumber = configurator.getInt(MIAN_THREAD_NUMBER);
        this.mainEachSize = configurator.getInt(MIAN_EACH_SIZE);
        this.eachUserSleepInterval = configurator.getLong(EACH_USER_SLEEP_INTERVAL);
        this.mianThreadIndex = configurator.getLong(MIAN_THREAD_INDEX);
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler weiboCrawler = new WeiboCrawler();
        long index = weiboCrawler.mianThreadIndex;
        ExecutorService mainThreadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < weiboCrawler.mainThreadNumber; i++) {
            mainThreadPool.submit(weiboCrawler.new MainThread((index + i * 1000), weiboCrawler.mainEachSize, weiboCrawler.eachUserSleepInterval));
        }
        mainThreadPool.shutdown();
    }

    /**
     * 主线程爬取 每个线程爬取n个用户
     */
    class MainThread implements Runnable {
        //起始位置id
        private long index;
        //抓取用户的数量
        private int size;
        //每一个用户暂停多少毫秒
        private long sleepInterval;
        private Random random = new Random();

        public MainThread(long index, int size, long sleepInterval) {
            this.index = index;
            this.size = size;
            this.sleepInterval = sleepInterval;
        }

        @Override
        public void run() {
            try {
                log.info("[主线程]：[" + Thread.currentThread() + "]开始启动，起始id：" + index+ "的用户");
                long start = System.currentTimeMillis();
                this.work(index, size, sleepInterval);
                long end = System.currentTimeMillis();
                log.info("[主线程]：[" + Thread.currentThread() + "]完成，结束id：" + (index + size) + "的用户,耗时：" + (end - start)/1000 + "s");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void work(long start, int size, long sleepInterval) throws Exception {
            CloseableHttpResponse response;
            //爬取一个用户的线程池
            ExecutorService crawlUserPool = Executors.newCachedThreadPool();
            for (long i = start, length = start + size; i < length; i++) {
                CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
                String uid = i + "";
                try {
                    HttpGet get = crawleHttpFactory.generateGet(mainUrl + uid);
                    response = client.execute(get);
                    get.clone();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                if (response.getFirstHeader("Content-Type").getValue().startsWith("application/json;")) {
                    log.warn("404 user: ["+uid+"]not fund");
                    response.close();
                    client.close();
                    System.out.println("==================================================="+sleepInterval/2);
                    Thread.sleep(sleepInterval/4);
                    continue;
                }
                String context0 = HttpUtil.getWeiboMainResp(response);
                User crawledUser = getUserInfo(context0);
                response.close();
                client.close();
                long tmp;
                if (crawledUser.getBlogNumber() != null && crawledUser.getBlogNumber() >= toCrawlWbNumber) {
                    weiboService.addUser(crawledUser);
                    crawlUserPool.submit(new CrawlWbThread(crawledUser.getBlogNumber().intValue(), uid, sleepInterval / 2));
                    tmp = (random.nextInt(2) + 1) * sleepInterval;
                    System.out.println("==================================================="+tmp);
                    Thread.sleep(tmp);
                } else {
                    log.info("[微博数少于" + toCrawlWbNumber + "]" + "[" + uid + "]" + "[blog][" + crawledUser.getBlogNumber() + "]" + "[focus][" + crawledUser.getFocus() + "]" + "[fans][" + crawledUser.getFans() + "]");
                    tmp = (random.nextInt(4) + 1) * sleepInterval;
                    System.out.println("==================================================="+tmp);
                    Thread.sleep(tmp);
                }
            }
            crawlUserPool.shutdown();
        }

        public User getUserInfo(String html) {
            //0. 粉丝数等
            String regex = Contants.REGEX_FANS;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html.toString());
            String result = "";
            while (matcher.find()) {
                result= (matcher.group(1));
            }
            User user = new User();
            JSONObject json = JSON.parseObject("{"+result+"}");
            if (result.isEmpty()) {
                return user;
            }
            String htmldata = json.getString("html");
            if (htmldata == null || htmldata.isEmpty()) {
                return user;
            }
            Document document = Jsoup.parse(htmldata);
            Elements as = document.select(".t_link");
            int wbcount = 0;
            for (Element a : as) {
                Element cnd = a.select(".S_txt2").get(0);
                String cn = cnd.html();
                int number = Integer.parseInt(cnd.previousElementSibling().html());
                log.info("weibo_info:"+cn+": "+number);
                if (cn.equals("关注")) {
                    user.setFocus(number);
                }
                if (cn.equals("粉丝")) {
                    user.setFans(Long.valueOf(wbcount));
                }
                if (cn.equals("微博")) {
                    wbcount = number;
                    user.setBlogNumber(Long.valueOf(wbcount));
                    if (wbcount < 15) {
                        // pass over it
                        return user;
                    }
                }
            }
            //1. id昵称等
            regex = Contants.REGEX_NICK;
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(html.toString());
            result = "";
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
                        log.info("weibo_info:"+"oid: " + id);
                        user.setId(id);
                    } else if(s.startsWith("$CONFIG['page_id']")) {
                        log.info("weibo_info:"+"page_id: "+s.split("=")[1]);
                    } else if(s.startsWith("$CONFIG['onick']")) {
                        String nickname = s.split("=")[1];
                        log.info("weibo_info:"+"昵称: " + nickname);
                        user.setNickname(nickname);
                    }
                }
            }
            //2 性别是否会员
            regex = Contants.REGEX_MEMBER;
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(html.toString());
            result = "";
            while (matcher.find()) {
                result= (matcher.group(1));
            }
            json = JSON.parseObject("{"+result+"}");
            htmldata = json.getString("html");
            document = Jsoup.parse(htmldata);
            String photo = document.select("img[class=photo]").get(0).attr("src");
            user.setAvatar(photo);
            log.info("weibo_info:"+"头像：" + photo);
            String intro = document.select(".pf_intro").first().html();
            user.setIntro(intro);
            log.info("weibo_info:"+"简介：" + intro);
            Element div = document.select(".pf_username").get(0);
            String username = div.select(".username").get(0).html();
            log.info("weibo_info:"+"用户名：" + username);
            user.setUsername(username);
            String sex = (div.select("a").get(0).select("i").get(0).hasClass("icon_pf_male") ? "男" : "女");
            log.info("weibo_info:"+"性别： " + sex);
            user.setSex(sex);
            Elements wbmenber = div.select("em[class^=W_icon icon_member]");
            if (wbmenber.size() > 0) {
                Set<String> clzz = wbmenber.first().classNames();
                int menlevel = 0;
                for (String c : clzz) {
                    if (c.startsWith("icon_member")) {
                        if (c.endsWith("_dis")) {
                            menlevel = -1;
                        } else {
                            try {
                                menlevel = Integer.parseInt(c.substring(11));
                            } catch (NumberFormatException e) {
                                menlevel = -2;
                            }
                        }
                    }
                }
                log.info("weibo_info:"+"微博会员：" + menlevel);
                user.setMember(menlevel);
            }
            //3个人信息等
            regex = Contants.REGEX_INFO;
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
            log.info("weibo_info:"+"level: " + level);
            user.setLevel(Integer.parseInt(level));
            Elements addrElem = document.select(".ficon_cd_place");
            if (addrElem.size() > 0) {
                String addr = addrElem.get(0).parent().nextElementSibling().html();
                log.info("weibo_info:"+"place: "+addr);
                user.setAddress(addr);
            }
            Elements birthDayEles = document.select(".ficon_constellation");
            if (birthDayEles.size() > 0) {
                String birthDay = birthDayEles.get(0).parent().nextElementSibling().html();
                log.info("weibo_info:"+"birthday: "+birthDay);
                try {
                    user.setBirthDate(sdf.parse(birthDay));
                } catch (ParseException e) {
                }
            }
            as = document.select(".ficon_link");
            if (as.size() > 0) {
                String link = as.get(0).parent().nextElementSibling().html();
                meta.put("link", link);
                user.setJSONMeta(meta);
                log.info("weibo_info:"+"link: " + link);
            }
            as = document.select(".ficon_cd_coupon");
            if (as.size() > 0) {
                String tags = as.get(0).parent().nextElementSibling().select("a").get(0).html();
                log.info("weibo_info:"+"tags: " + tags);
                user.setTags(tags);
            }
            return user;
        }
    }

    /**
     * 爬取微博的线程
     * 每个用户一个线程在爬取
     */
    class CrawlWbThread implements Runnable {
        //用户微博数
        private int size;
        //用户id
        private String uid;
        //每一个用户暂停多少毫秒
        private long stopInterval;

        public CrawlWbThread(int size, String uid, long stopInterval) {
            this.size = size;
            this.uid = uid;
            this.stopInterval = stopInterval;
        }
        @Override
        public void run() {
            log.info("[抓取线程]：[" + Thread.currentThread() + "]开始爬取用户：" + uid + "的" + size + "条微博");
            long start = System.currentTimeMillis();
            crawlerWeibo(size, uid);
            long end = System.currentTimeMillis();
            log.info("[抓取线程]：[" + Thread.currentThread() + "]完成爬取用户：" + uid + "的" + size + "条微博,耗时：" + (end - start) + "ms");
        }

        public void crawlerWeibo(int n, String uid) {
            try {
                HashMap<String, List> wbsmap = new HashMap<>();
                List<Weibo> eachLoop;
                //入库线程池
                ExecutorService insert2DbPool = Executors.newCachedThreadPool();
                int i = 0;
                for (; i < n / 45 - 1; i++) {
                    eachLoop = new ArrayList<>(45);
                    int prepage = i;
                    int page = (i + 1);
                    log.info("weibo_info:"+prepage + "\t" + page + "\t" + 0);
                    eachLoop.addAll(getWb(uid, prepage, page, 0));

                    log.info("weibo_info:"+page + "\t" + page + "\t" + 0);
                    eachLoop.addAll(getWb(uid, page, page, 0));

                    log.info("weibo_info:"+page + "\t" + page + "\t" + 1);
                    eachLoop.addAll(getWb(uid, page, page, 1));

                    log.info("---------");
                    insert2DbPool.submit(new WriteDbThread(eachLoop));
                }
                int mod = n % 45;
                int div = mod / 15;
                if (mod == 0) div = 2;
                div++;
                List<Weibo> last = new ArrayList<>(45);
                switch (div) {
                    case 3:
                        log.info("weibo_info:"+i + 1 + "\t" + (i + 1) + "\t" + 1);//31-45;array.reverse();
                        last.addAll(getWb(uid, (i + 1), (i + 1), 1));
                        Collections.reverse(last);
                    case 2:
                        log.info("weibo_info:"+i + 1 + "\t" + (i + 1) + "\t" + 0);//16-30;array.reverse();
                        last.addAll(getWb(uid, (i + 1), (i + 1), 0));
                        Collections.reverse(last);
                    case 1:
                        log.info("weibo_info:"+i + "\t" + (i + 1) + "\t" + 0);//1-15;array.reverse();
                        last.addAll(getWb(uid, (i), (i + 1), 0));
                        Collections.reverse(last);
                }
                log.info("---------");
                Collections.reverse(last);
                insert2DbPool.submit(new WriteDbThread(last));
                insert2DbPool.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public List<Weibo> getWb(String uid, int prepage, int page, int pagebar) throws IOException {
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            String respStr = crawleHttpFactory.getRespStr(client, String.format(wbUrl,uid,prepage,page,pagebar));
            client.close();
            try {
                Thread.sleep((long) (stopInterval * Contants.intervalRadio));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject json = JSON.parseObject(respStr);
            String html = json.getString("data");
            Document wbs = Jsoup.parse(html);
            Elements feedlist = wbs.select("div[action-type=feed_list_item]");
            List<Weibo> list = new ArrayList<>(15);
            for (Element feed : feedlist) {
                Weibo weibo = new Weibo();
                String id = feed.attr("mid");
                log.info("weibo_info:"+"id："+id);
                weibo.setId(id);
                boolean isforward = "1".equals(feed.attr("isforward"));
                weibo.setForward(isforward);
                log.info("weibo_info:"+"转发：" + isforward);
                if (isforward) {
                    String minfo = feed.attr("minfo");
                    String ru = crawleUtils.getValue(minfo, "ru");
                    String rm = crawleUtils.getValue(minfo, "rm");
                    log.info("weibo_info:"+"原up主：" + ru);
                    log.info("weibo_info:"+"原id：" + rm);
                    weibo.setForwarduid(ru);
                    weibo.setForwardmid(rm);
                }
                String tbinfo = feed.attr("tbinfo");
                log.info("weibo_info:"+"ouid：" + (isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
                weibo.setUid((isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
                String opt = feed.select(".WB_feed_detail .opt").get(0).attr("action-data");
                log.info("weibo_info:"+"昵称：" + opt.split("&")[1].split("=")[1]);
                weibo.setNickname(opt.split("&")[1].split("=")[1]);

                Element datea = feed.select(".WB_detail a[name]").get(0);
                log.info("weibo_info:"+"发布时间: "+datea.html()+"  "+datea.attr("date"));
                weibo.setCreateDate(new Date(Long.parseLong(datea.attr("date"))));
                if (datea.nextElementSibling() != null) {
                    log.info("weibo_info:"+"来源：" + datea.nextElementSibling().html());
                    weibo.setSource(datea.nextElementSibling().html());
                }

                Element content = feed.select("div[node-type=feed_list_content]").get(0);
//                log.info("weibo_info:"+content.html());
                Elements unfold = content.select("a[action-type=fl_unfold]");
                if (unfold.size() > 0) {
                    if (unfold.get(0).text().startsWith("展开全文")) {
                        System.out.print("展开全文：");
                        try {
                            Thread.sleep(1100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Element newContent = crawleUtils.getLongText(weibo.getId());
                        if (newContent != null) {
                            content = newContent;
                        }
                    }
                }
                weibo.setHtmlContent(content.html());
                String contentText = content.text();
                weibo.setContent(contentText);
                log.info("weibo_info:"+"内容：" + contentText);
                if (isforward) {
                    JSONObject originWeibo = new JSONObject();
                    Element forwardContentElem = feed.select(".WB_feed_expand div[node-type=feed_list_forwardContent]").get(0);
                    Elements titleA = forwardContentElem.select(".WB_info a[node-type=feed_list_originNick]");
                    String originNick = "";
                    if (titleA.size() > 0) {
                        originNick = titleA.get(0).attr("title");
                    }
                    originWeibo.put("originNick", originNick);
                    Elements feedReasonElems = forwardContentElem.select("div[node-type=feed_list_reason]");
                    if (feedReasonElems.size() > 0) {
                        Element feedReasonElem = feedReasonElems.first();
                        Elements unfoldA = feedReasonElem.select("a[action-type=fl_unfold]");
                        if (unfoldA.size() > 0) {
                            if (unfoldA.get(0).text().startsWith("展开全文")) {
                                System.out.print("原微博 展开全文：");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Element newElem = crawleUtils.getLongText(weibo.getForwardmid());
                                if (newElem != null) {
                                    feedReasonElem = newElem;
                                }
                            }
                        }
                        String originContent = feedReasonElem.text();
                        String originContentHtml = feedReasonElem.html();
                        log.info("weibo_info:"+"   原内容 " + originContent);
                        originWeibo.put("originContent", originContent);
                        originWeibo.put("originContentHtml", originContentHtml);
                    }
                    Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                    String originPicIds = "";
                    if (pics != null) {
                        if (!(pics.attr("node-type").isEmpty())) {
                            originPicIds = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                            log.info("weibo_info:"+"原来微博的图片：" + originPicIds);
                        } else {
                            pics = pics.select(".WB_pic").first();
                            if (pics != null) {
                                originPicIds = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                                log.info("weibo_info:"+"原来微博的图片：" + originPicIds);
                            }
                        }
                    }
                    originWeibo.put("picids", originPicIds);

                    Element WB_func = forwardContentElem.select(".WB_func").first();
                    if (WB_func != null) {
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
                            log.info("weibo_info:"+"原转发：" + forwardCount + "  链接：" + forwardCommandLink+"?type=repost");

                            Element commentA = lis.get(1);
                            String commentCount = commentA.select("em").get(1).html();
                            if (!StringUtils.isNumeric(commentCount)) {
                                commentCount = "0";
                            }
                            originWeibo.put("commentNumber", Long.parseLong(commentCount));
                            originWeibo.put("forwardUrl", forwardCommandLink + "?type=comment");
                            log.info("weibo_info:"+"原评论：" + commentCount + "  链接：" + forwardCommandLink+"?type=comment");

                            Element likeA = lis.get(2);
                            String likeCount = likeA.select("em").get(1).html();
                            if (!StringUtils.isNumeric(likeCount)) {
                                likeCount = "0";
                            }
                            originWeibo.put("likeNumber", Long.parseLong(likeCount));
                            log.info("weibo_info:"+"原赞：" + likeCount + "  链接：" + forwardCommandLink+"?type=like");
                        }
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
                log.info("weibo_info:"+"话题：" + tps.replaceFirst("#", ""));
                weibo.setTopics(tps.replaceFirst("#", ""));
                Elements video = feed.select(".WB_detail .WB_video");
                if (video.size() > 0) {
                    String actionData = video.attr("action-data");
                    String video_src = crawleUtils.getValue(actionData, "video_src");
                    String url = video_src.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    String urlStr = URLDecoder.decode(url, "UTF-8");
                    log.info("weibo_info:"+"视频地址：" + urlStr);
                    weibo.setVideo_src(urlStr);
                }
                Element WB_row_line = feed.select(".WB_row_line").first();
                Element forwardA = WB_row_line.select("li a[action-type=fl_forward]").first();
                String forwardCommandLink = crawleUtils.getValue(forwardA.attr("action-data"), "url");
                String forwardCount = forwardA.select("em").get(1).html();
                if (!StringUtils.isNumeric(forwardCount)) {
                    forwardCount = "0";
                }
                weibo.setForwardNumber(Long.parseLong(forwardCount));
                weibo.setForwardUrl(forwardCommandLink + "?type=repost");
                log.info("weibo_info:"+"转发：" + forwardCount + "  链接：" + forwardCommandLink+"?type=repost");

                Element commentA = WB_row_line.select("li a[action-type=fl_comment]").first();
                String commentCount = commentA.select("em").get(1).html();
                if (!StringUtils.isNumeric(commentCount)) {
                    commentCount = "0";
                }
                weibo.setCommentNumber(Long.parseLong(commentCount));
                weibo.setForwardUrl(forwardCommandLink + "?type=comment");
                log.info("weibo_info:"+"评论：" + commentCount + "  链接：" + forwardCommandLink+"?type=comment");

                Element likeA = WB_row_line.select("li span[node-type=like_status]").first();
                String likeCount = likeA.select("em").get(1).html();
                if (!StringUtils.isNumeric(likeCount)) {
                    likeCount = "0";
                }
                weibo.setLikeNumber(Long.parseLong(likeCount));
                log.info("weibo_info:"+"赞：" + likeCount + "  链接：" + forwardCommandLink+"?type=like");

                Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                if (pics != null) {
                    if (!(pics.attr("node-type").isEmpty())) {
                        String pic_ids = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                        log.info("weibo_info:"+"图片：" + pic_ids);
                        weibo.setPicids(pic_ids);
                    } else {
                        pics = pics.select(".WB_pic").first();
                        if (pics != null) {
                            String pic_ids = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                            weibo.setPicids(pic_ids);
                            log.info("weibo_info:"+"图片：" + pic_ids);
                        }
                    }
                }
                log.info("====================");
                list.add(weibo);
            }
            return list;
        }
    }

    /**
     * 写hbase线程
     */
    class WriteDbThread implements Runnable {
        private List<Weibo> weiboList;

        public WriteDbThread(List<Weibo> weiboList) {
            this.weiboList = weiboList;
        }

        @Override
        public void run() {
            if (weiboList.size() > 0) {
                try {
                    log.info("[入库线程:" + Thread.currentThread() + "]完成批量导入[user:"+weiboList.get(0).getUid()+"]的微博数据：[size:" + weiboList.size() + "]条微博");
                    long start = System.currentTimeMillis();
                    weiboService.batchAddWeibo(weiboList);
                    long end = System.currentTimeMillis();
                    log.info("[入库线程:" + Thread.currentThread() + "]完成批量导入[user:"+weiboList.get(0).getUid()+"]的微博数据：[size:" + weiboList.size() + "]条微博,[耗时：" + (end - start) + "]ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
