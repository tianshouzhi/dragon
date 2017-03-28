package com.tianshouzhi.dragon.demo;

import com.tianshouzhi.dragon.demo.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
public class DragonSpringMybatisTest {
    private static   SqlSession sqlSession=null;
    @BeforeClass
    public static void beforeClass(){
        ApplicationContext context=new ClassPathXmlApplicationContext("dragon-spring.xml");
        SqlSessionFactory sqlSessionFactory= (SqlSessionFactory) context.getBean("sqlSessionFactory");
        sqlSession=sqlSessionFactory.openSession();
    }
    @Test
    public void testDeleteAll(){
        int deleteNums = sqlSession.delete("com.tianshouzhi.mybatis.dao.User.deleteAll");
        System.out.println(deleteNums);
    }

    @Test
    public void testBatchDelete(){
        int[] deleteIds=new int[]{10001,10000,10101};
        int deleteNums = sqlSession.delete("com.tianshouzhi.mybatis.dao.User.batchDelete",deleteIds);
        System.out.println(deleteNums);
    }

    @Test
    public void testInsert(){
        User user=new User();
        user.setId(30001);
        user.setName("tianshouzhi");
        int num=sqlSession.insert("com.tianshouzhi.mybatis.dao.User.insert",user);
        System.out.println(num);
    }
    @Test
    public void testBatchInsert(){
        List<User> userList=new ArrayList<User>();
        userList.add(new User(10000,"tianhui"));
        userList.add(new User(20000,"tainmin"));
        userList.add(new User(10001,"tianhui"));
        userList.add(new User(20001,"tainmin"));
        userList.add(new User(10100,"tianhui"));
        userList.add(new User(20100,"tainmin"));
        userList.add(new User(10101,"tianhui"));
        userList.add(new User(20101,"tainmin"));
        int insertNum= sqlSession.insert("com.tianshouzhi.mybatis.dao.User.batchInsert",userList);
        System.out.println(insertNum);
    }

    @Test
    public void testSelectById(){
        User user=sqlSession.selectOne("com.tianshouzhi.mybatis.dao.User.selectById",10001);
        System.out.println(user);
    }
    @Test
    public void testSelectAll(){
        List<User> userList=sqlSession.selectList("com.tianshouzhi.mybatis.dao.User.selectAll");
        printList(userList);
    }

    @Test
    public void testSelectOrderByLimit(){
        HashMap<String, Integer> params = new HashMap<String, Integer>();
        params.put("offset",0);
        params.put("rows",10);
        List<User> userList=sqlSession.selectList("com.tianshouzhi.mybatis.dao.User.selectOrderByLimit",params);
        printList(userList);
    }

    @Test
    public void testAggrGroupBy(){
        List<Map<String,Object>> result=sqlSession.selectList("com.tianshouzhi.mybatis.dao.User.selectAggrGroupBy");
        printList(result);
    }

    @Test
    public void testSelectWhereIdIn(){
        int[] selectIds=new int[]{10001,10000,10101};
        List<User> userList=sqlSession.selectList("com.tianshouzhi.mybatis.dao.User.selectWhereIdIn",selectIds);
        printList(userList);
    }

    @Test
    public void testUpdateById(){
        User user=sqlSession.selectOne("com.tianshouzhi.mybatis.dao.User.selectById",10001);
        System.out.println(user);
        user.setName("wangxiaoxiao");
        int num=sqlSession.update("com.tianshouzhi.mybatis.dao.User.updateById",user);
        System.out.println(num);
    }
    @Test
    public void testDeleteById(){
        int num=sqlSession.delete("com.tianshouzhi.mybatis.dao.User.deleteById",10001);
        System.out.println(num);
    }

    public void printList(List<? extends Object> userList) {
        for (Object obj : userList) {
            System.out.println(obj);
        }
    }
}
