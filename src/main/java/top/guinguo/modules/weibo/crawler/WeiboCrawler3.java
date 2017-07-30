package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.WeiboService;
import top.guinguo.modules.weibo.utils.*;
import top.guinguo.utils.HttpUtil;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * 微博抓取器
 */
public class WeiboCrawler3 {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static final String MAIN_URL = "wb.main.url";//个人主页
    public static final String MAIN_INFO = "wb.main.info";//个人信息
    public static final String WB_URL = "wb.url";//微博api
    public static final String FANS_URL = "fans.url";//粉丝api
    public static final String TO_CRAWL_WB_NUMBER = "to.crawl.wb.number";//抓取微博最低数
    public static final String TO_CRAWL_FANS_NUMBER = "to.crawl.fans.number";//粉丝最低数
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
    private String fansUrl;
    private int toCrawlWbNumber;
    private int toCrawlFansNumber;
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
    private RedisUtils redisUtils;

    public WeiboCrawler3() {
        crawleHttpFactory = CrawleHttpFactory.getInstance();
        crawleUtils = CrawleUtils.getInstance();
        weiboService = WeiboService.getInstance();
        redisUtils = RedisUtils.getInstance();
        Configurator configurator = Configurator.getInstance();
        this.mainUrl = configurator.get(MAIN_URL);
        this.mainInfo = configurator.get(MAIN_INFO);
        this.wbUrl = configurator.get(WB_URL);
        this.fansUrl = configurator.get(FANS_URL);
        this.toCrawlWbNumber = configurator.getInt(TO_CRAWL_WB_NUMBER);
        this.toCrawlFansNumber = configurator.getInt(TO_CRAWL_FANS_NUMBER);
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
        WeiboCrawler3 weiboCrawler = new WeiboCrawler3();
        weiboCrawler.redisUtils.loadData();
        long index = weiboCrawler.mianThreadIndex;
        ExecutorService mainThreadPool = Executors.newFixedThreadPool(weiboCrawler.mainThreadNumber);
        for (int i = 0; i < weiboCrawler.mainThreadNumber; i++) {
            mainThreadPool.submit(weiboCrawler.new MainThread((index + i * weiboCrawler.mainEachSize), weiboCrawler.mainEachSize, weiboCrawler.eachUserSleepInterval));
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

        public void work(long start, int size, long sleepInterval) {
            //爬取一个用户的线程池
            ExecutorService crawlUserPool = Executors.newCachedThreadPool();
            //入库线程池
            ExecutorService insert2DbPool = Executors.newCachedThreadPool();
            for (long i = start, length = start + size; i < length; i++) {
                try {
                    getOneBlogs(i + "", crawlUserPool, insert2DbPool);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("[getOneBlogs]: " + e.getMessage());
                    continue;
                }
            }
            insert2DbPool.shutdown();
            crawlUserPool.shutdown();
        }

        public void getFansBlogs(User user, ExecutorService crawlUserPool, ExecutorService insert2DbPool) throws Exception {
            List<User> fans = getFans(user);
            for (User fan : fans) {
                getOneBlogs(fan.getId(), crawlUserPool, insert2DbPool);
            }
        }

        /**
         * 获取用户的粉丝
         * @param user
         * @return
         */
        private List<User> getFans(User user) {
            List<User> users = new ArrayList<>(user.getFans().intValue());
            try {
                int i = 1;
                JSONObject firstPage = getFanUser(user.getId(), i++);
                List<User> eachPageUser = getUsersFromJson(firstPage);
                users.addAll(eachPageUser);
                while ("1".equals(firstPage.get("ok"))) {
                    long tmp = (long) (sleepInterval / 2 * Contants.intervalRadio);
                    System.out.println("eachFan:===================================================" + tmp);
                    Thread.sleep(tmp);
                    firstPage = getFanUser(user.getId(), i++);
                    eachPageUser = getUsersFromJson(firstPage);
                    users.addAll(eachPageUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return users;
        }

        /**
         * json 转 user list
         * @param jsonObject
         * @return
         */
        private List<User> getUsersFromJson(JSONObject jsonObject) {
            List<User> users = new ArrayList<>();
            JSONArray cards = jsonObject.getJSONArray("cards");
            if (cards != null && cards.size() > 0) {
                JSONObject card = cards.getJSONObject(0);
                JSONArray cardGroup = card.getJSONArray("card_group");
                if (cardGroup != null) {
                    for (int i = 0; i < cardGroup.size(); i++) {
                        JSONObject ci = cardGroup.getJSONObject(i);
                        if ("10".equals(ci.getString("card_type"))) {
                            JSONObject userJson = ci.getJSONObject("user");
                            if (userJson != null) {
                                User user = new User();
                                user.setId(userJson.getString("id"));
                                user.setFans(userJson.getLong("follow_count"));
                                user.setFocus(userJson.getInteger("followers_count"));
                                user.setBlogNumber(userJson.getLong("statuses_count"));
                                user.setUsername(userJson.getString("screen_name"));
                                user.setNickname(userJson.getString("screen_name"));
                                user.setIntro(userJson.getString("description"));
                                user.setLevel(userJson.getInteger("urank"));
                                user.setMember(userJson.getInteger("mbrank"));
                                log.info("FFFFFans: " + user.getId() + ": [" + user.getBlogNumber() + "-" + user.getFans() + "-" + user.getFocus() + "]");
                                if ((Math.abs(user.getFans() - user.getFocus()) < 800) && user.getBlogNumber() >= toCrawlWbNumber) {
                                    users.add(user);
                                }
                            }
                        }
                    }
                }
            }
            return users;
        }

        /**
         * 回去每个粉丝页面，页面大小不一致。
         * @param uid
         * @param page
         * @return
         * @throws IOException
         */
        private JSONObject getFanUser(String uid, int page) throws IOException {
            log.info("weibo_fans_page:" + "\t" + page);
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            String respStr = crawleHttpFactory.getRespStr(client, String.format(fansUrl, uid, uid, uid, page));
            client.close();
            try {
                long tmp = (long) (sleepInterval / 2 * Contants.intervalRadio);
                System.out.println("eachFans:===================================================" + tmp);
                Thread.sleep(tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject json = JSON.parseObject(respStr);
            log.info("---------");
            return json;
        }

        public User getOneBlogs(String uid, ExecutorService crawlUserPool, ExecutorService insert2DbPool) throws Exception {
            long tmp;
            if (redisUtils.get(uid) != null) {
                log.warn("user:[" + uid + "]had been crawled");
                tmp = (random.nextInt(4)) * sleepInterval;
                System.out.println("==================================================="+tmp);
                Thread.sleep(tmp);
                return null;
            }
            CloseableHttpResponse response = null;
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            try {
                HttpGet get = crawleHttpFactory.generateGet(String.format(mainUrl, uid, uid));
                response = client.execute(get);
                get.clone();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            String responRs = HttpUtil.getRespString(response);
            response.close();
            if (responRs.length() < 150 && responRs.contains("errmsg")) {
                log.warn("404 user: ["+uid+"]not fund");
                response.close();
                client.close();
                System.out.println("==================================================="+(random.nextInt(3) + 1) * sleepInterval);
                Thread.sleep((random.nextInt(3) + 1) * sleepInterval);
                return null;
            }
            JSONObject result = null;
            try {
                result = JSONObject.parseObject(responRs);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(uid);
            }
            if (result.getJSONObject("userInfo") != null && result.getJSONObject("userInfo").getLong("statuses_count") < toCrawlWbNumber) {
                log.info("[微博数少于" + toCrawlWbNumber + "]" + "[" + uid + "]");
                tmp = (random.nextInt(4)) * sleepInterval;
                System.out.println("==================================================="+tmp);
                Thread.sleep(tmp);
            }

            User crawledUser = getUserBasic(result);
            tmp = (random.nextInt(2) + 1) * sleepInterval;
            System.out.println("getUserInfo:==================================================="+tmp);
            Thread.sleep(tmp);
            String resultStr = null;
            try {
                resultStr = crawleHttpFactory.getRespStr(client, String.format(mainInfo, uid));
            } catch (NoRouteToHostException nrte) {
                nrte.printStackTrace();
                log.error("[getOneBlogs Exception]: " + nrte.getMessage());
                response.close();
                client.close();
                Thread.sleep(5 * 60 * 1000);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("[getOneBlogs]: " + e.getMessage());
                response.close();
                client.close();
                return null;
            }
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
                return null;
            }
            Long oriNumber = cardlistInfo.getLong("total");
            if (oriNumber == null) {
                log.error("[" + uid + "]NullPointerException");
                log.info("[too busy]===========================[sleep][" + (1000 * 60 * 20) + "]");
                response.close();
                client.close();
                Thread.sleep(1000 * 60 * 30);//太频繁，歇30分钟
                return null;
            }
            Double ratio = (oriNumber * 1.0 / crawledUser.getBlogNumber());
            client.close();

            if (crawledUser.getBlogNumber() != null && crawledUser.getBlogNumber() < toCrawlWbNumber) {
                log.info("[微博数少于" + toCrawlWbNumber + "]" + "[" + uid + "]" + "[blog][" + crawledUser.getBlogNumber() + "]" + "[focus][" + crawledUser.getFocus() + "]" + "[fans][" + crawledUser.getFans() + "]");
                tmp = (random.nextInt(4) + 1) * sleepInterval;
                System.out.println("==================================================="+tmp);
                Thread.sleep(tmp);
            } else if (oriNumber != null && oriTooLow(crawledUser, ratio, toCrawlWbRatio)) {
                tmp = (random.nextInt(4) + 1) * sleepInterval;
                System.out.println("===================================================" + tmp);
                Thread.sleep(tmp);
                return null;
            } else {
                if (load2Db) {
                    crawledUser.setCrawlDate(new Date());
                    weiboService.addUser(crawledUser);
                }
                crawlUserPool.submit(new CrawlWbThread(crawledUser.getBlogNumber().intValue(), uid, sleepInterval / 2, insert2DbPool));
                tmp = (random.nextInt(2) + 1) * sleepInterval;
                System.out.println("===================================================" + tmp);
                Thread.sleep(tmp);
                redisUtils.set(uid, crawledUser.getBlogNumber() + "-" + crawledUser.getFans() + "-" + crawledUser.getFocus());
                if (crawledUser.getFans() >= toCrawlFansNumber) {
                    getFansBlogs(crawledUser, crawlUserPool, insert2DbPool);
                }
                return crawledUser;
            }
            return null;
        }

        /**
         * 原创率
         * @param user
         * @param userRatio
         * @param toCrawlWbRatio
         * @return
         */
        private boolean oriTooLow(User user, Double userRatio, double toCrawlWbRatio) {
            boolean oriTooLow = false;
            boolean mayZombie = (user.getFocus() - user.getFans()) > 900;//可能僵尸用户
            double localRatio = toCrawlWbRatio;
            if ("女".equals(user.getSex())) {
                double weight = 0.45;
                if (user.getBlogNumber() < 100) {
                    weight = 0.167;
                } else if (user.getBlogNumber() < 300) {
                    weight = 0.262;
                } else if (user.getBlogNumber() < 500) {
                    weight = 0.30;
                } else if (user.getBlogNumber() < 1000) {
                    weight = 0.36;
                }
                localRatio = localRatio * weight;//降低标准
                userRatio *= 1.124;//增加权重
            }
            if (mayZombie) {
                localRatio += 0.1;//僵尸用户 提高要求
            }
            if (userRatio < localRatio) {
                oriTooLow = true;
                log.info("[原创率][" + String.format("%.2f", userRatio * 100) + "%]小于[" + String.format("%.2f", localRatio * 100)  + "%][uid][" + user.getId() + "]" + "[blog][" + user.getBlogNumber() + "]" + "[originBlog][" + (int) (user.getBlogNumber() * userRatio) + "]" + "[focus][" + user.getFocus() + "]" + "[fans][" + user.getFans() + "]" + (mayZombie ? "[僵尸用户]" : ""));
            }
            return oriTooLow;
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
                        /*String date = null;
                        try {
                            date = DateUtils.parseAndFormat(dateStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        weibo.setCreateDate(dateStr);
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
                            /*date = null;
                            try {
                                date = DateUtils.parseAndFormat(dateStr);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                            weibo.setCreateDate(dateStr);
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
                            }
                            String originContent = feedReasonElem.text();
                            String originContentHtml = feedReasonElem.html();
                            log.info("weibo_info:"+"   原内容 " + originContent);
                            originWeibo.put("originContent", originContent);
                            originWeibo.put("originContentHtml", originContentHtml);
                            JSONArray pics = origin.getJSONArray("pics");
                            if (pics != null) {
                                JSONArray array1 = new JSONArray();
                                for (int j = 0; j < pics.size(); j++) {
                                    array1.add(pics.getJSONObject(j).getString("pid"));
                                }
                                log.info("weibo_info:"+"原来微博的图片：" + array1.toJSONString());
                                originWeibo.put("picids", array1.toJSONString());
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
//                                weibo.setVideo_src(videoUrl);
                            }
                        }
                        String bid = blog.getString("bid");
                        weibo.setForwardNumber(blog.getLong("reposts_count"));
//                        weibo.setForwardUrl("https://m.weibo.cn/status/" + bid);
                        log.info("weibo_info:" + "转发：" + blog.getLong("reposts_count"));

                        weibo.setCommentNumber(blog.getLong("comments_count"));
                        weibo.setCommentUrl("https://m.weibo.cn/status/" + bid);
                        log.info("weibo_info:" + "评论：" + blog.getLong("comments_count") + "  链接：" + "https://m.weibo.cn/status/" + bid);

                        weibo.setLikeNumber(blog.getLong("attitudes_count"));
                        log.info("weibo_info:"+"赞：" + blog.getLong("attitudes_count"));

                        JSONArray pics = blog.getJSONArray("pics");
                        if (pics != null) {
                            JSONArray array1 = new JSONArray();
                            for (int j = 0; j < pics.size(); j++) {
                                array1.add(pics.getJSONObject(j).getString("pid"));
                            }
                            log.info("weibo_info:"+"图片：" + array1.toJSONString());
                            weibo.setPicids(array1.toJSONString());
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
