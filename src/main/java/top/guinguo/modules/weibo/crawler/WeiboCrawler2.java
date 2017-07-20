package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.scripts.JO;
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
import top.guinguo.modules.weibo.utils.*;
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
public class WeiboCrawler2 {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static final String MAIN_URL = "wb.main.url";
    public static final String MAIN_INFO = "wb.main.info";
    public static final String WB_URL = "wb.url";
    public static final String TO_CRAWL_WB_NUMBER = "to.crawl.wb.number";
    public static final String MIAN_THREAD_NUMBER = "mian.thread.number";
    public static final String MIAN_EACH_SIZE = "mian.each.size";
    public static final String EACH_USER_SLEEP_INTERVAL = "each.user.sleep.interval";
    public static final String MIAN_THREAD_INDEX = "mian.thread.index";
    public static final String LOAD_TO_DB = "load.to.db";
    public static final String PAGE_SIZE = "page.size";
    public static final String TO_CRAWL_PAGE_STEP1 = "to.crawl.page.step1";
    public static final String TO_CRAWL_PAGE_STEP2 = "to.crawl.page.step2";
    public static final String TO_CRAWL_WB_RATIO = "to.crawl.wb.ratio";

    private String mainUrl;
    private String mainInfo ;
    private String wbUrl;
    private int toCrawlWbNumber;
    private int mainThreadNumber;
    private int mainEachSize;
    private long eachUserSleepInterval;
    private long mianThreadIndex;
    private boolean load2Db;
    private int pageSize;
    private int toCrawlPageStep1;
    private int toCrawlPageStep2;
    private double toCrawlWbRatio;


    private CrawleHttpFactory crawleHttpFactory;
    private CrawleUtils crawleUtils;
    private IWeiboService weiboService;

