package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.UserLoginRequest;
import com.iancheng.ecommerce.dto.UserRegisterRequest;
import com.iancheng.ecommerce.mapper.UserMapper;
import com.iancheng.ecommerce.model.User;
import com.iancheng.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public Integer register(UserRegisterRequest request) {
        // 檢查註冊的 email
        checkRegisterEmail(request);

        // 創建帳號
        User user = new User();
        user.setEmail(request.getEmail());

        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        user.setPassword(hashedPassword);

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

    @Override
    public User login(UserLoginRequest request) {
        // 檢查 user 是否存在
        checkLoginEmail(request);

        checkLoginPassword(request);

        return userMapper.getUserByEmail(request.getEmail());
    }

    private void checkRegisterEmail(UserRegisterRequest request) {
        if (userExist(request.getEmail())) {
            log.warn("該 email {} 已經被註冊", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkLoginEmail(UserLoginRequest request) {
        if (!userExist(request.getEmail())) {
            log.warn("該 email {} 尚未註冊", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkLoginPassword(UserLoginRequest request) {
        User user = userMapper.getUserByEmail(request.getEmail());
        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());

        // 比較密碼
        if (!comparePassword(hashedPassword, user.getPassword())) {
            log.warn("email {} 的密碼不正確", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean userExist(String email) {
        return userMapper.getUserByEmail(email) != null;
    }

    private boolean comparePassword(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }


}
