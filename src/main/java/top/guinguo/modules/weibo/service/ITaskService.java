package top.guinguo.modules.weibo.service;

/**
 * Created by guin_guo on 2018/3/10.
 */
public interface ITaskService {
    public boolean addTask(String uid);

    boolean deleteTask(int id);
}
