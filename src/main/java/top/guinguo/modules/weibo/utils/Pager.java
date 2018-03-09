package top.guinguo.modules.weibo.utils;

import java.util.List;

/**
 * 分页类
 * @author guin_uo
 * Created by guin_uo on 17-5-25.
 */
public class Pager<E> {
    /**
     * 第几页
     */
    private int num;
    /**
     * 每页显示多少条
     */
    private int pageSize;
    /**
     * 总共多少条记录
     */
    private int total;
    /**
     * 放置具体数据的列表
     */
    private List<E> datas;

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public List<E> getDatas() {
        return datas;
    }
    public void setDatas(List<E> datas) {
        this.datas = datas;
    }
}

