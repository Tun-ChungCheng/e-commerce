package com.iancheng.ecommerce.controller;

import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.dto.OrderQueryParams;
import com.iancheng.ecommerce.model.Order;
import com.iancheng.ecommerce.service.OrderService;
import com.iancheng.ecommerce.util.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        // 取得 order list
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        // 取得 order 總數
        Integer count = orderService.countOrder(orderQueryParams);

        // 分頁
        Page<Order> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(count);
        page.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(
            @PathVariable Integer userId,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<String> checkout(
            @PathVariable Integer userId,
            @PathVariable Integer orderId
    ) {
        String checkoutForm = orderService.checkout(userId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(checkoutForm);
    }

    @PostMapping("/callback")
    public void callback(@RequestBody MultiValueMap<String, String> formData) {
        orderService.callback(formData);
    }
}
