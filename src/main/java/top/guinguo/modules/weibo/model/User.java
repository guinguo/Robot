package top.guinguo.modules.weibo.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @描述: 新浪用户
 * @作者: guin_guo
 * @日期: 2017-06-25 11:24
 * @版本: v1.0
 */
public class User implements java.io.Serializable {
    /**
     * 用户id
     */
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称，微博名
     */
    private String nickname;
    /**
     * 性别，
     * male：男
     * female：女
     */
    private String sex;
    /**
     * 头像
     * 可以考虑存字符串数据
     */
    private String avatar;
    /**
     * 简介
     */
    private String intro;
    /**
     * 关注人数
     */
    private Integer focus;
    /**
     * 粉丝数
     */
    private Long fans;
    /**
     * 所发微博数
     */
    private Long blogNumber;
    /**
     * 微博等级
     */
    private Integer level;
    /**
     * 地址
     */
    private String address;
    /**
     * 生日
     */
    private Date birthDate;
    /**
     * 抓取日期
     */
    private Date crawlDate;
    /**
     * 注册日期
     */
    private String registedDate;
    /**
     * 标签  空格分割
     */
    private String tags;
    /**
     * 认证类型 int
     * 比如微博会员5
     * member = 5
     * 不是会员 member = 0
     */
    private int member;
    /**
     * 学校
     */
    private String school;
    /**
     * 公司
     */
    private String company;
    /**
     * 预留字段，json格式
     */
    private String meta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getFocus() {
        return focus;
    }

    public void setFocus(Integer focus) {
        this.focus = focus;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public Long getBlogNumber() {
        return blogNumber;
    }

    public void setBlogNumber(Long blogNumber) {
        this.blogNumber = blogNumber;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(Date crawlDate) {
        this.crawlDate = crawlDate;
    }

    public String getRegistedDate() {
        return registedDate;
    }

    public void setRegistedDate(String registedDate) {
        this.registedDate = registedDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

        if (obj == null || !this.getClass().equals(obj.getClass()))
            return false;

        User u = (User) obj;

        return (this.id == u.getId()
                || (this.id != null && this.id.equals(u.getId()))
        );
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", intro='" + intro + '\'' +
                '}';
    }
}
