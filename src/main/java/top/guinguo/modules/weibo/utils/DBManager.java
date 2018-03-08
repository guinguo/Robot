package top.guinguo.modules.weibo.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;

/**
 * @author guin_uo
 */
public class DBManager {


    private static ComboPooledDataSource cpds=null;
    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<PreparedStatement> preparedStatementThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<ResultSet> resultSetThreadLocal = new ThreadLocal<>();

    static {
        //这里有个优点，写好配置文件，想换数据库，简单
        cpds = new ComboPooledDataSource("mysql");//这是mysql数据库
    }
    public DBManager() {
    }

    public Connection getConnection(){
        Connection conn = connectionThreadLocal.get();
        try {
            if (conn == null || conn.isClosed()) {
                conn = cpds.getConnection();
                connectionThreadLocal.set(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public ResultSet query(String sql) throws SQLException {
        PreparedStatement pstate = createStatement(sql);
        this.preparedStatementThreadLocal.set(pstate);
        ResultSet res = pstate.executeQuery();
        this.resultSetThreadLocal.set(res);
        return res;
    }

    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement pstate = createStatement(sql, params);
        this.preparedStatementThreadLocal.set(pstate);
        ResultSet res = pstate.executeQuery();
        this.resultSetThreadLocal.set(res);
        return res;
    }

    public int updateQuery(String sql, Object... params) throws SQLException {
        PreparedStatement pstate = createStatement(sql, params);
        this.preparedStatementThreadLocal.set(pstate);
        return pstate.executeUpdate();
    }

    private PreparedStatement createStatement(String sql, Object... params) throws SQLException {
        PreparedStatement pstate = getConnection().prepareStatement(sql);
        this.preparedStatementThreadLocal.set(pstate);
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            pstate.setObject(i + 1, param);
        }
        return pstate;
    }

    public int save(String sql, Object... params)throws SQLException{
        PreparedStatement pstate = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.preparedStatementThreadLocal.set(pstate);
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            pstate.setObject(i + 1, param);
        }
        pstate.executeUpdate();

        ResultSet res = pstate.getGeneratedKeys();
        this.resultSetThreadLocal.set(res);
        if(res.next()){
            return res.getInt(1);
        }
        return 0;
    }

    public void close() throws SQLException {
        ResultSet res = this.resultSetThreadLocal.get();
        if (res != null) {
            res.close();
        }
        PreparedStatement pstate = this.preparedStatementThreadLocal.get();
        if (pstate != null) {
            pstate.close();
        }
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            conn.close();
            connectionThreadLocal.remove();
        }
    }
}
