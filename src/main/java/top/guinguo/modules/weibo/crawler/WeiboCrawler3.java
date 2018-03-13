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

import javax.net.ssl.SSLHandshakeException;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
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
public class WeiboCrawler3 {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String MAIN_URL = "wb.main.url";//个人主页
    public static final String MAIN_INFO = "wb.main.info";//个人信息
    public static final String WB_URL = "wb.url";//微博api
    public static final String WB_URL_ORI = "wb.ori.url";//微博api
    public static final String FANS_URL = "fans.url";//粉丝api
    public static final String TO_CRAWL_WB_NUMBER = "to.crawl.wb.number";//抓取微博最低数
    public static final String TO_CRAWL_FANS_NUMBER = "to.crawl.fans.number";//粉丝最低数
    public static final String MIAN_THREAD_NUMBER = "mian.thread.number";
    public static final String MIAN_EACH_SIZE = "mian.each.size";
    public static final String EACH_USER_SLEEP_INTERVAL = "each.user.sleep.interval";
    public static final String MIAN_THREAD_INDEX = "mian.thread.index";
    public static final String LOAD_TO_DB = "load.to.db";
    public static final String NEED_CACHE = "need.cache";
    public static final String PAGE_SIZE = "page.size";
    public static final String TO_CRAWL_PAGE_STEP1 = "to.crawl.page.step1";
    public static final String TO_CRAWL_PAGE_STEP2 = "to.crawl.page.step2";
    public static final String TO_CRAWL_WB_RATIO = "to.crawl.wb.ratio";

