package com.example.dao.mapper;

import com.example.dao.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT id, user_name AS userName, email FROM user_info ORDER BY id")
    List<UserInfo> findAll();

    @Select("SELECT id, user_name AS userName, email FROM user_info WHERE id = #{id}")
    UserInfo findById(@Param("id") Long id);

    @Insert("INSERT INTO user_info(user_name, email) VALUES(#{userName}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserInfo user);

    @Update("UPDATE user_info SET user_name = #{userName}, email = #{email} WHERE id = #{id}")
    int update(UserInfo user);

    @Delete("DELETE FROM user_info WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
