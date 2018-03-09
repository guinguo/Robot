package test.weibo;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import top.guinguo.modules.weibo.utils.DBManager;
import top.guinguo.modules.weibo.utils.RedisUtils;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-20 17:22
 * @版本: v1.0
 */
public class TestRedis {
    @Test
    public void test01() throws Exception {
        RedisUtils redisUtils = RedisUtils.getInstance();
        redisUtils.set("k3", "333");
        System.out.println("k3: " + redisUtils.get("k3"));
        redisUtils.set("k4", "444");
        System.out.println("k4: " + redisUtils.get("k4"));
    }
    @Test
    public void test02() throws Exception {
        RedisUtils redisUtils = RedisUtils.getInstance();
        redisUtils.loadData();
    }
    @Test
    public void test03() throws Exception {
        RedisUtils redisUtils = RedisUtils.getInstance();
        Jedis jedis = redisUtils.getJedis();
        DBManager dbManager = new DBManager();
        ResultSet res = dbManager.query("select rowkey, blogNumber,focus,fans from user3");
        while (res.next()) {
            jedis.set(res.getString(1), res.getString(2) + "-" + res.getString(4) + "-" + res.getString(3));
        }
        dbManager.close();
        jedis.close();
    }
}
