package test.weibo;

import com.alibaba.fastjson.JSONObject;
import org.hsqldb.types.JavaObject;
import org.junit.Test;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-20 17:22
 * @版本: v1.0
 */
public class TestHbase {
    @Test
    public void test01() throws Exception {
        HBaseDaoImlp hBaseDaoImlp = HBaseDaoImlp.getInstance();
        hBaseDaoImlp.queryByColumn("user3", "sex", "女");
    }
    @Test
    public void test02() throws Exception {
        String html = "{\"userInfo\":{\"id\":1000000125,\"screen_name\":\"小芳alive\",\"profile_image_url\":\"https:\\/\\/tva1.sinaimg.cn\\/crop.0.0.852.852.180\\/3b9aca7djw8en36s75osbj20no0np40p.jpg\",\"profile_url\":\"https:\\/\\/m.weibo.cn\\/u\\/1000000125?uid=1000000125&luicode=10000011&lfid=1005051000000125\",\"statuses_count\":63,\"verified\":false,\"verified_type\":-1,\"description\":\"\",\"gender\":\"f\",\"mbtype\":0,\"urank\":4,\"mbrank\":0,\"follow_me\":false,\"following\":false,\"followers_count\":19,\"follow_count\":100,\"cover_image_phone\":\"https:\\/\\/tva1.sinaimg.cn\\/crop.0.0.640.640.640\\/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg\",\"toolbar_menus\":[{\"type\":\"profile_follow\",\"name\":\"关注\",\"pic\":\"\",\"params\":{\"uid\":1000000125}},{\"type\":\"link\",\"name\":\"聊天\",\"pic\":\"http:\\/\\/h5.sinaimg.cn\\/upload\\/2015\\/06\\/12\\/2\\/toolbar_icon_discuss_default.png\",\"params\":{\"scheme\":\"sinaweibo:\\/\\/messagelist?uid=1000000125&nick=\"},\"scheme\":\"https:\\/\\/passport.weibo.cn\\/signin\\/welcome?entry=mweibo&r=https://m.weibo.cn/api/container/getIndex?jumpfrom=wapv4&tip=1&type=uid&containerid=1005051000000125\"},{\"type\":\"link\",\"name\":\"文章\",\"pic\":\"\",\"params\":{\"scheme\":\"sinaweibo:\\/\\/cardlist?containerid=2303190002_445_1000000125_WEIBO_ARTICLE_LIST_DETAIL&count=20\"},\"scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=2303190002_445_1000000125_WEIBO_ARTICLE_LIST_DETAIL&count=20&luicode=10000011&lfid=1005051000000125\"}],\"fans_scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=231051_-_fansrecomm_-_1000000125&luicode=10000011&lfid=1005051000000125\",\"follow_scheme\":\"https:\\/\\/m.weibo.cn\\/p\\/index?containerid=231051_-_followersrecomm_-_1000000125&luicode=10000011&lfid=1005051000000125\"},\"fans_scheme\":\"sinaweibo:\\/\\/cardlist?containerid=231051_-_fansrecomm_-_1000000125\",\"follow_scheme\":\"sinaweibo:\\/\\/cardlist?containerid=231051_-_followersrecomm_-_1000000125\",\"tabsInfo\":{\"selectedTab\":1,\"tabs\":[{\"title\":\"主页\",\"tab_type\":\"profile\",\"containerid\":\"2302831000000125\"},{\"title\":\"微博\",\"tab_type\":\"weibo\",\"containerid\":\"1076031000000125\",\"url\":\"\\/index\\/my\"}]},\"ok\":1,\"showAppTips\":0,\"scheme\":\"sinaweibo:\\/\\/userinfo?uid=1000000125&luicode=10000011&lfid=1076031000000125&featurecode=\"}";
        JSONObject userInfo = JSONObject.parseObject(html).getJSONObject("userInfo");
        String sex = ("m".equals(userInfo.getString("gender")) ? "男" : "女");
        System.out.println(sex);
    }
}
