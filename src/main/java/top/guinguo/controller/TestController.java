package top.guinguo.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by guin_guo on 2016/5/30.
 */
@Controller
public class TestController {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    @RequestMapping("/file")
    public String file(@RequestParam("file")CommonsMultipartFile file,
                       HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String realpath = req.getSession().getServletContext().getRealPath("/download");
        System.out.println(realpath);
        if (file.getOriginalFilename().isEmpty()) {
            req.getSession().setAttribute("msg","文件是空的！！");
            return "error";
        } else {
            if (file.getSize() > 50 * 1024 * 1024) {
                req.getSession().setAttribute("msg","文件太大了！！");
                return "error";
            }
            realpath = realpath.replace("Robot", "ScauSky");
            log.debug(realpath+"========================");
            realpath = realpath.replace("robot", "ScauSky");
            log.debug(realpath+"========================");
            java.io.File f = new java.io.File(realpath + "/" + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), f);
            req.getSession().setAttribute("msg","上传成功");
            return "a";
        }
    }
}
