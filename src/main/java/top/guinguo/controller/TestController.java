package top.guinguo.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;

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
            java.io.File f = new java.io.File(realpath + File.separator + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), f);
            req.getSession().setAttribute("msg","上传成功");
            return "redirect:list";
        }
    }

    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest req){
        String realpath = req.getSession().getServletContext().getRealPath("/download");
        File downloads = new File(realpath);
        String[] files = downloads.list();
        List<String> list = Arrays.asList(files);
        list.forEach(v -> System.out.println(v));
        model.addAttribute("list", list);
        return "list";
    }

    @RequestMapping(value = "/download")
    @ResponseBody
    public String download(String name,
                           HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String realpath = req.getSession().getServletContext().getRealPath("/download");
        req.setCharacterEncoding("UTF-8");
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        String downLoadPath = realpath + java.io.File.separator + name;
        try {
            long fileLength = new java.io.File(downLoadPath).length();
            resp.setHeader("Content-disposition", "attachment; filename="
                    + new String(name.getBytes("utf-8"), "ISO8859-1"));
            resp.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(resp.getOutputStream());
            byte[] buff = new byte[1024 * 32];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return "list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(String name, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String realpath = req.getSession().getServletContext().getRealPath("download");
        String filepath = realpath + File.separator + name;
		System.out.println("delete--->"+filepath);
        File file = new File(filepath);
		System.out.println("directry--->"+file.isDirectory());
        if (file.exists()) {
            file.delete();
            return "ok";
        } else return "fail";
    }
}
