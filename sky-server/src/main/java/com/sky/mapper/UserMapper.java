package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
