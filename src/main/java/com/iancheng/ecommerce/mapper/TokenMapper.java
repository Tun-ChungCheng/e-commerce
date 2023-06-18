package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Token;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TokenMapper {

    List<Token> getValidTokensByUserId(Integer userId);

    Token getTokenByJwt(String jwt);

    void createToken(Token token);

    void updateToken(Token token);

    void updateTokens(List<Token> tokens);

}
