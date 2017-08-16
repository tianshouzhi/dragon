package com.tianshouzhi.dragon.ha;

import com.tianshouzhi.dragon.domain.User;
import com.tianshouzhi.dragon.mappers.UserMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class DragonHAMybatisSpringTest {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath:ha/dragon-ha-mybatis-spring.xml");
        UserMapper userMapper = context.getBean(UserMapper.class);
        User user = new User();
        user.setName("tianshozhi");
        User user1 = new User();
        user.setName("wangxiaoxiao");
        List<User> userList=new ArrayList<User>();
        userList.add(user);
        userList.add(user1);
        userMapper.batchInsert(userList);

        int insertCount = userMapper.insert(user);
        assert insertCount==1;

        Integer id = user.getId();
        user = userMapper.selectById(id);
        System.out.println(user);

        user.setName("wangxiaoxiao");
        int updateCount = userMapper.updateById(user);
        assert updateCount==1;

        int deleteCount = userMapper.deleteById(user.getId());
        assert deleteCount==1;
    }

}
