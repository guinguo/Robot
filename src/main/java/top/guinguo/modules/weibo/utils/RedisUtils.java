package top.guinguo.modules.weibo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Set<String> keys(String pattern) {
        Jedis jedis = getJedis();
        Set<String> set = jedis.keys(pattern);
        jedis.close();
        return set;
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
    public void loadDBData() throws Exception {
        HBaseDaoImlp hBaseDao = HBaseDaoImlp.getInstance();
        List<Map<String, Object>> list = hBaseDao.queryAll(Contants.T_USER, false);
//        List<Map<String, Object>> list = hBaseDao.queryByColumn(Contants.T_USER, "address", "广西 南宁");
        DBManager dbManager = new DBManager();
        Connection connection = dbManager.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement cmd = connection.prepareStatement("UPDATE `weibo_analysis`.`user` " +
                "SET company=?, intro=?, school=?, registedDate=?, credit=? WHERE id=?");
        Jedis jedis = getJedis();
        int i = 1;
        for (Map<String, Object> user : list) {
            String userId = user.get("rowkey").toString();
            cmd.setString(1, user.get("company") == null ? "" : user.get("company").toString());
            cmd.setString(2, user.get("intro") == null ? "" : user.get("intro").toString());
            cmd.setString(3, user.get("school") == null ? "" : user.get("school").toString());
            Timestamp createTime = null;
            if (user.get("registedDate") != null) {
                createTime = new Timestamp(DateUtils.parse(user.get("registedDate").toString()).getTime());
            }
            cmd.setTimestamp(4, createTime);
            String credit = null;
            if (user.get("meta") != null) {
                JSONObject meta = JSON.parseObject(user.get("meta").toString());
                if (meta.get("credit") != null) {
                    credit = meta.getString("credit");
                }
            }
            cmd.setString(5, credit);
            cmd.setString(6, userId);
            i++;
            cmd.addBatch();
            if(i%1000==0){
                cmd.executeBatch();
                i = 1;
            }
        }
        cmd.executeBatch();
        connection.commit();
        cmd.close();
        connection.close();
        jedis.close();
    }
}
