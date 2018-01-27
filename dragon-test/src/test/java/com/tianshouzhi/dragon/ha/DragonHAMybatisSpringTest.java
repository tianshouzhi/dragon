package com.tianshouzhi.dragon.ha;

import com.tianshouzhi.dragon.common.domain.User;
import com.tianshouzhi.dragon.common.mappers.UserMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

public class DragonHAMybatisSpringTest {
	static UserMapper userMapper;

	static DataSourceTransactionManager transactionManager;

	@BeforeClass
	public static void beforeClass() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("ha/spring-dragon.xml");
		userMapper = context.getBean(UserMapper.class);
		transactionManager = context.getBean(DataSourceTransactionManager.class);
	}

	@Test
	public void testInsert() {
		User user = new User();
		user.setName("tianshozhi");
		userMapper.insert(user);
		System.out.println(user);
	}
	@Test
	public void testBatchInsert(){
		User user = new User();
		user.setName("tianshozhi");
		User user1 = new User();
		user.setName("wangxiaoxiao");
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		userList.add(user1);
		userMapper.batchInsert(userList);
		System.out.println(userList);
	}

	@Test
	public void testSelect() {
		List<User> users = userMapper.selectAll();
		System.out.println(users);
	}

	@Test
	public void testUpdate() {
		User user = new User(1, "tianshouzhi");
		int updateCount = userMapper.updateById(user);

	}

	@Test
	public void testDelete() {
		int deleteCount = userMapper.deleteAll();
		System.out.println(deleteCount);
	}

	@Test(expected = Exception.class)
	public void testTransaction() {
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition(
		      TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
		try {
			User user = new User();
			user.setName("transaction1");

			userMapper.insert(user);
			int i = 1 / 0;
			User user1 = new User();
			user.setName("transaction2");
			userMapper.insert(user1);
			transactionManager.commit(transaction);
		} catch (Exception e) {
			transactionManager.rollback(transaction);
			throw e;
		}
	}
}