    private String mainUrl;
    private String mainInfo ;
    private String wbUrl;
    private String wbUrlOri;
    private String fansUrl;
    private int toCrawlWbNumber;
    private int toCrawlFansNumber;
    private int mainThreadNumber;
    private int mainEachSize;
    private long eachUserSleepInterval;
    private long mianThreadIndex;
    private boolean load2Db;
    private boolean needCache;
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
        this.wbUrlOri = configurator.get(WB_URL_ORI);
        this.fansUrl = configurator.get(FANS_URL);
        this.toCrawlWbNumber = configurator.getInt(TO_CRAWL_WB_NUMBER);
        this.toCrawlFansNumber = configurator.getInt(TO_CRAWL_FANS_NUMBER);
        this.mainThreadNumber = configurator.getInt(MIAN_THREAD_NUMBER);
        this.mainEachSize = configurator.getInt(MIAN_EACH_SIZE);
        this.eachUserSleepInterval = configurator.getLong(EACH_USER_SLEEP_INTERVAL);
        this.mianThreadIndex = configurator.getLong(MIAN_THREAD_INDEX);
        this.load2Db = configurator.getBoolean(LOAD_TO_DB, false);
        this.needCache = configurator.getBoolean(NEED_CACHE, true);
        this.pageSize = configurator.getInt(PAGE_SIZE, 10);
        this.toCrawlPageStep1 = configurator.getInt(TO_CRAWL_PAGE_STEP1, 5);
        this.toCrawlPageStep2 = configurator.getInt(TO_CRAWL_PAGE_STEP2, 1);
        this.toCrawlWbRatio = configurator.getDouble(TO_CRAWL_WB_RATIO, 0.618);
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler3 weiboCrawler = new WeiboCrawler3();
        if (weiboCrawler.needCache) {
            weiboCrawler.redisUtils.loadData();
        }
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
                log.error("[主线程]：[" + Thread.currentThread() + "]开始启动，起始id：" + index+ "的用户");
                long start = System.currentTimeMillis();
                this.work(index, size, sleepInterval);
                long end = System.currentTimeMillis();
                log.error("[主线程]：[" + Thread.currentThread() + "]完成，结束id：" + (index + size) + "的用户,耗时：" + (end - start)/1000 + "s");
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
                while ("1".equals(firstPage.getString("ok"))) {
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
            jsonObject = jsonObject.getJSONObject("data");
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
            } catch (SSLHandshakeException e) {
                e.printStackTrace();
                log.error("SSLHandshakeException:" + e.getMessage());
                client.close();
                Thread.sleep(1000 * 60 * 30);
                return null;
            } catch (ConnectException e) {
                e.printStackTrace();
                log.error("ConnectException:" + e.getMessage());
                client.close();
                Thread.sleep(1000 * 60 * 30);
                return null;
            } catch (EOFException e) {
                e.printStackTrace();
                log.error("EOFException:" + e.getMessage());
                client.close();
                Thread.sleep(1000 * 60 * 30);
                return null;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                log.error("UnknownHostException:" + e.getMessage());
                client.close();
                Thread.sleep(1000 * 60 * 30);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                client.close();
                Thread.sleep(1000 * 60 * 30);
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
            String respStr = null;
            try {
                respStr = crawleHttpFactory.getRespStr(client, String.format(wbUrlOri, uid, uid, 1));
            } catch (SSLHandshakeException e) {
                e.printStackTrace();
                log.error("[getwbUrlOri][SSLHandshakeException]: " + uid + "：" + e.getMessage());
                response.close();
                client.close();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("[getwbUrlOri]: " + uid + "：" + e.getMessage());
                response.close();
                client.close();
                return null;
            }
            JSONObject cardlistInfo = JSON.parseObject(respStr).getJSONObject("data").getJSONObject("cardlistInfo");
            if (cardlistInfo == null) {
                response.close();
                client.close();
                return null;
            }
            Long oriNumber = cardlistInfo.getLong("total");
            if (oriNumber == null) {
                log.error("[" + uid + "]NullPointerException");
                log.info("[too busy]===========================[sleep][" + (1000 * 60 * 30) + "]");
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
                if (load2Db && needCache) {
                    redisUtils.set(uid, crawledUser.getBlogNumber() + "-" + crawledUser.getFans() + "-" + crawledUser.getFocus());
                }
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
                double weight = 0.50;
                if (user.getBlogNumber() < 100) {
                    weight = 0.301;
                } else if (user.getBlogNumber() < 300) {
                    weight = 0.352;
                } else if (user.getBlogNumber() < 500) {
                    weight = 0.403;
                } else if (user.getBlogNumber() < 1000) {
                    weight = 0.461;
                }
//                localRatio = localRatio * weight;//降低标准
                localRatio = weight;//降低标准
//                userRatio *= 1.104;//增加权重
            }
            if (mayZombie) {
                localRatio += 0.25;//僵尸用户 提高要求
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
            result = result.getJSONObject("data");
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
            result = result.getJSONObject("data");
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
                                        user.setRegistedDate(itemValue);
                                        log.info("weibo_info:" + "注册时间: " + itemValue + ",-->" + itemValue);
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
            crawlerWeibo(size, uid);
            long end = System.currentTimeMillis();
            log.info("[抓取线程]：[" + Thread.currentThread() + "]完成爬取用户：" + uid + "约" + size + "条微博,耗时：" + (end - start) + "ms");
        }

        public void crawlerWeibo(int n, String uid) {
            try {
                List<Weibo> userWeibos = new ArrayList<>((toCrawlPageStep1 + toCrawlPageStep2) * pageSize);
                List<Weibo> eachLoop = null;
                //入库线程池
                ExecutorService insert2DbPool = Executors.newCachedThreadPool();
                int i = 0;
                int firstTotalPage = n / pageSize; //55
                boolean needTail = false;
                if (firstTotalPage > toCrawlPageStep1) {//10 > 5
                    firstTotalPage = toCrawlPageStep1; //5
                    needTail = true;
                }
                for (; i < firstTotalPage; i++) {
                    int prepage = i;
                    int page = (i + 1);
                    log.info("weibo_info_page_1:" + prepage + "\t" + page + "\t" + 0);
                    userWeibos.addAll(getWb(uid, prepage, page, 0));

                    log.info("weibo_info_page_1:" + page + "\t" + page + "\t" + 0);
                    userWeibos.addAll(getWb(uid, page, page, 0));

                    log.info("weibo_info_page_1:" + page + "\t" + page + "\t" + 1);
                    userWeibos.addAll(getWb(uid, page, page, 1));

                    log.info("---------");
                }
                if (needTail) {
                    //>225
                    int lastIndex = n / pageSize - (toCrawlPageStep2);
                    if (lastIndex < i) {
                        lastIndex = i;
                    }
                    i = lastIndex;//5 or more
                    for (; i < n / pageSize; i++) {
                        int prepage = i;
                        int page = (i + 1);
                        log.info("weibo_info_page_2:" + prepage + "\t" + page + "\t" + 0);
                        userWeibos.addAll(getWb(uid, prepage, page, 0));

                        log.info("weibo_info_page_2:" + page + "\t" + page + "\t" + 0);
                        userWeibos.addAll(getWb(uid, page, page, 0));

                        log.info("weibo_info_page_2:" + page + "\t" + page + "\t" + 1);
                        userWeibos.addAll(getWb(uid, page, page, 1));
                        log.info("---------");
                    }
                }
                insert2DbPool.submit(new WriteDbThread(userWeibos));
                insert2DbPool.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public List<Weibo> getWb(String uid, int prepage, int page, int pagebar) throws IOException {
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            String respStr = null;
            try {
                respStr = crawleHttpFactory.getRespStr(client, String.format(wbUrl, uid, prepage, page, pagebar));
            } catch (ConnectException e) {
                long tmp = 1000*60*10;
                log.error("[ConnectException][sleep][" + tmp + "]");
                System.err.println("ConnectException:===================================================" + tmp);
                e.printStackTrace();
                try {
                    Thread.sleep(tmp);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
            client.close();
            try {
                long tmp = (long) (stopInterval * Contants.intervalRadio);
                System.out.println("eachPage:===================================================" + tmp);
                Thread.sleep(tmp);
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
                log.info("weibo_info:" + "id：" + id);
                weibo.setId(id);
                boolean isforward = "1".equals(feed.attr("isforward"));
                weibo.setForward(isforward);
                log.info("weibo_info:" + "转发：" + isforward);
                if (isforward) {
                    String minfo = feed.attr("minfo");
                    String ru = crawleUtils.getValue(minfo, "ru");
                    String rm = crawleUtils.getValue(minfo, "rm");
                    log.info("weibo_info:" + "原up主：" + ru);
                    log.info("weibo_info:" + "原id：" + rm);
                    weibo.setForwarduid(ru);
                    weibo.setForwardmid(rm);
                }
                String tbinfo = feed.attr("tbinfo");
                log.info("weibo_info:" + "ouid：" + (isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
                weibo.setUid((isforward ? tbinfo.split("&")[0].split("=")[1] : tbinfo.split("=")[1]));
                String opt = feed.select(".WB_feed_detail .opt").get(0).attr("action-data");
                log.info("weibo_info:" + "昵称：" + opt.split("&")[1].split("=")[1]);
                weibo.setNickname(opt.split("&")[1].split("=")[1]);

                Element datea = feed.select(".WB_detail a[name]").get(0);
                log.info("weibo_info:" + "发布时间: " + datea.attr("title"));
                weibo.setCreateDate(datea.attr("title"));
                if (datea.nextElementSibling() != null) {
                    log.info("weibo_info:" + "来源：" + datea.nextElementSibling().html());
                    weibo.setSource(datea.nextElementSibling().html());
                }

                Element content = feed.select("div[node-type=feed_list_content]").get(0);
//                log.info("weibo_info:"+content.html());
                Elements unfold = content.select("a[action-type=fl_unfold]");
                if (unfold.size() > 0) {
                    if (unfold.get(0).text().startsWith("展开全文")) {
                        System.out.println("展开全文：");
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
                log.info("weibo_info:" + "内容：" + contentText);
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
                                System.out.println("原微博 展开全文：");
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
                        log.info("weibo_info:" + "   原内容 " + originContent);
                        originWeibo.put("originContent", originContent);
                        originWeibo.put("originContentHtml", originContentHtml);
                    }
                    Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                    String originPicIds = "";
                    if (pics != null) {
                        if (!(pics.attr("node-type").isEmpty())) {
                            originPicIds = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                            log.info("weibo_info:" + "原来微博的图片：" + originPicIds);
                        } else {
                            pics = pics.select(".WB_pic").first();
                            if (pics != null) {
                                originPicIds = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                                log.info("weibo_info:" + "原来微博的图片：" + originPicIds);
                            }
                        }
                    }
                    originWeibo.put("picids", originPicIds);

                    Element WB_func = forwardContentElem.select(".WB_func").first();
                    if (WB_func != null) {
                        Elements lis = WB_func.select("ul a");
                        if (lis.size() == 3) {
                            Element forwardA = lis.get(0);
                            String forwardCommandLink = "http:" + forwardA.attr("href");
                            forwardCommandLink = forwardCommandLink.substring(0, forwardCommandLink.indexOf("?"));
                            String forwardCount = forwardA.select("em").get(1).html();
                            if (!StringUtils.isNumeric(forwardCount)) {
                                forwardCount = "0";
                            }
                            originWeibo.put("forwardNumber", (Long.parseLong(forwardCount)));
                            originWeibo.put("forwardUrl", (forwardCommandLink + "?type=repost"));
                            log.info("weibo_info:" + "原转发：" + forwardCount + "  链接：" + forwardCommandLink + "?type=repost");

                            Element commentA = lis.get(1);
                            String commentCount = commentA.select("em").get(1).html();
                            if (!StringUtils.isNumeric(commentCount)) {
                                commentCount = "0";
                            }
                            originWeibo.put("commentNumber", Long.parseLong(commentCount));
                            originWeibo.put("forwardUrl", forwardCommandLink + "?type=comment");
                            log.info("weibo_info:" + "原评论：" + commentCount + "  链接：" + forwardCommandLink + "?type=comment");

                            Element likeA = lis.get(2);
                            String likeCount = likeA.select("em").get(1).html();
                            if (!StringUtils.isNumeric(likeCount)) {
                                likeCount = "0";
                            }
                            originWeibo.put("likeNumber", Long.parseLong(likeCount));
                            log.info("weibo_info:" + "原赞：" + likeCount + "  链接：" + forwardCommandLink + "?type=like");
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
                log.info("weibo_info:" + "话题：" + tps.replaceFirst("#", ""));
                weibo.setTopics(tps.replaceFirst("#", ""));
                Elements video = feed.select(".WB_detail .WB_video");
                if (video.size() > 0) {
                    String actionData = video.attr("action-data");
                    String video_src = crawleUtils.getValue(actionData, "video_src");
                    String url = video_src.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    String urlStr = "http:" + URLDecoder.decode(url, "UTF-8");
                    log.info("weibo_info:" + "视频地址：" + urlStr);
//                    weibo.setVideo_src(urlStr);
                }
                Element WB_row_line = feed.select(".WB_row_line").first();
                Element forwardA = WB_row_line.select("li a[action-type=fl_forward]").first();
                String forwardCommandLink = "http:" + crawleUtils.getValue(forwardA.attr("action-data"), "url");
                String forwardCount = forwardA.select("em").get(1).html();
                if (!StringUtils.isNumeric(forwardCount)) {
                    forwardCount = "0";
                }
                weibo.setForwardNumber(Long.parseLong(forwardCount));
//                weibo.setForwardUrl(forwardCommandLink + "?type=repost");
//                log.info("weibo_info:" + "转发：" + forwardCount + "  链接：" + forwardCommandLink + "?type=repost");

                Element commentA = WB_row_line.select("li a[action-type=fl_comment]").first();
                String commentCount = commentA.select("em").get(1).html();
                if (!StringUtils.isNumeric(commentCount)) {
                    commentCount = "0";
                }
                weibo.setCommentNumber(Long.parseLong(commentCount));
                weibo.setForwardUrl(forwardCommandLink + "?type=comment");
//                log.info("weibo_info:" + "评论：" + commentCount + "  链接：" + forwardCommandLink + "?type=comment");

                Element likeA = WB_row_line.select("li span[node-type=like_status]").first();
                String likeCount = likeA.select("em").get(1).html();
                if (!StringUtils.isNumeric(likeCount)) {
                    likeCount = "0";
                }
                weibo.setLikeNumber(Long.parseLong(likeCount));
                log.info("赞：" + likeCount + ",评论：" + commentCount + ",转发：" + forwardCount);

                Element pics = feed.select(".WB_media_wrap .media_box .WB_media_a").first();
                if (pics != null) {
                    if (!(pics.attr("node-type").isEmpty())) {
                        String pic_ids = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                        log.info("weibo_info:" + "图片：" + pic_ids);
                        weibo.setPicids(pic_ids);
                    } else {
                        pics = pics.select(".WB_pic").first();
                        if (pics != null) {
                            String pic_ids = crawleUtils.getValue(pics.attr("action-data"), "pic_ids");
                            weibo.setPicids(pic_ids);
                            log.info("weibo_info:" + "图片：" + pic_ids);
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
