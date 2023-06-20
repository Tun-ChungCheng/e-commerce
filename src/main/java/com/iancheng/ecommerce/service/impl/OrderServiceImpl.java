package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.BuyItem;
import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.dto.OrderQueryParams;
import com.iancheng.ecommerce.mapper.OrderItemMapper;
import com.iancheng.ecommerce.mapper.OrderMapper;
import com.iancheng.ecommerce.mapper.ProductMapper;
import com.iancheng.ecommerce.mapper.UserMapper;
import com.iancheng.ecommerce.model.Order;
import com.iancheng.ecommerce.model.OrderItem;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.service.OrderService;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final AllInOne allInOne;

    @Value("${app.order.clientBackUrl}")
    private String CLIENT_BACK_URL;

    @Value("${app.order.returnUrl}")
    private String RETURN_URL;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.allInOne = new AllInOne("");
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        List<Order> orderList = orderMapper.getOrders(orderQueryParams);

        for (Order order : orderList) {
            List<OrderItem> orderItemList = orderItemMapper.getOrderItemsByOrderId(order.getOrderId());

            order.setOrderItemList(orderItemList);
        }

        return orderList;
    }

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderMapper.countOrder(orderQueryParams);
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        // 檢查 user 是否存在
        checkUser(userId);

        BigDecimal totalAmount = new BigDecimal("0");
        List<OrderItem> orderItemList = new ArrayList<>();

        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            Product product = productMapper.getProductById(buyItem.getProductId());
            
            // 檢查 product 是否存在、庫存是否足夠
            checkProduct(product, buyItem);

            // 扣除商品庫存
            productMapper.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity(), new Date());

            // 計算總價錢
            BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(buyItem.getQuantity()));
            totalAmount = totalAmount.add(amount);

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

    @Transactional
    @Override
    public String checkout(Integer userId, Integer orderId) {
        // 檢查 user 是否存在
        checkUser(userId);

        Order order = getOrderById(orderId);

        StringBuilder orderItemDetail = new StringBuilder();
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItemDetail.append('[');
            orderItemDetail.append(orderItem.getProductName());
            orderItemDetail.append(" * ");
            orderItemDetail.append(orderItem.getQuantity());
            orderItemDetail.append(" = $NT");
            orderItemDetail.append(orderItem.getAmount());
            orderItemDetail.append(']');
        }

        AioCheckOutALL aioCheckOutALL = new AioCheckOutALL();
        aioCheckOutALL.setMerchantTradeNo(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
        aioCheckOutALL.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
        aioCheckOutALL.setTotalAmount(order.getTotalAmount().toString());
        aioCheckOutALL.setTradeDesc(orderItemDetail.toString());
        aioCheckOutALL.setItemName(orderItemDetail.toString());
        aioCheckOutALL.setNeedExtraPaidInfo("N");
        aioCheckOutALL.setClientBackURL(CLIENT_BACK_URL);
        aioCheckOutALL.setReturnURL(RETURN_URL);

        String checkoutForm = allInOne.aioCheckOut(aioCheckOutALL, null);

        return checkoutForm;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderMapper.getOrderById(orderId);

        List<OrderItem> orderItemList = orderItemMapper.getOrderItemsByOrderId(orderId);

        order.setOrderItemList(orderItemList);

        return order;
    }

    private void checkUser(Integer userId) {
        if (!userExist(userId)) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean userExist(Integer userId) {
        return userMapper.getUserById(userId) != null;
    }

    private void checkProduct(Product product, BuyItem buyItem) {
        if (product == null) {
            log.warn("商品 {} 不存在", buyItem.getProductId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (product.getStock() < buyItem.getQuantity()) {
            log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {}",
                    buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
