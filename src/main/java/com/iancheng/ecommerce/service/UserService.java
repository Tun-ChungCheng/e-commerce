package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.UserLoginRequest;
import com.iancheng.ecommerce.dto.UserRegisterRequest;
import com.iancheng.ecommerce.model.User;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);

    User getUserById(Integer userId);

    User login(UserLoginRequest userLoginRequest);
}
