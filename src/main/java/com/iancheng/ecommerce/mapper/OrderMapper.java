package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.dto.OrderQueryParams;
import com.iancheng.ecommerce.model.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Integer countOrder(OrderQueryParams orderQueryParams);

    void createOrder(Order order);

    Order getOrderById(Integer orderId);

    Integer getUserIdByOrderId(Integer orderId);
}
