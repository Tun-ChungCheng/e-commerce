package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.dto.OrderQueryParams;
import com.iancheng.ecommerce.model.Order;

import java.util.List;

public interface OrderService {
    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

    Order getOrderById(Integer orderId);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Integer countOrder(OrderQueryParams orderQueryParams);
}
