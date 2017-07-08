package top.guinguo.modules.weibo.dao;

import org.apache.hadoop.hbase.client.Put;

import java.util.List;
import java.util.Map;

/**
 * @描述: 操作hbase的方法接口
 * @作者: guin_guo
 * @日期: 2017-07-07 23:52
 * @版本: v1.0
 */
public interface HBaseDao {

    /**
     * 查询某张表下所有数据 (方法慎用)
     *
     * @param tableName
     */
    List<Map<String, Object>> queryAll(String tableName) throws Exception;

    /**
     * 根据rowkey查询唯一一条记录
     *
     * @param tableName 表名
     * @param rowKey    行主键
     */
    Map<String, Object> queryByRowKey(String tableName, String rowKey) throws Exception;

    /**
     * 批量添加数据
     *
     * @param tableName 表名
     * @param puts      数据
     */
    void batchPut(String tableName, List<Put> puts) throws Exception;
}
