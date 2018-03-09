package top.guinguo.modules.weibo.dao;

import org.springframework.stereotype.Repository;
import top.guinguo.modules.weibo.model.UserData;
import top.guinguo.modules.weibo.utils.DBManager;
import top.guinguo.modules.weibo.utils.ObjectUtil;
import top.guinguo.modules.weibo.utils.Pager;
import top.guinguo.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by guin_guo on 2018/03/09.
 */
@Repository("userDao")
public class UserDao {

    private DBManager dbManager = new DBManager();
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
}
