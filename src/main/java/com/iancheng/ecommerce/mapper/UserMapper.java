package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createUser(User user);

    User getUserById(Integer userId);

    User getUserByEmail(String email);
}
