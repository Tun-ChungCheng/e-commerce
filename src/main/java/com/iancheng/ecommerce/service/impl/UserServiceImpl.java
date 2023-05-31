package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.UserRegisterRequest;
import com.iancheng.ecommerce.mapper.UserMapper;
import com.iancheng.ecommerce.model.User;
import com.iancheng.ecommerce.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public Integer register(UserRegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        Date now = new Date();
        user.setCreatedDate(now);
        user.setLastModifiedDate(now);

        userMapper.createUser(user);

        return user.getUserId();
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }
}
