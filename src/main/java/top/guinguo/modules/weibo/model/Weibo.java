package top.guinguo.modules.weibo.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-06-25 15:00
 * @版本: v1.0
 */
public class Weibo implements java.io.Serializable {
    /**
     * 微博id ：mid
     */
    private String id;
    /**
     * 发布者id ：ouid
     */
    private String uid;
    /**
     * 是否是转发微博
     */
    private Boolean forward;
    /**
     * 转发的原微博id
     */
    private String forwardmid;
    /**
     * 原微博的发布者
     */
    private String forwarduid;
    /**
     * 发布者的微博名
     */
    private String nickname;
    /**
     * 发布时间
     */
    private Date createDate;
    /**
     * 发布微博的app来源
     * Android，ios，其他手机型号等
     */
    private String source;
    /**
     * 微博内容
     */
    private String content;
    /**
     * 微博内容包含的话题 空格分隔
     * #奥运会# #篮球# 等
     */
    private String topics;
    /**
     *  多媒体地址，视频等
     */
    private String video_src;
    /**
     * 转发数
     */
    private Long forwardNumber;
    /**
     * 评论数
     */
    private Long commentNumber;
    /**
     * 赞 数
     */
    private Long likeNumber;
    /**
     * 评论地址
     */
    private String commentUrl;
    /**
     * 转发地址
     */
    private String forwardUrl;
    /**
     * 图片ids
     */
    private String picids;
    /**
     * 预留字段 json
     */
    private String meta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getForward() {
        return forward;
    }

    public void setForward(Boolean forward) {
        this.forward = forward;
    }

    public String getForwardmid() {
        return forwardmid;
    }

    public void setForwardmid(String forwardmid) {
        this.forwardmid = forwardmid;
    }

    public String getForwarduid() {
        return forwarduid;
    }

    public void setForwarduid(String forwarduid) {
        this.forwarduid = forwarduid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getVideo_src() {
        return video_src;
    }

    public void setVideo_src(String video_src) {
        this.video_src = video_src;
    }

    public Long getForwardNumber() {
        return forwardNumber;
    }

    public void setForwardNumber(Long forwardNumber) {
        this.forwardNumber = forwardNumber;
    }

    public Long getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(Long commentNumber) {
        this.commentNumber = commentNumber;
    }

    public Long getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(Long likeNumber) {
        this.likeNumber = likeNumber;
    }

    public String getCommentUrl() {
        return commentUrl;
    }

    public void setCommentUrl(String commentUrl) {
        this.commentUrl = commentUrl;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public String getPicids() {
        return picids;
    }

    public void setPicids(String picids) {
        this.picids = picids;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public JSONObject getJSONMeta() {
        return JSONObject.parseObject(meta);
    }

    public void setJSONMeta(JSONObject meta) {
        this.meta = meta.toJSONString();
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (this.id != null) {
            hash = hash * 31 + this.id.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || (!this.getClass().equals(obj.getClass())))
            return false;

        Weibo w = (Weibo) obj;

        return (this.id == w.getId()
                || (this.id != null && this.id.equals(w.getId()))
        );
    }

    @Override
    public String toString() {
        return "Weibo{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
