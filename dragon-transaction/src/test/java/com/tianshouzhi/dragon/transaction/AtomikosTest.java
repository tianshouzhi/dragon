package com.tianshouzhi.dragon.transaction;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.XAConnection;
import javax.transaction.*;
import javax.transaction.xa.XAException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/10/12.
 */
public class AtomikosTest {
    private DruidXADataSource dataSource1;

    private DruidXADataSource dataSource2;

    @Before
    public void before() {
        dataSource1 = new DruidXADataSource();
        dataSource1.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource1.setUsername("root");
        dataSource1.setPassword("shxx12151022");

        dataSource2 = new DruidXADataSource();
        dataSource2.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource2.setUsername("root");
        dataSource2.setPassword("shxx12151022");
    }

    @Test
    public void test() throws SQLException, XAException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
//        UserTransaction userTransaction=new UserTransactionImp();
//        userTransaction.begin();
        XAConnection xaConnection1 = dataSource1.getXAConnection();
        XAConnection xaConnection2 = dataSource2.getXAConnection();

        PreparedStatement preparedStatement1 = xaConnection1.getConnection()
                .prepareStatement("INSERT INTO t VALUES" + "(5,5)");
        preparedStatement1.executeUpdate();

        PreparedStatement preparedStatement2 = xaConnection2.getConnection()
                .prepareStatement("INSERT INTO t VALUES" + "(4,4)");
        preparedStatement2.executeUpdate();
//        userTransaction.commit();
    }
}
