package com.tianshouzhi.dragon.transaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/12.
 */
public class AtomikosExample {

    private static AtomikosDataSourceBean createAtomikosDataSourceBean(String dbName) {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName(dbName);
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        Properties p = new Properties();
        p.setProperty("url", "jdbc:mysql://localhost:3306/"+dbName);
        p.setProperty("username", "root");
        p.setProperty("password", "");
        ds.setXaProperties(p);
        ds.setPoolSize(5);
        return ds;
    }

    public static void main(String[] args) {
        UserTransaction userTransaction=new UserTransactionImp();
        AtomikosDataSourceBean ds1 = createAtomikosDataSourceBean("db1");
        AtomikosDataSourceBean ds2 = createAtomikosDataSourceBean("db2");
        Connection conn1 = null;
        Connection conn2 = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try{
            userTransaction.begin();

            conn1 = ds1.getConnection();
            ps1= conn1.prepareStatement("INSERT into user(name,age) VALUES ('tianshouzhi',23)");
            ps1.executeUpdate();
// int i=1/0; //模拟异常 ，直接进入catch代码块，2个都不会提交
            conn2 = ds2.getConnection();
            ps2 = conn2.prepareStatement("INSERT into user(name,age) VALUES ('tianshouzhi',23)");
            ps2.executeUpdate();

// int i=1/0; //模拟异常 ，commit方法没有执行，2个依然都不会提交
            userTransaction.commit();
        }catch (Exception e){
            try {
                userTransaction.rollback();
            } catch (SystemException ignore) {
            }
        }finally {
            try{
                ps1.close();
                ps2.close();
                conn1.close();
                conn2.close();
                ds1.close();
                ds2.close();
            }catch (Exception ignore){}
        }
    }
}