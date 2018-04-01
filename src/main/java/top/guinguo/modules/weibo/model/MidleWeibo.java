package top.guinguo.modules.weibo.model;

/**
 * Created by guin_guo on 2018/03/13.
 */
public class MidleWeibo {
    private String postText;
    private String postCutText;
    private Integer commentsSize;
    private Integer forwardSize;
    private Integer likeSize;
    private String date;
    private String id;

    public MidleWeibo(String id, String postText, Integer commentsSize, String date) {
        this.id = id;
        this.setPostText(postText);
        this.commentsSize = commentsSize;
        this.date = date;
    }

    public MidleWeibo(String id, String postText, Integer forwardSize) {
        this.id = id;
        this.setPostText(postText);
        this.forwardSize = forwardSize;
    }

    public MidleWeibo(String id, Integer likeSize, String postText) {
        this.id = id;
        this.setPostText(postText);
        this.likeSize = likeSize;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
        if (postText != null && postText.length() > 0) {
            this.postCutText = postText.length() < 30 ? postText : postText.substring(0, 30) + "...";
        }
    }

    public String getPostCutText() {
        return postCutText;
    }

    public void setPostCutText(String postCutText) {
        this.postCutText = postCutText;
    }

    public Integer getCommentsSize() {
        return commentsSize;
    }

    public void setCommentsSize(Integer commentsSize) {
        this.commentsSize = commentsSize;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getForwardSize() {
        return forwardSize;
    }

    public void setForwardSize(Integer forwardSize) {
        this.forwardSize = forwardSize;
    }

    public Integer getLikeSize() {
        return likeSize;
    }

    public void setLikeSize(Integer likeSize) {
        this.likeSize = likeSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
