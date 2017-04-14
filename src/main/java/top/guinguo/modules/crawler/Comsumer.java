package top.guinguo.modules.crawler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * @描述: 消费者，从生产队列取出视频 下载
 * @作者: guin_guo
 * @日期: 2017-04-14 10:53
 * @版本: v1.0
 */
public class Comsumer implements Runnable {

    public static final String DIR = "d:/videos/";

    private BlockingQueue<Video> queue;

    public Comsumer(BlockingQueue<Video> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            Video video;
            while ((video = queue.take()) != null) {
                System.out.println(video + "下载结果：" + downloadVideo(video));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static String downloadVideo(Video video) {
        String  url = video.getUrl(),
                filename = video.toString()+".flv";
        try {
            URL httpurl = new URL(url);
            String fileName = filename;
            File f = new File(Comsumer.DIR + fileName);
            System.out.println("消费线程[" + Thread.currentThread() + "] 下载：" + video + " url:" + url);
            FileUtils.copyURLToFile(httpurl, f);
        } catch (Exception e) {
            e.printStackTrace();
            return "Fault!";
        }
        return "Successful!";
    }
}
