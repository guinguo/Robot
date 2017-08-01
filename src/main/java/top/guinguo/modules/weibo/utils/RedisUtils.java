package top.guinguo.modules.weibo.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;

import java.util.List;
import java.util.Map;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-26 17:45
 * @版本: v1.0
 */
public class RedisUtils {

    public static final String IP = "redis.ip";
    public static final String PORT = "redis.port";

    private static String ip;
    private static int port;
    private static JedisPool jedisPool = null;
    private static Configurator configurator  = null;

    static {
        configurator = Configurator.getInstance();
        ip = configurator.get(IP, "127.0.0.1");
        port = configurator.getInt(PORT, 6379);
        JedisPoolConfig config = new JedisPoolConfig();
        jedisPool = new JedisPool(config, ip, port);
    }

    public RedisUtils() {
    }
    private static class DefaultInstance {
        public static RedisUtils instance = new RedisUtils();
    }
    public static RedisUtils getInstance() {
        return DefaultInstance.instance;
    }

    public String get(String key) {
        Jedis jedis = getJedis();
        String s = jedis.get(key);
        jedis.close();
        return s;
    }

    public String set(String key, String value) {
        Jedis jedis = getJedis();
        String s = jedis.set(key, value);
        jedis.close();
        return s;
    }

    /**
     * 从jedis连接池中获取获取jedis对象
     * @return
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void loadData() throws Exception {
        HBaseDaoImlp hBaseDao = HBaseDaoImlp.getInstance();
        List<Map<String, Object>> list = hBaseDao.queryAll(Contants.T_USER, false);
        Jedis jedis = getJedis();
        for (Map<String, Object> user : list) {
            if (jedis.get(user.get("rowkey").toString()) == null) {
                jedis.set(user.get("rowkey").toString(), user.get("blogNumber")+"-"+user.get("fans")+"-"+user.get("focus"));
            }
        }
        jedis.close();
    }
}
