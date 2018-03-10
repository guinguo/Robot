package top.guinguo.modules.weibo.model;

import java.sql.Timestamp;

/**
 * 用户画像任务
 * Created by guin_guo on 2018/3/10.
 */
public class Task {
    /**
     * id
     */
    private Integer id;
    /**
     * user id
     */
    private String userid;
    /**
     * 创建日期
     */
    private Timestamp createDate;
    /**
     * 完成日期
     */
    private Timestamp finishDate;
    /**
     * 状态
     */
    private String status;
    /**
     * 用户
     */
    private UserData user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Timestamp finishDate) {
        this.finishDate = finishDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
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

        Task t = (Task) obj;

        return (this.id == t.getId()
                || (this.id != null && this.id.equals(t.getId()))
        );
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userid='" + userid + '\'' +
                ", createDate=" + createDate +
                ", status='" + status + '\'' +
                '}';
    }
}
