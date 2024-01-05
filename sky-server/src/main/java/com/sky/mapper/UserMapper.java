package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId};")
    User getByOpenId(String openId);

    void insert(User user);

    /**
     *
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    @Select("select count(*) from user where create_time between #{begin} and #{end}")
    Integer countByMap(Map map);
}
