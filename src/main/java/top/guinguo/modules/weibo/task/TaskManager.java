package top.guinguo.modules.weibo.task;

import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.model.Task;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.WeiboService;
import top.guinguo.modules.weibo.utils.Contants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guin_guo on 2018/3/10.
 */
public class TaskManager {
    private int initPoolSize = 12;
    private ExecutorService fixedThreadPool;
    private HBaseDaoImlp hBaseDaoImlp;
    private IWeiboService weiboService;

    public TaskManager(int initPoolSize) {
        this.initPoolSize = initPoolSize;
        this.hBaseDaoImlp = HBaseDaoImlp.getInstance();
        this.weiboService= WeiboService.getInstance();
        fixedThreadPool = Executors.newFixedThreadPool(initPoolSize);
    }
    private static class DefaultInstance {
        public static TaskManager instance = new TaskManager(12);
    }
    public static TaskManager getInstance() {
        return DefaultInstance.instance;
    }

    public void runTask(Task task) {
        System.out.println("running task: " + task);

    }

    class TaskRunner implements Runnable {
        private Task task;
        public TaskRunner(Task task) {
            this.task = task;
        }
        @Override
        public void run() {
            List<Weibo> weibos;
            try {
                weibos = weiboService.getWeiboByUid(task.getUserid());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