    public WeiboCrawler2() {
        crawleHttpFactory = CrawleHttpFactory.getInstance();
        crawleUtils = CrawleUtils.getInstance();
        weiboService = WeiboService.getInstance();
        Configurator configurator = Configurator.getInstance();
        this.mainUrl = configurator.get(MAIN_URL);
        this.mainInfo = configurator.get(MAIN_INFO);
        this.wbUrl = configurator.get(WB_URL);
        this.toCrawlWbNumber = configurator.getInt(TO_CRAWL_WB_NUMBER);
        this.mainThreadNumber = configurator.getInt(MIAN_THREAD_NUMBER);
        this.mainEachSize = configurator.getInt(MIAN_EACH_SIZE);
        this.eachUserSleepInterval = configurator.getLong(EACH_USER_SLEEP_INTERVAL);
        this.mianThreadIndex = configurator.getLong(MIAN_THREAD_INDEX);
        this.load2Db = configurator.getBoolean(LOAD_TO_DB, false);
        this.pageSize = configurator.getInt(PAGE_SIZE, 10);
        this.toCrawlPageStep1 = configurator.getInt(TO_CRAWL_PAGE_STEP1, 5);
        this.toCrawlPageStep2 = configurator.getInt(TO_CRAWL_PAGE_STEP2, 1);
        this.toCrawlWbRatio = configurator.getDouble(TO_CRAWL_WB_RATIO, 0.618);
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler2 weiboCrawler = new WeiboCrawler2();
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
            //入库线程池
            ExecutorService insert2DbPool = Executors.newCachedThreadPool();
            for (long i = start, length = start + size; i < length; i++) {
                CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
                String uid = i + "";
                try {
                    HttpGet get = crawleHttpFactory.generateGet(String.format(mainUrl, uid, uid));
                    response = client.execute(get);
                    get.clone();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                String responRs = HttpUtil.getRespString(response);
                response.close();
                if (responRs.length() < 150 && responRs.contains("errmsg")) {
                    log.warn("404 user: ["+uid+"]not fund");
                    response.close();
                    client.close();
                    System.out.println("==================================================="+(random.nextInt(3) + 1) * sleepInterval);
                    Thread.sleep((random.nextInt(3) + 1) * sleepInterval);
                    continue;
                }
                JSONObject result = null;
                try {
                    result = JSONObject.parseObject(responRs);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(uid);
                }
                long tmp;
                if (result.getJSONObject("userInfo") != null && result.getJSONObject("userInfo").getLong("statuses_count") < toCrawlWbNumber) {
                    log.info("[微博数少于" + toCrawlWbNumber + "]" + "[" + uid + "]");
                    tmp = (random.nextInt(4)) * sleepInterval;
                    System.out.println("404:==================================================="+tmp);
                    Thread.sleep(tmp);
                }

                User crawledUser = getUserBasic(result);
                tmp = (random.nextInt(2) + 1) * sleepInterval;
                System.out.println("getUserInfo:==================================================="+tmp);
                Thread.sleep(tmp);
                String resultStr = crawleHttpFactory.getRespStr(client, String.format(mainInfo, uid));
                result = JSONObject.parseObject(resultStr);
                crawledUser = getUserInfo(crawledUser, result);

                tmp = (random.nextInt(2) + 1) * (sleepInterval / 2);
                System.out.println("getOriginRatio:==================================================="+tmp);
                Thread.sleep(tmp);
                String respStr = crawleHttpFactory.getRespStr(client, String.format(wbUrl, uid, uid + "_-_WEIBO_SECOND_PROFILE_WEIBO_ORI", 1));
                JSONObject cardlistInfo = JSON.parseObject(respStr).getJSONObject("cardlistInfo");
                if (cardlistInfo == null) {
                    response.close();
                    client.close();
                    continue;
                }
                Long oriNumber = cardlistInfo.getLong("total");
                if (oriNumber == null) {
                    log.error("[" + uid + "]NullPointerException");
                    log.info("[too busy]===========================[sleep][" + (1000 * 60 * 20) + "]");
                    response.close();
                    client.close();
                    Thread.sleep(1000 * 60 * 30);//太频繁，歇30分钟
                    continue;
                }
                Double ratio = (oriNumber * 1.0 / crawledUser.getBlogNumber());
                client.close();

                if (crawledUser.getBlogNumber() != null && crawledUser.getBlogNumber() < toCrawlWbNumber) {
                    log.info("[微博数少于" + toCrawlWbNumber + "]" + "[" + uid + "]" + "[blog][" + crawledUser.getBlogNumber() + "]" + "[focus][" + crawledUser.getFocus() + "]" + "[fans][" + crawledUser.getFans() + "]");
                    tmp = (random.nextInt(4) + 1) * sleepInterval;
                    System.out.println("==================================================="+tmp);
                    Thread.sleep(tmp);
                } else if (oriNumber != null && ratio < toCrawlWbRatio) {
                    log.info("[原创率][" + String.format("%.2f", ratio * 100) + "%]小于" + (toCrawlWbRatio * 100) + "%]" + "[" + uid + "]" + "[blog][" + crawledUser.getBlogNumber() + "]" + "[originBlog][" + oriNumber + "]" + "[focus][" + crawledUser.getFocus() + "]" + "[fans][" + crawledUser.getFans() + "]");
                    tmp = (random.nextInt(4) + 1) * sleepInterval;
                    System.out.println("==================================================="+tmp);
                    Thread.sleep(tmp);
                    continue;
                } else {
                    if (load2Db) {
                        weiboService.addUser(crawledUser);
                    }
                    crawlUserPool.submit(new CrawlWbThread(crawledUser.getBlogNumber().intValue(), uid, sleepInterval / 2, insert2DbPool));
                    tmp = (random.nextInt(2) + 1) * sleepInterval;
                    System.out.println("===================================================" + tmp);
                    Thread.sleep(tmp);
                }
            }
            insert2DbPool.shutdown();
            crawlUserPool.shutdown();
        }

        /**
         * 用户主页
         * @param result
         * @return
         */
        public User getUserBasic(JSONObject result) {
            User user = new User();
            JSONObject userInfo = result.getJSONObject("userInfo");
            //0. id昵称等
            if (userInfo == null) {
                user.setBlogNumber(0L);
                return user;
            }
            user.setId(userInfo.getString("id"));
            user.setNickname(userInfo.getString("screen_name"));
            log.info("weibo_info:"+"id: " + userInfo.getString("id"));
            log.info("weibo_info:"+"昵称: " + userInfo.getString("screen_name"));
            //1. 粉丝数等
            user.setFocus(userInfo.getInteger("follow_count"));
            user.setFans(userInfo.getLong("followers_count"));
            user.setBlogNumber(userInfo.getLong("statuses_count"));
            log.info("weibo_info:"+"微博: " + userInfo.getLong("statuses_count"));
            log.info("weibo_info:"+"粉丝: " + userInfo.getLong("followers_count"));
            log.info("weibo_info:"+"关注: " + userInfo.getInteger("follow_count"));
            //2 性别是否会员
            user.setAvatar(userInfo.getString("profile_image_url"));
            String intro = userInfo.getString("verified_reason");
            if (StringUtils.isEmpty(intro)) {intro = "暂无简介";}
            user.setIntro(intro);
            user.setUsername(userInfo.getString("screen_name"));
            String sex = ("m".equals(userInfo.getString("gender")) ? "男" : "女");
            user.setSex(sex);
            user.setMember(userInfo.getInteger("mbrank"));
            log.info("weibo_info:"+"头像： " + userInfo.getString("profile_image_url"));
            log.info("weibo_info:"+"简介： " + intro);
            log.info("weibo_info:"+"性别： " + sex);
            log.info("weibo_info:"+"会员： " + userInfo.getInteger("mbrank"));
            //3个人信息等
            user.setLevel(userInfo.getInteger("urank"));
            log.info("weibo_info:"+"等级： " + userInfo.getInteger("urank"));
            return user;
        }

        /**
         * 用户信息页
         * @param result
         * @return
         */
        public User getUserInfo(User user, JSONObject result) {
            JSONArray cards = result.getJSONArray("cards");
            if (cards != null) {
                for (int i = 0; i < cards.size(); i++) {
                    JSONObject card = cards.getJSONObject(i);
                    JSONArray cardGroup = card.getJSONArray("card_group");
                    if (cardGroup != null && cardGroup.size() > 0) {
                        for (int j = 0; j < cardGroup.size(); j++) {
                            JSONObject cardOne = cardGroup.getJSONObject(j);
                            String itemName = cardOne.getString("item_name");
                            if (!StringUtils.isEmpty(itemName)) {
                                String itemValue = cardOne.getString("item_content");
                                if ("昵称".equals(itemName)) {
                                    if (user.getNickname() == null) {
                                        user.setNickname(itemValue);
                                    }
                                } else if ("性别".equals(itemName)) {
                                    if (user.getSex() == null) {
                                        user.setSex(itemValue);
                                    }
                                } else if ("所在地".equals(itemName)) {
                                    if (user.getAddress() == null) {
                                        user.setAddress(itemValue);
                                        log.info("weibo_info:" + "所在地: " + itemValue);
                                    }
                                } else if ("简介".equals(itemName)) {
                                    if (user.getIntro() == null) {
                                        user.setIntro(itemValue);
                                    }
                                } else if ("注册时间".equals(itemName)) {
                                    if (user.getRegistedDate() == null) {
                                        try {
                                            user.setRegistedDate(sdf.parse(itemValue));
                                            log.info("weibo_info:" + "注册时间: " + itemValue);
                                        } catch (ParseException e) {
                                        }
                                    }
                                } else if ("公司".equals(itemName)) {
                                    if (user.getCompany() == null) {
                                        user.setCompany(itemValue);
                                        log.info("weibo_info:" + "公司: " + itemValue);
                                    }
                                } else if ("学校".equals(itemName)) {
                                    if (user.getSchool() == null) {
                                        user.setSchool(itemValue);
                                        log.info("weibo_info:" + "学校: " + itemValue);
                                    }
                                } else if ("标签".equals(itemName)) {
                                    if (user.getTags() == null) {
                                        user.setTags(itemValue);
                                        log.info("weibo_info:" + "标签: " + itemValue);
                                    }
                                } else if ("阳光信用".equals(itemName)) {
                                    if (user.getMeta() == null) {
                                        JSONObject meta = new JSONObject();
                                        meta.put("credit", itemValue);
                                        user.setJSONMeta(meta);
                                    } else {
                                        user.getJSONMeta().put("credit", itemValue);
                                    }
                                    log.info("weibo_info:" + "阳光信用: " + itemValue);
                                }
                            }
                        }
                    }
                }
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
        //入库线程池
        private ExecutorService dbPool;

        public CrawlWbThread(int size, String uid, long stopInterval, ExecutorService dbPool) {
            this.size = size;
            this.uid = uid;
            this.stopInterval = stopInterval;
            this.dbPool = dbPool;
        }
        @Override
        public void run() {
            log.info("[抓取线程]：[" + Thread.currentThread() + "]开始爬取用户：" + uid + "约" + size + "条微博");
            long start = System.currentTimeMillis();
            crawlerWeibo(uid);
            long end = System.currentTimeMillis();
            log.info("[抓取线程]：[" + Thread.currentThread() + "]完成爬取用户：" + uid + "约" + size + "条微博,耗时：" + (end - start) + "ms");
        }

        public void crawlerWeibo(String uid) {
            try {
                List<Weibo> userWeibos = new ArrayList<>(pageSize * (toCrawlPageStep1 + toCrawlPageStep1));
                int i = 1;
                JSONObject firstPage = getWb(uid, i++);
                List<Weibo> eachPage = getWeibosFromJson(firstPage);
                userWeibos.addAll(eachPage);
                JSONObject cardlistInfo = firstPage.getJSONObject("cardlistInfo");
                if (cardlistInfo == null) {
                    return;
                }
                int n = cardlistInfo.getInteger("total");//554
                int firstTotalPage = n / pageSize; //55
                boolean needTail = false;
                if (firstTotalPage > pageSize * (toCrawlPageStep1)) {//55 > 40(10*4)
                    firstTotalPage = pageSize * (toCrawlPageStep1); //40
                    needTail = true;
                }
                JSONObject page;
                for (; i <= firstTotalPage; i++) {//2 - 40
                    log.info("weibo_info_page:" + "\t" + i);
                    page = getWb(uid, i);
                    if (page != null) {
                        eachPage = getWeibosFromJson(page);
                        userWeibos.addAll(eachPage);
                    }
                    log.info("---------");
                }
                if (needTail) {
                    //>400
                    int lastIndex = n / pageSize - (pageSize * toCrawlPageStep2);
                    if (lastIndex < i) {
                        lastIndex = i;
                    }
                    i = lastIndex;//41 or more
                    for (; i <= n / pageSize; i++) {
                        log.info("weibo_info_page:" + "\t" + i);
                        page = getWb(uid, i);
                        if (page != null) {
                            eachPage = getWeibosFromJson(page);
                            userWeibos.addAll(eachPage);
                        }
                        log.info("---------");
                    }
                }
                dbPool.submit(new WriteDbThread(userWeibos));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public JSONObject getWb(String uid, int page) throws IOException {
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            String respStr = crawleHttpFactory.getRespStr(client, String.format(wbUrl, uid, uid, page));
            client.close();
            try {
                long tmp = (long) (stopInterval * Contants.intervalRadio);
                System.out.println("eachPage:===================================================" + tmp);
                Thread.sleep(tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject json = JSON.parseObject(respStr);
            return json;
        }

        private List<Weibo> getWeibosFromJson(JSONObject firstPage) {
            List<Weibo> list = new ArrayList<>(10);
            JSONArray cards = firstPage.getJSONArray("cards");
            if (cards != null && cards.size() > 0) {
                for (int i = 0; i < cards.size(); i++) {
                    JSONObject card = cards.getJSONObject(i);
                    JSONObject blog = card.getJSONObject("mblog");
                    if (blog != null) {
                        Weibo weibo = new Weibo();
                        String id = blog.getString("id");
                        log.info("weibo_info:" + "id：" + id);
                        weibo.setId(id);
                        boolean isforward = blog.getJSONObject("retweeted_status") != null ? true : false;
                        weibo.setForward(isforward);
                        log.info("weibo_info:"+"转发：" + isforward);
                        JSONObject origin = null;
                        if (isforward) {
                            origin = blog.getJSONObject("retweeted_status");
                            if (origin.getJSONObject("user") == null) {
                                continue;
                            }
                            String originUid = origin.getJSONObject("user").getString("id");
                            String originid = origin.getString("id");
                            log.info("weibo_info:"+"原up主：" + originUid);
                            log.info("weibo_info:"+"原id：" + originid );
                            weibo.setForwarduid(originUid);
                            weibo.setForwardmid(originid );
                        }
                        JSONObject user = blog.getJSONObject("user");
                        log.info("weibo_info:" + "ouid：" + user.getString("id"));
                        weibo.setUid(user.getString("id"));
                        log.info("weibo_info:" + "昵称：" + user.getString("screen_name"));
                        weibo.setNickname(user.getString("screen_name"));

                        String dateStr = blog.getString("created_at");
                        log.info("weibo_info:"+"发布时间: "+dateStr);
                        Date date = null;
                        try {
                            date = DateUtils.parse(dateStr);
                        } catch (Exception e) {
                        }
                        weibo.setCreateDate(date);
                        log.info("weibo_info:"+"来源：" + blog.getString("source"));
                        weibo.setSource(blog.getString("source"));

                        Element content = Jsoup.parse(blog.getString("text")).body();
                        if (blog.getBoolean("isLongText")) {
                            System.out.print("展开全文：");
                            try {
                                Thread.sleep(1100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Element newContent = null;
                            try {
                                newContent = crawleUtils.getLongText(weibo.getId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (newContent != null) {
                                content = newContent;
                            }
                        }
                        weibo.setHtmlContent(content.html());
                        String contentText = content.text();
                        weibo.setContent(contentText);
                        log.info("weibo_info:"+"内容：" + contentText);
                        if (isforward) {
                            JSONObject originWeibo = new JSONObject();
                            String originNick = origin.getJSONObject("user").getString("screen_name");
                            originWeibo.put("originNick", originNick);

                            dateStr = origin.getString("created_at");
                            log.info("原微博:"+"发布时间: "+dateStr);
                            date = null;
                            try {
                                date = DateUtils.parse(dateStr);
                            } catch (Exception e) {
                            }
                            weibo.setCreateDate(date);
                            log.info("原微博:"+"来源：" + origin.getString("source"));
                            weibo.setSource(origin.getString("source"));

                            Element feedReasonElem = Jsoup.parse(origin.getString("text")).body();
                            if (origin.getBoolean("isLongText")) {
                                System.out.print("原微博 展开全文：");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Element newElem = null;
                                try {
                                    newElem = crawleUtils.getLongText(weibo.getForwardmid());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (newElem != null) {
                                    feedReasonElem = newElem;
                                }
                                String originContent = feedReasonElem.text();
                                String originContentHtml = feedReasonElem.html();
                                log.info("weibo_info:"+"   原内容 " + originContent);
                                originWeibo.put("originContent", originContent);
                                originWeibo.put("originContentHtml", originContentHtml);
                            }
                            JSONArray pics = origin.getJSONArray("pics");
                            if (pics != null) {
                                log.info("weibo_info:"+"原来微博的图片：" + pics.size());
                                originWeibo.put("picids", pics.toJSONString());
                            }
                            originWeibo.put("bid", origin.getString("bid"));

                            originWeibo.put("forwardNumber", origin.getLongValue("reposts_count"));
                            log.info("weibo_info:"+"原转发：" + origin.getLongValue("reposts_count"));

                            originWeibo.put("likeNumber", origin.getLongValue("attitudes_count"));
                            log.info("weibo_info:"+"原赞：" + origin.getLongValue("attitudes_count"));

                            originWeibo.put("commentNumber", origin.getLongValue("comments_count"));
                            originWeibo.put("forwardUrl", "https://m.weibo.cn/status/" + origin.getString("bid"));
                            log.info("weibo_info:" + "原评论：" + origin.getLongValue("comments_count") + "  链接：" + "https://m.weibo.cn/status/" + origin.getString("bid"));

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

                        JSONObject media = blog.getJSONObject("page_info");
                        if (media != null) {
                            String type = media.getString("type");
                            if ("video".equals(type)) {
                                String videoUrl = media.getJSONObject("media_info").getString("stream_url");
                                log.info("weibo_info:" + "视频地址：" + videoUrl);
                                weibo.setVideo_src(videoUrl);
                            }
                        }
                        String bid = blog.getString("bid");
                        weibo.setForwardNumber(blog.getLong("reposts_count"));
                        weibo.setForwardUrl("https://m.weibo.cn/status/" + bid);
                        log.info("weibo_info:" + "转发：" + blog.getLong("reposts_count"));

                        weibo.setCommentNumber(blog.getLong("comments_count"));
                        weibo.setForwardUrl("https://m.weibo.cn/status/" + bid);
                        log.info("weibo_info:" + "评论：" + blog.getLong("comments_count") + "  链接：" + "https://m.weibo.cn/status/" + bid);

                        weibo.setLikeNumber(blog.getLong("attitudes_count"));
                        log.info("weibo_info:"+"赞：" + blog.getLong("attitudes_count"));

                        JSONArray pics = blog.getJSONArray("pics");
                        if (pics != null) {
                            log.info("weibo_info:"+"图片：" + pics.size());
                            weibo.setPicids(pics.toJSONString());
                        }
                        log.info("====================");
                        list.add(weibo);
                    }
                }
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
                    if (load2Db) {
                        weiboService.batchAddWeibo(weiboList);
                    }
                    long end = System.currentTimeMillis();
                    log.info("[入库线程:" + Thread.currentThread() + "]完成批量导入[user:"+weiboList.get(0).getUid()+"]的微博数据：[size:" + weiboList.size() + "]条微博,[耗时：" + (end - start) + "]ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
