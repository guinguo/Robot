package top.guinguo.modules.weibo.model;

/**
 * 用户画像任务
 * Created by guin_guo on 2018/3/10.
 */
public class TaskResult {
    /**
     * id
     */
    private Integer id;
    /**
     * task id
     */
    private Integer taskId;
    /**
     * 用户数据
     */
    private String profile;
    /**
     * 用户数据
     */
    private String data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

        TaskResult t = (TaskResult) obj;

        return (this.id == t.getId()
                || (this.id != null && this.id.equals(t.getId()))
        );
    }

}
