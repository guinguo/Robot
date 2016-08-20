package top.guinguo.worker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ServletContextAware;
import top.guinguo.utils.Constant;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * Created by guin_guo on 2016/8/3.
 */
public class List2File implements ServletContextAware {

    private ServletContext servletContext;

    public void SynList2Disk() throws IOException {
        List<String> datas;
        datas = (List<String>) servletContext.getAttribute("datas");

        if (datas == null || datas.size() == 0) {
            System.out.println("--------------datas is null");
            return;
        }
        File file = new File(Constant.StoreFilePATH + File.separator + "current.txt");
        if (isCreateFile(file, datas)) {
            //current.txt not exists
            return;
        }

        //current.txt exists
        BufferedReader br = new BufferedReader(new FileReader(file));
        for (String val : datas) {
            String line = br.readLine();
            System.out.println(val + "=?=" + line);
            if (line == null || !StringUtils.equals(val, line)) {
                //if (line == null || !val.equals(line)) {
                // current.txt --> yyyy-MM-dd.txt
                File file2Disk = new File(Constant.StoreFilePATH + File.separator + getNowDate());
                file2Disk.createNewFile();
                FileUtils.copyFile(file, file2Disk);
                //datas --> current.txt
                if (line == null) {
                    br.close();
                }
                if (file.delete()) {
                    System.out.println("删除文件-->旧的current.txt");
                }
                File list2File = new File(Constant.StoreFilePATH + File.separator + "current.txt");
                isCreateFile(list2File, datas);
                break;
            }
        }
        br.close();
    }

    public static String getNowDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+".txt";
    }
    private boolean isCreateFile(File file, List<String> datas) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("创建文件"+file.getName());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String value : datas) {
                bw.write(value);
                System.out.println("write value = " + value);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            return true;
        }
        return false;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
