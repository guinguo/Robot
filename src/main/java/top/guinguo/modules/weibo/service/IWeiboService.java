package top.guinguo.modules.weibo.service;

import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;

import java.util.List;
import java.util.Map;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-08 21:14
 * @版本: v1.0
 */
public interface IWeiboService {

    List<Map<String, Object>> queryAll(String tableName) throws Exception;

    Map<String, Object> queryByRowKey(String tableName, String rowKey) throws Exception;

    void addWeibo(Weibo weibo) throws Exception;

    void addUser(User user) throws Exception;

    void batchAddWeibo(List<Weibo> weibos) throws Exception;
}
