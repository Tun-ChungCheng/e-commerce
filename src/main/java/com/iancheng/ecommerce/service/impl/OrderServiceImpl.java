package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.BuyItem;
import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.mapper.OrderItemMapper;
import com.iancheng.ecommerce.mapper.OrderMapper;
import com.iancheng.ecommerce.mapper.ProductMapper;
import com.iancheng.ecommerce.model.Order;
import com.iancheng.ecommerce.model.OrderItem;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;


    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }


    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest request) {
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        for (BuyItem buyItem : request.getBuyItemList()) {
            Product product = productMapper.getProductById(buyItem.getProductId());

            // 計算總價錢
            int amount = product.getPrice() * buyItem.getQuantity();
            totalAmount += amount;

            // 轉換 BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);

        Date now = new Date();
        order.setCreatedDate(now);
        order.setLastModifiedDate(now);

        // 創建訂單
        orderMapper.createOrder(order);

        orderItemMapper.createOrderItems(order.getOrderId(), orderItemList);

        return order.getOrderId();
    }
}
