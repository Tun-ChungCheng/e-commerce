package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.model.Order;

public interface OrderService {
    Integer createOrder(Integer userId, CreateOrderRequest request);

    Order getOrderById(Integer orderId);
}
