package com.tianshouzhi.dragon.mappers;

import com.tianshouzhi.dragon.domain.User;

public interface UserMapper {
    public int insert(User user);

    public User selectById(int id);

    public int updateById(User user);

    public int deleteById(int id);
}
