package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.CreateOrderRequest;

public interface OrderService {
    Integer createOrder(Integer userId, CreateOrderRequest request);
}
