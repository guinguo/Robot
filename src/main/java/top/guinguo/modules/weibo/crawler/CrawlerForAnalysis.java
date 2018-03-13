package top.guinguo.modules.weibo.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.utils.Contants;
import top.guinguo.modules.weibo.utils.CrawleHttpFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static top.guinguo.utils.HttpUtil.USER_AGEN;

/**
 * Created by guin_guo on 2018/03/13.
 */
public class CrawlerForAnalysis {

    private int sleepInterval = 5000;
    private String fansUrl = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_%1$s&luicode=10000011&lfid=100505%2$s&type=uid&value=%3$s&since_id=%4$d";
    private int toCrawlWbNumber = 75;
    private CrawleHttpFactory crawleHttpFactory = CrawleHttpFactory.getInstance();

    /**
     * 获取用户的粉丝
     * @param uid
     * @return
     */
    public List<User> getFans(String uid) {
        List<User> users = new ArrayList<>();
        try {
            int i = 1;
            JSONObject firstPage = getFanUser(uid, i++);
            List<User> eachPageUser = getUsersFromJson(firstPage);
            users.addAll(eachPageUser);
            while ("1".equals(firstPage.getString("ok"))) {
                long tmp = (long) (sleepInterval / 2 * Contants.intervalRadio);
                Thread.sleep(tmp);
                firstPage = getFanUser(uid, i++);
                eachPageUser = getUsersFromJson(firstPage);
                users.addAll(eachPageUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public JSONObject getFanUser(String uid, int page) throws IOException {
//        log.info("weibo_fans_page:" + "\t" + page);
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGEN).build();
        String respStr = crawleHttpFactory.getRespStr(client, String.format(fansUrl, uid, uid, uid, page));
        client.close();
        try {
            long tmp = (long) (sleepInterval / 2 * Contants.intervalRadio);
            Thread.sleep(tmp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject json = JSON.parseObject(respStr);
//        log.info("---------");
        return json;
    }


    /**
     * json 转 user list
     * @param jsonObject
     * @return
     */
    public List<User> getUsersFromJson(JSONObject jsonObject) {
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
}
