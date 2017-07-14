package com.tianshouzhi.dragon.ha;

import com.tianshouzhi.dragon.domain.User;
import com.tianshouzhi.dragon.mappers.UserMapper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DragonHAMybatisSpringTest {
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath:ha/dragon-ha-mybatis-spring.xml");
        UserMapper userMapper = context.getBean(UserMapper.class);
        User user = new User();
        user.setName("tianshozhi");
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
