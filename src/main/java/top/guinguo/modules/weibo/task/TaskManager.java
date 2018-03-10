package top.guinguo.modules.weibo.task;

import top.guinguo.modules.weibo.model.Task;

/**
 * Created by guin_guo on 2018/3/10.
 */
public class TaskManager {

    public TaskManager() {
    }
    private static class DefaultInstance {
        public static TaskManager instance = new TaskManager();
    }
    public static TaskManager getInstance() {
        return DefaultInstance.instance;
    }

    public void runTask(Task task) {
        System.out.println("running task: " + task);
    }
}
