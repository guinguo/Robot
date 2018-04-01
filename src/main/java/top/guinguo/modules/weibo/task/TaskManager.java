package top.guinguo.modules.weibo.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.dao.UserDao;
import top.guinguo.modules.weibo.model.*;
import top.guinguo.modules.weibo.service.ITaskService;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.TaskService;
import top.guinguo.modules.weibo.service.WeiboService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
    private ITaskService taskService;
    private UserDao userDao;
    // 创建一个数值格式化对象
    private NumberFormat numberFormat = NumberFormat.getInstance();

    public TaskManager(int initPoolSize) {
        this.initPoolSize = initPoolSize;
        this.hBaseDaoImlp = HBaseDaoImlp.getInstance();
        this.weiboService = WeiboService.getInstance();
        this.userDao = UserDao.getInstance();
        this.taskService= TaskService.getInstance();
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
        private DecimalFormat df = new DecimalFormat("#.0");
        public TaskRunner(Task task) {
            this.task = task;
        }
        @Override
        public void run() {
            List<Weibo> weibos;
            try {
                //status for 50%
                task.setStatus("50");
                updateTask(null);
                weibos = weiboService.getWeiboByUid(task.getUserid());
                JSONObject data = new JSONObject();
                Map<String, List<MidleWeibo>> top5 = getTop5(weibos);
                List<String> worldCloud = getWordCloud(weibos);
                List<Label> userLabels = getUserLabels(weibos);
                JSONObject araeDatas = getAraeDatas(task.getUser().getAddress());
                data.put("top5", top5);
                data.put("worldCloud", worldCloud);
                data.put("userLabels", userLabels);
                data.put("araeDatas", araeDatas);
                //画像任务小窗数据
                data.put("preferWordsInfo", worldCloud.subList(0, 3));
                data.put("myInterestsInfo", userLabels.subList(0, 3));
                task.setStatus("100");
                task.setFinishDate(new Timestamp(new Date().getTime()));
                updateTask(data.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean updateTask(String data) {
            int updateCount = 0;
            if (data != null) {
                updateCount = taskService.updateTaskResult(data, task.getId());
            }
            if (data == null || updateCount == 1) {
                int res = taskService.updateTask(task);
                if (res == 1) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
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
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getId(), weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                latest.add(midleWeibo);
            }
            //mostComments 5
            Collections.sort(weiboList, (o1, o2) -> o2.getCommentNumber().compareTo(o1.getCommentNumber()));
            List<MidleWeibo> mostComments = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getId(), weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                mostComments.add(midleWeibo);
            }
            //mostForwards
            Collections.sort(weiboList, (o1, o2) -> o2.getForwardNumber().compareTo(o1.getForwardNumber()));
            List<MidleWeibo> mostForwards = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getId(), weibo.getContent(), weibo.getForwardNumber().intValue());
                mostForwards.add(midleWeibo);
            }
            //mostLikes
            Collections.sort(weiboList, (o1, o2) -> o2.getLikeNumber().compareTo(o1.getLikeNumber()));
            List<MidleWeibo> mostLikes = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getId(), weibo.getLikeNumber().intValue(), weibo.getContent());
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
                Label label = new Label(map.getKey(), Double.valueOf(df.format(map.getValue() * 1.0 / sum)));
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
            List<User> areaUsers = userDao.getByAddress(address);
            JSONObject areaUserData = getAreaUserData(areaUsers);
            JSONObject citys = getCitys(address);
            JSONObject cutData = new JSONObject();
            JSONArray jsonArray  = citys.getJSONArray("city");
            if (jsonArray != null && jsonArray.size() > 0) {
                cutData.put("gather", jsonArray.getJSONObject(0).getString("name"));
            }
            cutData.put("mostGender", areaUserData.getJSONObject("sexRadio").getString("most"));
            cutData.put("areaCount", areaUsers.size());
            areaUserData.put("cutData", cutData);
            areaUserData.put("citys", citys);
            return areaUserData;
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
        private JSONObject getAreaUserData(List<User> users) {
            JSONObject areaUserData = new JSONObject();
            //sex 性别
            //level 等级
            //blog age 博龄
            //member level 会员等级
            //fans range 粉丝数量分布
            //company tag 公司标签
            //school rank 学校排名
            JSONObject sexRadio = new JSONObject();
            List<Map<String, Object>> levels = new ArrayList<>();
            Map<Integer, Integer> blogAges = new HashedMap();
            Map<Integer, Integer> memberLevels = new HashedMap();
            Map<String, Integer> fansRange = new HashedMap();
            Map<String, Integer> companyTag = new HashedMap();
            Map<String, Integer> schoolRank = new HashedMap();

            //sex
            int man = 0;
            int woman = 0;

            //level
            int level0To10 = 0;
            int level11To20 = 0;
            int level21To30 = 0;
            int level31To40 = 0;
            int level41To48 = 0;

            //blog age
            Calendar cal = Calendar.getInstance();
            int yearNow = cal.get(Calendar.YEAR);

            //fans count

            //level
            int count1k = 0;
            int count2k = 0;
            int count5k = 0;
            int count1w = 0;
            int count10w = 0;
            int count50w = 0;
            int count100w = 0;
            int count500w = 0;
            int count1kw = 0;
            int count1y = 0;
            
            for (User user : users) {
                //sex
                if ("男".equals(user.getSex())) {
                    man++;
                } else {
                    woman++;
                }

                //level
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

                //blog age
                String registerDate = user.getRegistedDate();
                if (StringUtils.isNotEmpty(registerDate)) {
                    int year = Integer.parseInt(registerDate.substring(0, 4));
                    if (year != 1970) {
                        int blogAge = yearNow - year;
                        if (blogAges.get(blogAge) != null) {
                            blogAges.put(blogAge, blogAges.get(blogAge) + 1);
                        } else {
                            blogAges.put(blogAge, 1);
                        }
                    }
                }

                //member level
                int memberLevel = user.getMember();
                if (memberLevels.get(memberLevel) != null) {
                    memberLevels.put(memberLevel, memberLevels.get(memberLevel) + 1);
                } else {
                    memberLevels.put(memberLevel, 1);
                }
                
                //fansCount
                long fansCount = user.getFans();
                if (fansCount <= 1000) {
                    count1k++;
                } else if (fansCount <= 2000) {
                    count2k++;
                } else if (fansCount <= 5000) {
                    count5k++;
                } else if (fansCount <= 10000) {
                    count1w++;
                } else if (fansCount <= 100000) {
                    count10w++;
                } else if (fansCount <= 500000) {
                    count50w++;
                } else if (fansCount <= 1000000) {
                    count100w++;
                } else if (fansCount <= 5000000) {
                    count500w++;
                } else if (fansCount <= 10000000) {
                    count1kw++;
                } else if (fansCount <= 100000000) {
                    count1y++;
                }

                //company
                String company = user.getCompany();
                if (StringUtils.isNotEmpty(company)) {
                    if (companyTag.get(company) != null) {
                        companyTag.put(company, companyTag.get(company) + 1);
                    } else {
                        companyTag.put(company, 1);
                    }
                }

                //school
                String school = user.getSchool();
                if (StringUtils.isNotEmpty(school)) {
                    if (schoolRank.get(school) != null) {
                        schoolRank.put(school, schoolRank.get(school) + 1);
                    } else {
                        schoolRank.put(school, 1);
                    }
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
            sexRadio.put("most", man > woman ? "男" : "女");

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

            //fans count
            fansRange.put("0-1千", count1k);
            fansRange.put("1千-2千",count2k);
            fansRange.put("2千-5千", count5k);
            fansRange.put("5千-1万", count1w);
            fansRange.put("1万-10万", count10w);
            fansRange.put("10万-50万", count50w);
            fansRange.put("50万-1百万", count100w);
            fansRange.put("1百万-5百万", count500w);
            fansRange.put("5百万-1千万", count1kw);
            fansRange.put("1千万-1亿", count1y);
            
            Collections.sort(levels, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return Integer.compare(Integer.parseInt(o2.get("count").toString()), Integer.parseInt(o1.get("count").toString()));
                }
            });

            List<Map.Entry<Integer, Integer>> blogAgesList = new ArrayList<Map.Entry<Integer, Integer>>(blogAges.entrySet());
            Collections.sort(blogAgesList, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<Map.Entry<Integer, Integer>> memberLevelList = new ArrayList<Map.Entry<Integer, Integer>>(memberLevels.entrySet());
            Collections.sort(memberLevelList, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<Map.Entry<String, Integer>> fansRangeList = new ArrayList<Map.Entry<String, Integer>>(fansRange.entrySet());
            Collections.sort(fansRangeList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            List<Map.Entry<String, Integer>> companyTagList = new ArrayList<Map.Entry<String, Integer>>(companyTag.entrySet());
            Collections.sort(companyTagList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            //company
            List<Map<String, Object>> companyList = new ArrayList<>(companyTagList.size() <= 50 ? companyTagList.size() : 50);
            Map.Entry<String, Integer> m;
            for (int i=0;i<companyTagList.size()&&i<50;i++) {
                m = companyTagList.get(i);
                map = new HashedMap(2);
                map.put("text", m.getKey());
                map.put("size", m.getValue());
                companyList.add(map);
            }

            List<Map.Entry<String, Integer>> schoolList = new ArrayList<Map.Entry<String, Integer>>(schoolRank.entrySet());
            if (schoolList.size() > 10) {
                schoolList = schoolList.subList(0, 10);
            }
            Collections.sort(schoolList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            areaUserData.put("sexRadio", sexRadio);
            areaUserData.put("levels", levels);
            areaUserData.put("blogAges", blogAgesList);
            areaUserData.put("memberLevels", memberLevelList);
            areaUserData.put("fansRange", fansRangeList);
            areaUserData.put("companyTag", companyList);
            areaUserData.put("schoolRank", schoolList);
            areaUserData.put("fansNum", users.size());
            return areaUserData;
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
