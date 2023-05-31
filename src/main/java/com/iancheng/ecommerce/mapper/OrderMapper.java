package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    void createOrder(Order order);
}
