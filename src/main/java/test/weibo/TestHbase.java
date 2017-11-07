package test.weibo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hsqldb.types.JavaObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.utils.Contants;
import top.guinguo.modules.weibo.utils.CrawleHttpFactory;
import top.guinguo.modules.weibo.utils.RedisUtils;

import java.io.*;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-20 17:22
 * @版本: v1.0
 */
public class TestHbase {
    final Logger log = LoggerFactory.getLogger(this.getClass());
    @Test
    public void test01() throws Exception {
        HBaseDaoImlp hBaseDaoImlp = HBaseDaoImlp.getInstance();
        hBaseDaoImlp.queryByColumn(Contants.T_USER, "address", "广西 南宁");
    }
    @Test
    public void test02() throws Exception {
        String html = "{\"userInfo\":{\"id\":1000000125,\"screen_name\":\"小芳alive\",\"profile_image_url\":\"https:\\/\\/tva1.sinaimg.cn\\/crop.0.0.852.852.180\\/3b9aca7djw8en36s75osbj20no0np40p.jpg\",\"profile_url\":\"https:\\/\\/m.weibo.cn\\/u\\/1000000125?uid=1000000125&luicode=10000011&lfid=1005051000000125\",\"statuses_count\":63,\"verified\":false,\"verified_type\":-1,\"description\":\"\",\"gender\":\"f\",\"mbtype\":0,\"urank\":4,\"mbrank\":0,\"follow_me\":false,\"following\":false,\"followers_count\":19,\"follow_count\":100,\"cover_image_phone\":\"https:\\/\\/tva1.sinaimg.cn\\/crop.0.0.640.640.640\\/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg\",\"toolbar_menus\":[{\"type\":\"profile_follow\",\"name\":\"关注\",\"pic\":\"\",\"params\":{\"uid\":1000000125}},{\"type\":\"link\",\"name\":\"聊天\",\"pic\":\"http:\\/\\/h5.sinaimg.cn\\/upload\\/2015\\/06\\/12\\/2\\/toolbar_icon_discuss_default.png\",\"params\":{\"scheme\":\"sinaweibo:\\/\\/messagelist?uid=1000000125&nick=\"},\"scheme\":\"https:\\/\\/passport.weibo.cn\\/signin\\/welcome?entry=mweibo&r=https://m.weibo.cn/api/container/getIndex?jumpfrom=wapv4&tip=1&type=uid&containerid=1005051000000125\"},{\"type\":\"link\",\"name\":\"文章\",\"pic\":\"\",\"params\":{\"scheme\":\"sinaweibo:\\/\\/cardlist?containerid=2303190002_445_1000000125_WEIBO_ARTICLE_LIST_DETAIL&count=20\"},\"scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=2303190002_445_1000000125_WEIBO_ARTICLE_LIST_DETAIL&count=20&luicode=10000011&lfid=1005051000000125\"}],\"fans_scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=231051_-_fansrecomm_-_1000000125&luicode=10000011&lfid=1005051000000125\",\"follow_scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=231051_-_followersrecomm_-_1000000125&luicode=10000011&lfid=1005051000000125\"},\"fans_scheme\":\"sinaweibo:\\/\\/cardlist?containerid=231051_-_fansrecomm_-_1000000125\",\"follow_scheme\":\"sinaweibo:\\/\\/cardlist?containerid=231051_-_followersrecomm_-_1000000125\",\"tabsInfo\":{\"selectedTab\":1,\"tabs\":[{\"title\":\"主页\",\"tab_type\":\"profile\",\"containerid\":\"2302831000000125\"},{\"title\":\"微博\",\"tab_type\":\"weibo\",\"containerid\":\"1076031000000125\",\"url\":\"\\/index\\/my\"}]},\"ok\":1,\"showAppTips\":0,\"scheme\":\"sinaweibo:\\/\\/userinfo?uid=1000000125&luicode=10000011&lfid=1076031000000125&featurecode=\"}";
        JSONObject userInfo = JSONObject.parseObject(html).getJSONObject("userInfo");
        String sex = ("m".equals(userInfo.getString("gender")) ? "男" : "女");
        System.out.println(sex);
    }
    @Test
    public void test03() throws Exception {
        HBaseDaoImlp hBaseDaoImlp = HBaseDaoImlp.getInstance();
        List<Map<String, Object>> list = hBaseDaoImlp.scaneByRange("weibo4", "6026962315_0000000000000000", "6026962315_9000000000000000", false);
        List<String> contents = new ArrayList<>(list.size());
        StringBuilder sb = new StringBuilder("");
        for (Map<String, Object> map : list) {
            String content = map.get("content").toString();
//            System.out.println("rowkey:" + map.get("rowkey") + ",content:" + content);
            contents.add(content);
            sb.append(content + "\n");
        }
        StringBuilder sb2 = new StringBuilder("");
        for (String c : contents) {
            sb2.append(HanLP.extractKeyword(c, 1).size() > 0 ? HanLP.extractKeyword(c, 1).get(0) + "," : "");
        }
        StringBuilder sb3 = new StringBuilder("");
        List<String> kws1 = HanLP.extractKeyword(sb2.toString(), 10);
        List<String> kws2 = HanLP.extractKeyword(sb.toString(), 10);
        System.out.println("每条微博的前十个关键词");
        for (String kw : kws1) {
            System.out.print(kw + ",");
            sb3.append(kw + ",");
        }
        System.out.println();
        System.out.println("所有微博的前十个关键词");
        for (String kw : kws2) {
            System.out.print(kw + ",");
            sb3.append(kw + ",");
        }
        List<String> kws3 = HanLP.extractKeyword(sb3.toString(), 10);
        System.out.println();
        System.out.println(list.get(0).get("nickname") + "前十个关键词");
        for (String kw : kws3) {
            System.out.print(kw + ",");
        }
    }
    @Test
    public void test04() throws Exception {
        JSONArray array = JSON.parseArray("[ { \"pid\": \"006axyoply1fhrjlae13fj30qo0zkn90\", \"url\": \"https: //wx2.sinaimg.cn/orj360/006axyoply1fhrjlae13fj30qo0zkn90.jpg\" }, { \"pid\": \"006axyoply1fhrjl9au7lj30qo0zk7ez\", \"url\": \"https: //wx2.sinaimg.cn/orj360/006axyoply1fhrjl9au7lj30qo0zk7ez.jpg\" }, { \"pid\": \"006axyoply1fhrjlbckkkj30qo0zktgg\", \"url\": \"https: //wx3.sinaimg.cn/orj360/006axyoply1fhrjlbckkkj30qo0zktgg.jpg\" } ]");
        JSONArray array1 = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            array1.add(array.getJSONObject(i).getString("pid"));
        }
        System.out.println(array1.toJSONString());
    }
    @Test
    public void test05() throws Exception {
        RedisUtils redisUtils = RedisUtils.getInstance();
        redisUtils.loadData();
    }


    @Test
    public void test06() throws Exception {
        File file = new File("G:/user4.json");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(isr);
        String s = null;
        if (reader != null) {
            s = reader.readLine();
        }
        ExecutorService exePool = Executors.newCachedThreadPool();
        if (s != null) {
            JSONArray jsonArray = JSONArray.parseArray(s);

            int count = jsonArray.size() / 1000 + 1;

            for (int i = 0;i<count;i++) {
                int index = i * 1000;
                List<String> users = new ArrayList<>(1000);
                int j = 0;
                while (j < 1000 && index < jsonArray.size()) {
                    users.add(jsonArray.get(index++).toString());
                    j++;
                }
                exePool.submit(new GetRegisterDate(users));
            }
//            exePool.shutdown();
        }

    }

    class GetRegisterDate implements Runnable {

        private List<String> users;
        private CrawleHttpFactory crawleHttpFactory;
        RedisUtils redisUtils = RedisUtils.getInstance();

        public GetRegisterDate(List<String> users) {
            this.users = users;
            this.crawleHttpFactory = CrawleHttpFactory.getInstance();
        }

        @Override
        public void run() {
            String resultStr = null;
            CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
            String mainInfo = "https://m.weibo.cn/api/container/getIndex?containerid=230283%1$s_-_INFO";
            Jedis jedis = redisUtils.getJedis();
            for (String uid : users) {
                JSONObject result = null;
                try {
                    resultStr = crawleHttpFactory.getRespStr(client, String.format(mainInfo, uid));
                } catch (NoRouteToHostException nrte) {
                    nrte.printStackTrace();
                    log.error("[getOneBlogs Exception]: " + nrte.getMessage());
                    try {
                        client.close();
                        Thread.sleep(5 * 60 * 1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("[getOneBlogs]: " + e.getMessage());
                    try {
                        client.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                result = JSONObject.parseObject(resultStr);
                String registerDate = getUserRegDate(uid, result);
                if (registerDate != null) {
//                    jedis.append("reg_" + uid, registerDate);
                }
            }
            jedis.close();
        }
        public String getUserRegDate(String uid, JSONObject result) {
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
                                if ("注册时间".equals(itemName)) {
                                    log.info("weibo_info:" + "注册时间: " + ",-->" + itemValue);
                                    return itemValue;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
