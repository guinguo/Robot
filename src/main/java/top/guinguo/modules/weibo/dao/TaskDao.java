package top.guinguo.modules.weibo.dao;

import org.springframework.stereotype.Repository;
import top.guinguo.modules.weibo.model.Task;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by guin_guo on 2018/03/10.
 */
@Repository("taskDao")
public class TaskDao {

    private DBManager dbManager = new DBManager();
    public TaskDao() {
    }

    public boolean deleteTask(int id) {
        //todo 删除任务结果
        String sql = "delete from task where id = ?";
        dbManager.getConnection();
        try {
            dbManager.updateQuery(sql, id);
            dbManager.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    private static class DefaultInstance {
        public static TaskDao instance = new TaskDao();
    }
    public static TaskDao getInstance() {
        return DefaultInstance.instance;
    }

    public Pager<Task> list(Integer num, Integer pageSize, String status) {
        if (num == null) {
            num = 1;//默认第一页
        }
        if (pageSize == null || pageSize > Constant.MAX_PAGE_SIZE) {
            pageSize = Constant.TASK_PAGE_SIZE;
        }
        String sql = "select * from task left join user on task.userid=user.id ";
        String countSql = "select count(*) from task ";
        if (status != null && status.length() > 0) {
            if ("100".equals(status)) {
                sql += "where task.status = " + status + " ";
                countSql += "where task.status = " + status + " ";
            } else {
                sql += "where task.status is not null and task.status <> 100 ";
                countSql += "where task.status is not null and task.status <> 100 ";
            }
        }
        sql += "order by task.createDate desc limit " + (num - 1) * pageSize + ", " + pageSize;
        Pager<Task> taskPager = new Pager<>();
        List<Task> tasks;
        try {
            dbManager.getConnection();
            ResultSet res = null;
            ResultSet countRes = null;
            res = dbManager.query(sql);
            countRes = dbManager.query(countSql);
            tasks = new ArrayList<>();
            while (res.next()) {
                Task task = ObjectUtil.getObject(res, Task.class);
                UserData userData = ObjectUtil.getObject(res, UserData.class);
                task.setUser(userData);
                tasks.add(task);
            }
            if (countRes.next()) {
                taskPager.setTotal(countRes.getInt(1));
            }
            taskPager.setDatas(tasks);
            taskPager.setNum(num);
            taskPager.setPageSize(pageSize);
            dbManager.close();
        } catch (SQLException ex) {
            Logger.getLogger(TaskDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return taskPager;
    }

    public boolean addTask(Task task) {
        String sql = "insert into task(userid,status,createDate) value(?,?,?)";
        dbManager.getConnection();
        try {
            task.setId(dbManager.save(sql, task.getUserid(), task.getStatus(), task.getCreateDate()));
            dbManager.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
}