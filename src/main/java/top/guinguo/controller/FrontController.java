package top.guinguo.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import top.guinguo.modules.weibo.dao.UserDao;
import top.guinguo.modules.weibo.model.UserData;
import top.guinguo.modules.weibo.utils.Pager;
import top.guinguo.utils.Constant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guin_guo on 2018/2/22.
 */
@Controller
public class FrontController {
    @Resource
    private UserDao userDao;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping({"/", "/index", "/index.html"})
    public String index(Model model, HttpServletRequest req){
        return "views/index";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public Pager<UserData> users(Integer num, Integer pageSize, String username) {
        return userDao.list(num, pageSize, username);
    }
}
