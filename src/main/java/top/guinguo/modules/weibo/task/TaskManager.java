package top.guinguo.modules.weibo.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.dao.UserDao;
import top.guinguo.modules.weibo.model.*;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.WeiboService;
import top.guinguo.modules.weibo.utils.Contants;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guin_guo on 2018/3/10.
 */
public class TaskManager {
    private int initPoolSize = 12;
    private ExecutorService fixedThreadPool;
    private HBaseDaoImlp hBaseDaoImlp;
    private IWeiboService weiboService;
    private UserDao userDao;
    // 创建一个数值格式化对象
    private NumberFormat numberFormat = NumberFormat.getInstance();
    private static String SCITY = "北京 上海 天津 重庆 香港 澳门 海外";

    public TaskManager(int initPoolSize) {
        this.initPoolSize = initPoolSize;
        this.hBaseDaoImlp = HBaseDaoImlp.getInstance();
        this.weiboService = WeiboService.getInstance();
        this.userDao = UserDao.getInstance();
        fixedThreadPool = Executors.newFixedThreadPool(initPoolSize);
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
    }
    private static class DefaultInstance {
        public static TaskManager instance = new TaskManager(12);
    }
    public static TaskManager getInstance() {
        return DefaultInstance.instance;
    }

    public void runTask(Task task) {
        System.out.println("running task: " + task);
        fixedThreadPool.submit(new TaskRunner(task));
    }

