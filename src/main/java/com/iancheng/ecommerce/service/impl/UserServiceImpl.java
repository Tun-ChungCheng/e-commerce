package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.constant.Role;
import com.iancheng.ecommerce.constant.TokenType;
import com.iancheng.ecommerce.dto.UserLoginRequest;
import com.iancheng.ecommerce.dto.UserRegisterRequest;
import com.iancheng.ecommerce.mapper.TokenMapper;
import com.iancheng.ecommerce.mapper.UserMapper;
import com.iancheng.ecommerce.model.Token;
import com.iancheng.ecommerce.model.User;
import com.iancheng.ecommerce.service.JwtService;
import com.iancheng.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, LogoutHandler {
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private final TokenMapper tokenMapper;
    private final JwtService jwtService;

    public UserServiceImpl(UserMapper userMapper, TokenMapper tokenMapper, JwtService jwtService) {
        this.userMapper = userMapper;
        this.tokenMapper = tokenMapper;
        this.jwtService = jwtService;
    }


    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        // 檢查註冊的 email
        checkRegisterEmail(userRegisterRequest);

        // 創建帳號
        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());

        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        user.setPassword(hashedPassword);

        Date now = new Date();
        user.setCreatedDate(now);
        user.setLastModifiedDate(now);
        user.setRole(Role.MEMBER);

        userMapper.createUser(user);

        return user.getUserId();
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        // 檢查 user 是否存在
        checkLoginEmail(userLoginRequest);

        // 檢查 password 是否正確
        checkLoginPassword(userLoginRequest);

        User user = userMapper.getUserByEmail(userLoginRequest.getEmail());

        revokeTokens(user);

        // 產生 JWT
        String jwt = jwtService.generateToken(user);
        Token token = new Token();
        token.setUserId(user.getUserId());
        token.setJwt(jwt);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(Boolean.FALSE);
        token.setRevoked(Boolean.FALSE);
        tokenMapper.createToken(token);

        user.setToken(jwt);

        return user;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        Token token = tokenMapper.getTokenByJwt(jwt);

        if (token != null) {
            token.setExpired(Boolean.TRUE);
            token.setRevoked(Boolean.TRUE);
            tokenMapper.updateToken(token);
        }
    }

    private void revokeTokens(User user) {
        List<Token> validTokens = tokenMapper.getValidTokensByUserId(user.getUserId());

        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setExpired(Boolean.TRUE);
            token.setRevoked(Boolean.TRUE);
        });

        tokenMapper.updateTokens(validTokens);
    }

    private void checkRegisterEmail(UserRegisterRequest userRegisterRequest) {
        if (userExist(userRegisterRequest.getEmail())) {
            log.warn("該 email {} 已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkLoginEmail(UserLoginRequest userLoginRequest) {
        if (!userExist(userLoginRequest.getEmail())) {
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkLoginPassword(UserLoginRequest userLoginRequest) {
        User user = userMapper.getUserByEmail(userLoginRequest.getEmail());
        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        // 比較密碼
        if (!comparePassword(hashedPassword, user.getPassword())) {
            log.warn("email {} 的密碼不正確", userLoginRequest.getEmail());
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
