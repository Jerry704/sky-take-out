package com.sky.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 靠openid查用戶
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入數據
     * @param user
     */
    void insert(User user);
}
