package top.guinguo.modules.weibo.dao;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.UserData;
import top.guinguo.modules.weibo.utils.DBManager;
import top.guinguo.modules.weibo.utils.ObjectUtil;
import top.guinguo.modules.weibo.utils.Pager;
import top.guinguo.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by guin_guo on 2018/03/09.
 */
@Repository("userDao")
public class UserDao {

    private DBManager dbManager = new DBManager();
    private static String SCITY = "北京 上海 天津 重庆 香港 澳门 海外";

    public UserDao() {
    }

    private static class DefaultInstance {
        public static UserDao instance = new UserDao();
    }

    public static UserDao getInstance() {
        return DefaultInstance.instance;
    }

    public Pager<UserData> list(Integer num, Integer pageSize, String username) {
        if (num == null) {
            num = 1;//默认第一页
        }
        if (pageSize == null || pageSize > Constant.MAX_PAGE_SIZE) {
            pageSize = Constant.PAGE_SIZE;
        }
        String sql = "select * from user ";
        String countSql = "select count(*) from user ";
        if (username != null && username.length() > 0) {
            sql += "where nickname like \"%" + username + "%\" ";
            countSql += "where nickname like \"%" + username + "%\" ";
        }
        sql += "limit " + (num - 1) * pageSize + ", " + pageSize;
        Pager<UserData> userPager = new Pager<>();
        List<UserData> users;
        try {
            dbManager.getConnection();
            ResultSet res = null;
            ResultSet countRes = null;
            res = dbManager.query(sql);
            countRes = dbManager.query(countSql);
            users = new ArrayList<>();
            while (res.next()) {
                UserData user = ObjectUtil.getObject(res, UserData.class);
                users.add(user);
            }
            if (countRes.next()) {
                userPager.setTotal(countRes.getInt(1));
            }
            userPager.setDatas(users);
            userPager.setNum(num);
            userPager.setPageSize(pageSize);
            dbManager.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userPager;
    }

    /**
     * 根据id获取用户数据
     *
     * @param uid
     * @return
     */
    public User getById(String uid) {
        User user = null;
        try {
            String sql = "select * from user where id = ?";
            dbManager.getConnection();
            ResultSet res;
            res = dbManager.query(sql, uid);
            while (res.next()) {
                user = ObjectUtil.getObject(res, User.class);
                if (user != null && user.getId() != null) {
                    break;
                }
            }
            dbManager.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }

    /**
     * 根据地址获取同省user
     *
     * @param address
     * @return
     */
    public List<User> getByAddress(String address) {
        List<User> users = new ArrayList<>();
        if (StringUtils.isEmpty(address)) {
            return users;
        }
        String[] as = address.split(" ");
        String provice = address;
        if (as.length == 2) {
            provice = as[0];
        }
        String sql = "select * from user where address like '" + provice + "%' and address <> '" + provice + "'";
        try {
            dbManager.getConnection();
            ResultSet res;
            res = dbManager.query(sql);
            users = new ArrayList<>();
            while (res.next()) {
                User user = ObjectUtil.getObject(res, User.class);
                users.add(user);
            }
            dbManager.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    /**
     * 获取相同省份不同城市的用户分布
     * @param address
     * @return
     */
    public List<Map<String, Object>> getCitys(String address) {
        List<Map<String, Object>> citys = new ArrayList<>();
        if (StringUtils.isEmpty(address)) {
            return citys;
        }
        //北京 上海 天津 重庆 香港 澳门 海外
        boolean isSpecial = false;
        String[] as = address.split(" ");
        String provice = address;
        if (as.length == 2) {
            provice = as[0];
        }
        if (SCITY.contains(provice)) {
            isSpecial = true;
        }
        String sql = "SELECT address, count(*) as value from user where address like '" + provice + "%' ";
        if (as.length == 2) {
            sql += "and address <> '" + provice + "' ";
        }
        sql += "GROUP BY address ORDER BY value desc";
        try {
            dbManager.getConnection();
            ResultSet res;
            res = dbManager.query(sql);
            citys = new ArrayList<>();
            while (res.next()) {
                Map<String, Object> ct = new HashedMap(2);
                String resAddres = res.getString("address");
                if (resAddres.contains(" ")) {
                    if (!isSpecial) {
                        ct.put("name", resAddres.split(" ")[1] + "市");
                    } else {
                        ct.put("name", resAddres.split(" ")[1]);
                    }
                } else {
                    ct.put("name", resAddres);
                }
                citys.add(ct);
            }
            dbManager.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return citys;
    }

}
