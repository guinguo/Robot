package top.guinguo.modules.weibo.service;

import top.guinguo.modules.weibo.model.Task;
import top.guinguo.modules.weibo.model.TaskResult;

/**
 * Created by guin_guo on 2018/3/10.
 */
public interface ITaskService {
    public boolean addAndRunTask(String uid);
    TaskResult getResultByTaskId(int taskId);
    boolean deleteTask(int id);
    int updateTask(Task task);
    int updateTaskResult(String data, int taskId);
}
