package top.guinguo.modules.weibo.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by guin_guo on 2018/03/27.
 */
public class ResultDto {
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
    private JSONObject profile;
    /**
     * 图表数据
     */
    private JSONObject data;

    public ResultDto() {
    }

    public ResultDto(TaskResult result) {
        this.id = result.getId();
        this.taskId = result.getTaskId();
        this.profile = JSON.parseObject(result.getProfile());
        this.data = JSON.parseObject(result.getData());
    }


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

    public JSONObject getProfile() {
        return profile;
    }

    public void setProfile(JSONObject profile) {
        this.profile = profile;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
