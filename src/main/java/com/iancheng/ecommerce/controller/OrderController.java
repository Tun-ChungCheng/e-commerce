package com.iancheng.ecommerce.controller;

import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(
            @PathVariable Integer userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        Integer orderId = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
}
