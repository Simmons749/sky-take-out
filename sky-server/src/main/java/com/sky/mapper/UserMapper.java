package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {


    /**
     * 根据openId查询用户数据
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    /**
     * 插入新数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据动态条件查询用户数据
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
