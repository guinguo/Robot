package top.guinguo.modules.weibo.service;

import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.dao.IHbaseDao;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.modules.weibo.utils.Contants;
import top.guinguo.modules.weibo.utils.HBaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-08 21:16
 * @版本: v1.0
 */
public class WeiboService implements IWeiboService {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private IHbaseDao hbaseDao;

    public WeiboService() {
        hbaseDao = HBaseDaoImlp.getInstance();
    }
    private static class DefaultInstance {
        public static WeiboService instance = new WeiboService();
    }
    public static WeiboService getInstance() {
        return DefaultInstance.instance;
    }
    @Override
    public List<Map<String, Object>> queryAll(String tableName) throws Exception {
        log.info("[queryAll][tableName][" + tableName + "]");
        return hbaseDao.queryAll(tableName,false);
    }

    @Override
    public Map<String, Object> queryByRowKey(String tableName, String rowKey) throws Exception {
        log.info("[queryByRowKey][tableName][" + tableName + "][rowKey][" + rowKey + "]");
        return hbaseDao.queryByRowKey(tableName, rowKey);
    }

    @Override
    public void addWeibo(Weibo weibo) throws Exception {
        log.info("[addWeibo][weiboid][" + weibo.getId() + "]");
        Put put = HBaseUtils.GeneratePutByWeibo(weibo);
        hbaseDao.insert(Contants.T_WEIBO, put);
    }

    @Override
    public void addUser(User user) throws Exception {
        log.info("[addUser][userid][" + user.getId() + "]");
        Put put = HBaseUtils.GeneratePutByUser(user);
        hbaseDao.insert(Contants.T_USER, put);
    }

    @Override
    public void batchAddWeibo(List<Weibo> weibos) throws Exception {
        log.info("[batchAddWeibo][size][" + weibos.size() + "]");
        List<Put>  list = HBaseUtils.GeneratePutSByWeibo(weibos);
        hbaseDao.batchInsert(Contants.T_WEIBO, list);
    }

    @Override
    public List<Weibo> getWeiboByUid(String uid) throws Exception {
        List<Map<String, Object>> weiboMaps = hbaseDao.scaneByPrefixFilter(Contants.T_WEIBO, "6358542917", false);
        List<Weibo> list = new ArrayList<>(weiboMaps.size());
        for (Map<String, Object> w : weiboMaps) {
            Weibo weibo = new Weibo();
            weibo.setId(w.get("rowkey").toString().substring(11));
            weibo.setCommentNumber(Long.valueOf(w.get("commentNumber").toString()));
            weibo.setForwardNumber(Long.valueOf(w.get("forwardNumber").toString()));
            weibo.setLikeNumber(Long.valueOf(w.get("likeNumber").toString()));
            weibo.setContent(w.get("content").toString());
            weibo.setCreateDate(w.get("createDate").toString());
            weibo.setForward("true".equals(w.get("forward")));
            weibo.setNickname(w.get("nickname").toString());
            if (w.get("picids") != null) {
                weibo.setPicids(w.get("picids").toString());
            }
            if (w.get("source") != null) {
                weibo.setSource(w.get("source").toString());
            }
            weibo.setTopics(w.get("topics").toString());
            weibo.setUid(w.get("uid").toString());
            list.add(weibo);
        }
        return list;
    }
}
