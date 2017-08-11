package com.tianshouzhi.dragon.mappers;

import com.tianshouzhi.dragon.domain.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import sun.jvm.hotspot.debugger.Page;

public interface UserMapper {
    public int insert(User user);

    public User selectById(int id);

    public int updateById(User user);

    public int deleteById(int id);
    
    public int batchInsert(List<User> list);
    
    public User selectById(@Param("id")Integer id);

    public List<User> selectWhereIdIn(@Param("array") Integer[] ids);
    
    public List<User> selectAll();
    
    public List<User> selectOrderByLimit(Map<String, Integer> params);
    
    public HashMap selectAggrGroupBy();
    
    public List<User> selectInnerJoinAccount();
    
    public List<User> selectLeftJoinAccount();

    public  int updateCaseWhen(@Param("list") List<User> users);
    
    public int deleteAll();
    
    public int batchDelete(Integer[] ids);

}
