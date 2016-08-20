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

        if (datas == null) {
            System.out.println("--------------datas is null");
            return;
        }
        File file = new File(Constant.StoreFilePATH + File.separator + "current.txt");
        createFile(file, datas);

        BufferedReader br = new BufferedReader(new FileReader(file));
        for (String val : datas) {
            String line = br.readLine();
            System.out.println("compare" + val + "==?" + line);
            if (!StringUtils.equals(val, line)) {
                File file2Disk = new File(Constant.StoreFilePATH + File.separator + getNowDate());
                file2Disk.createNewFile();
                FileUtils.copyFile(file, file2Disk);
                file.delete();
                File list2File = new File(Constant.StoreFilePATH + File.separator + "current.txt");
                createFile(list2File, datas);
                break;
            }
        }
        br.close();
    }

    public static String getNowDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+".txt";
    }
    private void createFile(File file, List<String> datas) throws IOException {
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
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
