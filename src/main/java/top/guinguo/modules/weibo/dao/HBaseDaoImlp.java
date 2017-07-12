package top.guinguo.modules.weibo.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.guinguo.modules.weibo.utils.Configurator;
import top.guinguo.modules.weibo.utils.CrawleUtils;

import java.util.*;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-07 23:52
 * @版本: v1.0
 */
public class HBaseDaoImlp implements IHbaseDao {

    public static final String HBASE_SERVER = "hbase.server";
    public static final String HBASE_PORT = "hbase.port";
    public static final String HBASE_USER = "hbase.user";
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static Configuration configuration;
    private static Connection connection;
    private static String hbaseIp;
    private static String hbasePort;
    private static String hbaseUser;

    public HBaseDaoImlp() {
    }
    private static class DefaultInstance {
        public static HBaseDaoImlp instance = new HBaseDaoImlp();
    }
    public static HBaseDaoImlp getInstance() {
        return DefaultInstance.instance;
    }


    static {
        try {
            Configurator configurator = Configurator.getInstance();
            hbaseIp = configurator.get(HBASE_SERVER,"server2");
            hbasePort = configurator.get(HBASE_PORT,"2181");
            hbaseUser = configurator.get(HBASE_USER,"hbase");

            configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", hbaseIp);
            configuration.set("hbase.zookeeper.property.clientPort", hbasePort);
            User user = User
                    .create(UserGroupInformation
                            .createRemoteUser(hbaseUser));
            connection = ConnectionFactory
                    .createConnection(configuration, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Table table = connection.getTable(TableName.valueOf("weibo"));
        /*Put put = new Put("5652557385".getBytes());
        put.addColumn("basic".getBytes(), "username".getBytes(), "托马斯穆勒 Thomas Muller".getBytes());*/
        List<Put> list = new ArrayList<Put>();
        for (int i = 0; i < 10; i++) {
            Put put = new Put(("565255738" + i).getBytes());
            if (i == 5) {
                put.addColumn("basic".getBytes(), "username".getBytes(), "托马斯穆勒 Thomas Muller".getBytes());
            } else {
                put.addColumn("basic".getBytes(), "username".getBytes(), ("user" + i).getBytes());
            }
            put.addColumn("basic".getBytes(), "age".getBytes(), (i + 20 + "").getBytes());
            list.add(put);
        }
//        table.put(list);
        HBaseDaoImlp hBaseDaoImlp = new HBaseDaoImlp();
        hBaseDaoImlp.queryAll("weibo");
//        hBaseDaoImlp.queryByRowKey("user","5652557385");
    }

    @Override
    public List<Map<String, Object>> queryAll(String tableName) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        TableName tn = TableName.valueOf(tableName);
        Table table = connection.getTable(tn);
        ResultScanner rs = table.getScanner(new Scan());
        for (Result r : rs) {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            for (Cell cell : r.listCells()) {
                CrawleUtils.dealCell(resultMap, cell);
            }
            System.out.println();
            resultList.add(resultMap);
        }
        rs.close();
        table.close();
        return resultList;
    }

    @Override
    public Map<String, Object> queryByRowKey(String tableName, String rowKey) throws Exception {
        Map<String, Object> resultMap = null;
        TableName tn = TableName.valueOf(tableName);
        Table table = connection.getTable(tn);
        Get scan = new Get(rowKey.getBytes());//get by rowkey
        Result r = table.get(scan);
        if (r.isEmpty()) {
            return resultMap;
        }
        resultMap = new LinkedHashMap<>();
        for (Cell cell : r.listCells()) {
            CrawleUtils.dealCell(resultMap, cell);
        }
        table.close();
        return resultMap;
    }

    @Override
    public void insert(String tableName, Put put) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        table.put(put);
        table.close();
    }

    @Override
    public void batchInsert(String tableName, List<Put> puts) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        table.put(puts);
        table.close();
    }
}
