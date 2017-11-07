package com.tianshouzhi.dragon.shard.sequence;

import com.tianshouzhi.dragon.common.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
public class SequenceShardMybatisTest {
    private static   SqlSession sqlSession=null;
    @BeforeClass
    public static void beforeClass(){
        ApplicationContext context = new ClassPathXmlApplicationContext("shard/sequence/sequence-sharding-spring.xml");
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) context.getBean("sqlSessionFactory");
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void testInsert(){
        User user=new User();
        user.setId(10);
        user.setName("tianshouzhi");
        int num = sqlSession.insert("com.tianshouzhi.dragon.mappers.UserMapper.insert", user);
        System.out.println(num);
    }

    @Test
    public void testBatchInsert(){
        List<User> userList=new ArrayList<User>();
        userList.add(new User(2,"tianhui2"));
        userList.add(new User(3,"tainmin3"));
        userList.add(new User(4,"tianhui4"));
        userList.add(new User(5,"tainmin5"));
        userList.add(new User(6,"tianhui6"));
        userList.add(new User(7,"tainmin7"));
        userList.add(new User(8,"tianhui8"));
        userList.add(new User(9,"tainmin9"));
        int insertNum = sqlSession.insert("com.tianshouzhi.dragon.mappers.UserMapper.batchInsert", userList);
        System.out.println(insertNum);
    }


    @Test
    public void testSelectById(){
        User user = sqlSession.selectOne("com.tianshouzhi.dragon.mappers.UserMapper.selectById", 2);
        System.out.println(user);
    }
    @Test
    public void testSelectWhereIdIn(){
        int[] selectIds=new int[]{1,2,3};
        List<User> userList = sqlSession.selectList("com.tianshouzhi.dragon.mappers.UserMapper.selectWhereIdIn", selectIds);
        printList(userList);
    }
    @Test
    public void testSelectAll(){
        List<User> userList = sqlSession.selectList("com.tianshouzhi.dragon.mappers.UserMapper.selectAll");
        printList(userList);
    }
    @Test
    public void testSelectOrderByLimit(){
        HashMap<String, Integer> params = new HashMap<String, Integer>();
        params.put("offset",0);
        params.put("rows",5);
        List<User> userList = sqlSession.selectList("com.tianshouzhi.dragon.mappers.UserMapper.selectOrderByLimit", params);
        printList(userList);
    }
    @Test
    public void testAggrGroupBy(){
        List<Map<String, Object>> result = sqlSession.selectList("com.tianshouzhi.dragon.mappers.UserMapper.selectAggrGroupBy");
        printList(result);
    }

    @Test
    public void testDeleteById(){
        int num = sqlSession.delete("com.tianshouzhi.dragon.mappers.UserMapper.deleteById", 2);
        System.out.println(num);
    }
    @Test
    public void testDeleteAll(){
        int deleteNums = sqlSession.delete("com.tianshouzhi.dragon.mappers.UserMapper.deleteAll");
        System.out.println(deleteNums);
    }

    @Test
    public void testBatchDelete(){
        int[] deleteIds = new int[] {1, 2, 3};
        int deleteNums = sqlSession.delete("com.tianshouzhi.dragon.mappers.UserMapper.batchDelete", deleteIds);
        System.out.println(deleteNums);
    }

    @Test
    public void testUpdateById(){
        User user = sqlSession.selectOne("com.tianshouzhi.dragon.mappers.UserMapper.selectById", 4);
        System.out.println(user);
        user.setName("wangxiao xiao");
        int num = sqlSession.update("com.tianshouzhi.dragon.mappers.UserMapper.updateById", user);
        System.out.println(num);
    }
    @Test
    public void testUpdateCaseWhen() throws SQLException {
        List<User> userList=new ArrayList<User>();
        userList.add(new User(1,"case when"));
        userList.add(new User(2,"case when"));
        userList.add(new User(3,"case when"));
        userList.add(new User(7,"case when"));
        int num = sqlSession.update("com.tianshouzhi.dragon.mappers.UserMapper.updateCaseWhen", userList);
        System.out.println(num);
    }

    public void printList(List<? extends Object> userList) {
        for (Object obj : userList) {
            System.out.println(obj);
        }
    }
}
