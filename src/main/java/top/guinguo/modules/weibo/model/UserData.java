package top.guinguo.modules.weibo.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @描述: 新浪分页用户
 * @作者: guin_guo
 * @日期: 2018-02-25 11:24
 * @版本: v1.0
 */
public class UserData implements java.io.Serializable {
    /**
     * 用户id
     */
    private String id;
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
     * 认证类型 int
     * 比如微博会员5
     * member = 5
     * 不是会员 member = 0
     */
    private int member;

    public UserData() {
    }

    public UserData(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.address = user.getAddress();
        this.avatar = user.getAvatar();
        this.blogNumber = user.getBlogNumber();
        this.fans = user.getFans();
        this.focus = user.getFocus();
        this.sex = user.getSex();
        this.level = user.getLevel();
        this.member = user.getMember();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
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

        UserData u = (UserData) obj;

        return (this.id == u.getId()
                || (this.id != null && this.id.equals(u.getId()))
        );
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
