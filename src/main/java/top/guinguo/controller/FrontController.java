package top.guinguo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.guinguo.modules.weibo.dao.TaskDao;
import top.guinguo.modules.weibo.dao.UserDao;
import top.guinguo.modules.weibo.model.Task;
import top.guinguo.modules.weibo.model.UserData;
import top.guinguo.modules.weibo.service.TaskService;
import top.guinguo.modules.weibo.utils.Pager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by guin_guo on 2018/2/22.
 */
@Controller
public class FrontController {
    @Resource
    private UserDao userDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private TaskService taskService;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 主页
     * @param model
     * @param req
     * @return
     */
    @RequestMapping({"/", "/index", "/index.html"})
    public String index(Model model, HttpServletRequest req){
        return "views/index";
    }

    /**
     * 用户列表
     * @param num
     * @param pageSize
     * @param username
     * @return
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public Pager<UserData> users(Integer num, Integer pageSize, String username) {
        return userDao.list(num, pageSize, username);
    }

    /**
     * 任务列表
     * @param num
     * @param pageSize
     * @param status
     * @return
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    @ResponseBody
    public Pager<Task> tasks(Integer num, Integer pageSize, String status) {
        return taskDao.list(num, pageSize, status);
    }

    /**
     * 添加任务
     * @param uid
     * @return
     */
    @RequestMapping(value = "/addTasks", method = RequestMethod.POST)
    @ResponseBody
    public String addTask(String uid) {
        boolean res = taskService.addAndRunTask(uid);
        if (res) {
            return "ok";
        }
        return "fail";
    }

    /**
     * 删除任务
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
    @ResponseBody
    public String deleteTask(int id) {
        boolean res = taskService.deleteTask(id);
        if (res) {
            return "ok";
        }
        return "fail";
    }
}