    class TaskRunner implements Runnable {
        private Task task;
        public TaskRunner(Task task) {
            this.task = task;
        }
        @Override
        public void run() {
            List<Weibo> weibos;
            try {
                weibos = weiboService.getWeiboByUid(task.getUserid());
                Map<String, List<MidleWeibo>> top5 = getTop5(weibos);
                List<String> worldCloud = getWordCloud(weibos);
                List<Label> getUserLabels = getUserLabels(weibos);
                //status for 50%
                JSONObject getAraeDatas = getAraeDatas(task.getUser().getAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * top 5
         * @param weiboList
         * @return
         */
        private Map<String, List<MidleWeibo>> getTop5(List<Weibo> weiboList) {
            Collections.reverse(weiboList);
            List<MidleWeibo> latest = new ArrayList<>(5);
            //latest 5
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                latest.add(midleWeibo);
            }
            //mostComments 5
            Collections.sort(weiboList, (o1, o2) -> o2.getCommentNumber().compareTo(o1.getCommentNumber()));
            List<MidleWeibo> mostComments = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                mostComments.add(midleWeibo);
            }
            //mostForwards
            Collections.sort(weiboList, (o1, o2) -> o2.getForwardNumber().compareTo(o1.getForwardNumber()));
            List<MidleWeibo> mostForwards = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getForwardNumber().intValue());
                mostForwards.add(midleWeibo);
            }
            //mostLikes
            Collections.sort(weiboList, (o1, o2) -> o2.getLikeNumber().compareTo(o1.getLikeNumber()));
            List<MidleWeibo> mostLikes = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getLikeNumber().intValue(), weibo.getContent());
                mostLikes.add(midleWeibo);
            }
            Map<String, List<MidleWeibo>> top5 = new HashMap<>(4);
            top5.put("latest", latest);
            top5.put("mostComments", mostComments);
            top5.put("mostForwards", mostForwards);
            top5.put("mostLikes", mostLikes);
            return top5;
        }

        /**
         * 所有微博的前十个关键词
         * @param weiboList
         * @return
         */
        private List<String> getWordCloud(List<Weibo> weiboList) {
            StringBuilder sb = new StringBuilder("");
            for (Weibo weibo : weiboList) {
                sb.append(weibo.getContent()).append("。");
            }
            List<String> kws2 = HanLP.extractKeyword(sb.toString(), 50);
            return kws2;
        }
        /**
         * 标签
         * @param weiboList
         * @return
         */
        private List<Label> getUserLabels(List<Weibo> weiboList) {
            Set<String> texts = new HashSet<>();
            Map<String, Integer> textMap = new HashMap<>();
            int sum = 0;
            for (Weibo weibo : weiboList) {
                String topics = weibo.getTopics();
                if (StringUtils.isNotEmpty(topics)) {
                    String[] ts = topics.split("#");
                    for (String t : ts) {
                        sum++;
                        texts.add(t);
                        if (textMap.get(t) != null) {
                            textMap.put(t, textMap.get(t) + 1);
                        } else {
                            textMap.put(t, 1);
                        }
                    }
                }
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<>(textMap.entrySet());
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            int i = 0;
            List<Label> labels = new ArrayList<>(10);
            for (Map.Entry<String, Integer> map : list) {
                Label label = new Label(map.getKey(), map.getValue() / sum * 1.0);
                labels.add(label);
                if (++i == 10) {
                    break;
                }
            }
            return labels;
        }

        /**
         * 获取同一个地区的用户数据
         * @param address
         * @return
         */
        private JSONObject getAraeDatas(String address) {
            if (StringUtils.isEmpty(address)) {
                return null;
            }
            JSONObject areaDatas = new JSONObject();
            List<User> areaUsers = userDao.getByAddress(address);
            //1. sex 性别
            JSONObject sexRatio = getSexRadio(areaUsers);
            //2. city 城市
            JSONObject citys = getCitys(address);
            //3. level 等级
            //4. blog age 博龄
            //5. member level 会员等级
            //6. fans range 用户粉丝分组
            //7. company tag 公司分组
            //8. school range 学校排行
            // total
            return null;
        }

        /**
         * 获取性别比例
         *
         "sexRatio": {
             "man": {
                 "number": 45,
                 "ratio": "56.3%"
             },
             "woman": {
                 "number": 35,
                 "ratio": "43.8%"
             },
             "unknown": {
                 "number": 0,
                 "ratio": "0.0%"
             }
         },
         * @param users
         * @return
         */
        private JSONObject getSexRadio(List<User> users) {
            JSONObject sexRadio = new JSONObject();
            List<Map<String, Object>> levels = new ArrayList<>();
            //sex
            int man = 0;
            int woman = 0;

            //level
            int level0To10 = 0;
            int level11To20 = 0;
            int level21To30 = 0;
            int level31To40 = 0;
            int level41To48 = 0;

            //blogAge

            for (User user : users) {
                if ("男".equals(user.getSex())) {
                    man++;
                } else {
                    woman++;
                }
                int level = user.getLevel();
                if (level >= 0 && level <= 10) {
                    level0To10++;
                } else if (level >= 11 && level <= 20) {
                    level11To20++;
                } else if (level >= 21 && level <= 30) {
                    level21To30++;
                } else if (level >= 31 && level <= 40) {
                    level31To40++;
                } else if (level >= 41 && level <= 48) {
                    level41To48++;
                }
            }
            int total = man + woman;
            JSONObject manMap = new JSONObject();
            manMap.put("size", man);
            JSONObject womanMap = new JSONObject();
            womanMap.put("size", woman);
            if (total != 0) {
                manMap.put("radio", numberFormat.format((float) man / (float) total * 100)+"%");
                womanMap.put("radio", numberFormat.format((float) woman / (float) total * 100)+"%");
            } else {
                manMap.put("radio", "0.0%");
                womanMap.put("radio", "0.0%");
            }
            sexRadio.put("man", manMap);
            sexRadio.put("woman", womanMap);

            Map<String, Object> map = new HashedMap(2);
            map.put("level", "0-10级");
            map.put("count", level0To10);
            levels.add(map);
            map = new HashedMap(2);
            map.put("level", "11-20级");
            map.put("count", level11To20);
            levels.add(map);
            map = new HashedMap(2);
            map.put("level", "21-30级");
            map.put("count", level21To30);
            levels.add(map);
            map = new HashedMap(2);
            map.put("level", "31-40级");
            map.put("count", level31To40);
            levels.add(map);
            map = new HashedMap(2);
            map.put("level", "41-48级");
            map.put("count", level41To48);
            levels.add(map);
            Collections.sort(levels, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return Integer.compare(Integer.parseInt(o1.get("count").toString()), Integer.parseInt(o2.get("count").toString()));
                }
            });

            return sexRadio;
        }

        /**
         *
         * @param address
         * @return
         */
        private JSONObject getCitys(String address) {
            JSONObject res = new JSONObject();
            List<Map<String, Object>> city = userDao.getCitys(address);
            res.put("city", city);
            if (address.contains(" ")) {
                address = address.split(" ")[0];
            }
            res.put("provice", address);
            res.put("total", city.size());
            return res;
        }
    }
}
