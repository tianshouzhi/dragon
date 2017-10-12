package com.tianshouzhi.dragon.transaction;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/9/30.
 */
public class DruidXATransactionTest {
	private DruidXADataSource dataSource1;

	private DruidXADataSource dataSource2;

	@Before
	public void before() {
		dataSource1 = new DruidXADataSource();
		dataSource1.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource1.setUsername("root");
		dataSource1.setPassword("shxx12151022");

		dataSource2 = new DruidXADataSource();
		dataSource2.setUrl("jdbc:mysql://115.28.171.77:3306/test");
		dataSource2.setUsername("root");
		dataSource2.setPassword("shxx12151022");
	}

	@Test
	public void test() throws SQLException, XAException {
		XAConnection xaConnection1 = dataSource1.getXAConnection();
		XAConnection xaConnection2 = dataSource2.getXAConnection();
		XAResource xaResource1 = xaConnection1.getXAResource();
		byte[] gtrid = null;
		byte[] bqual = null;
		int o = 0;
		Xid xid = new MyXid(100, new byte[] { 0x01 }, new byte[] { 0x02 });
		xaResource1.start(xid, XAResource.TMNOFLAGS);
		PreparedStatement preparedStatement1 = xaConnection1.getConnection()
		      .prepareStatement("INSERT INTO t VALUES" + "(5,5)");
		preparedStatement1.executeUpdate();
		xaResource1.end(xid, XAResource.TMSUCCESS);
		int prepare1 = xaResource1.prepare(xid);
		//
		XAResource xaResource2 = xaConnection2.getXAResource();
		xaResource2.start(xid, XAResource.TMNOFLAGS);
		PreparedStatement preparedStatement2 = xaConnection1.getConnection()
				.prepareStatement("INSERT INTO t VALUES" + "(4,4)");
		preparedStatement2.executeUpdate();
		xaResource2.end(xid, XAResource.TMSUCCESS);
		int prepare2 = xaResource2.prepare(xid);
		
		if (prepare1 == XAResource.XA_OK) {
			xaResource1.commit(xid, false);
		}

		if (prepare2 == XAResource.XA_OK) {
			xaResource2.commit(xid, false);
		}
	}
}
