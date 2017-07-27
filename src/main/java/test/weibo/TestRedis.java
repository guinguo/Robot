package test.weibo;

import org.junit.Test;
import top.guinguo.modules.weibo.utils.RedisUtils;

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
}
