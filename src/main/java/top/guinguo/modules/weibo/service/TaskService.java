package top.guinguo.modules.weibo.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import top.guinguo.modules.weibo.dao.TaskDao;
import top.guinguo.modules.weibo.dao.UserDao;
import top.guinguo.modules.weibo.model.Task;
import top.guinguo.modules.weibo.model.TaskResult;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.UserData;
import top.guinguo.modules.weibo.task.TaskManager;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by guin_guo on 2018/3/10.
 */
@Service("taskService")
public class TaskService implements ITaskService {
    @Resource
    private TaskDao taskDao;
    @Resource
    private UserDao userDao;

    @Override
    public boolean addTask(String uid) {
        Task task = new Task();
        task.setUserid(uid);
        task.setCreateDate(new Timestamp(new Date().getTime()));
        task.setStatus("20");
        boolean result = taskDao.addTask(task);
        if (result) {
            //执行任务
            TaskManager taskManager = TaskManager.getInstance();
            taskManager.runTask(task);
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(task.getId());
            User user = userDao.getById(uid);
            taskResult.setProfile(JSON.toJSONString(user));
            task.setUser(new UserData(user));
            taskDao.addTaskResult(taskResult);
        }
        return result;
    }

    @Override
    public boolean deleteTask(int id) {
        return taskDao.deleteTask(id);
    }
}
