package cn.itcast.travel.util;


import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * druid连接池工具
 * 江祖昌
 */
public class UtilsToDruid {
    static Properties pro;
    static InputStream ips;
    static DataSource ds;

    static {
        try {
            pro = new Properties();
            ips = UtilsToDruid.class.getClassLoader().getResourceAsStream("druid.properties");
            pro.load(ips);
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return ds;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close(InputStream ips, Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ips != null) {
            try {
                ips.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}