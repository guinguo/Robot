package top.guinguo.modules.crawler;

/**
 * @描述: 视频类
 * @作者: guin_guo
 * @日期: 2017-04-14 10:15
 * @版本: v1.0
 */
public class Video {
    private String filename;
    private String title;
    private String url;

    public Video() {
    }

    public Video(String filename, String title, String url) {
        this.filename = filename;
        this.title = title;
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return filename + '：' + title;
    }
}
