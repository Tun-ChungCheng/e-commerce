package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.BuyItem;
import com.iancheng.ecommerce.dto.CreateOrderRequest;
import com.iancheng.ecommerce.dto.OrderQueryParams;
import com.iancheng.ecommerce.mapper.*;
import com.iancheng.ecommerce.model.Order;
import com.iancheng.ecommerce.model.OrderItem;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.model.Trade;
import com.iancheng.ecommerce.service.OrderService;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
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
    private final TradeMapper tradeMapper;
    private final AllInOne allInOne;

    @Value("${app.order.clientBackUrl}")
    private String CLIENT_BACK_URL;

    @Value("${app.order.returnUrl}")
    private String RETURN_URL;

    public OrderServiceImpl(
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper,
            ProductMapper productMapper,
            UserMapper userMapper,
            TradeMapper tradeMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.tradeMapper = tradeMapper;
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
        order.setMerchantTradeNo(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20));
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

        // 檢查 order 是否存在
        checkOrder(orderId);

        // 檢查 order 是否屬於此 user
        checkOrderUser(userId, orderId);

        Order order = getOrderById(orderId);

        // 產生訂單細節
        StringBuilder merchandiseDetail = new StringBuilder();
        for (OrderItem orderItem : order.getOrderItemList()) {
            merchandiseDetail.append("#『").
                        append(orderItem.getProductName()).
                        append(" * ").
                        append(orderItem.getQuantity()).
                        append(" = $NT").
                        append(orderItem.getAmount()).
                        append("』");
        }

        // 呼叫 ECPay API 建立結帳頁面
        AioCheckOutALL aioCheckOutALL = new AioCheckOutALL();
        aioCheckOutALL.setMerchantTradeNo(order.getMerchantTradeNo());
        aioCheckOutALL.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
        aioCheckOutALL.setTotalAmount(order.getTotalAmount().toString());
        aioCheckOutALL.setTradeDesc(merchandiseDetail.toString());
        aioCheckOutALL.setItemName(merchandiseDetail.toString());
        aioCheckOutALL.setNeedExtraPaidInfo("N");
        aioCheckOutALL.setClientBackURL(CLIENT_BACK_URL);
        aioCheckOutALL.setReturnURL(RETURN_URL);

        String checkoutForm = allInOne.aioCheckOut(aioCheckOutALL, null);

        return checkoutForm;
    }

    @Override
    public void callback(MultiValueMap<String, String> formData) {
        formData.forEach((key, value) -> System.out.println(key + " : " + value.get(0)));

        // 保存交易紀錄
        Trade trade = new Trade();
        trade.setCustomField1(formData.get("CustomField1").get(0));
        trade.setCustomField1(formData.get("CustomField2").get(0));
        trade.setCustomField1(formData.get("CustomField3").get(0));
        trade.setCustomField1(formData.get("CustomField4").get(0));
        trade.setMerchantId(formData.get("MerchantID").get(0));
        trade.setMerchantTradeNo(formData.get("MerchantTradeNo").get(0));
        trade.setPaymentDate(formData.get("PaymentDate").get(0));
        trade.setPaymentType(formData.get("PaymentType").get(0));
        trade.setPaymentTypeChargeFee(formData.get("PaymentTypeChargeFee").get(0));
        trade.setRtnCode(formData.get("RtnCode").get(0));
        trade.setRtnMsg(formData.get("RtnMsg").get(0));
        trade.setSimulatePaid(formData.get("SimulatePaid").get(0));
        trade.setStoreId(formData.get("StoreID").get(0));
        trade.setTradeAmt(formData.get("TradeAmt").get(0));
        trade.setTradeDate(formData.get("TradeDate").get(0));
        trade.setTradeNo(formData.get("TradeNo").get(0));
        trade.setCheckMacValue(formData.get("CheckMacValue").get(0));

        tradeMapper.saveTrade(trade);
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
        if (!productExist(product)) {
            log.warn("商品 {} 不存在", buyItem.getProductId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (!productEnough(product, buyItem)) {
            log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {}",
                    buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean productExist(Product product) {
        return product != null;
    }

    private boolean productEnough(Product product, BuyItem buyItem) {
        return product.getStock() >= buyItem.getQuantity();
    }

    private void checkOrder(Integer orderId) {
        if (!orderExist(orderId)) {
            log.warn("訂單 {} 不存在", orderId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean orderExist(Integer orderId) {
        return orderMapper.getOrderById(orderId) != null;
    }

    private void checkOrderUser(Integer userId, Integer orderId) {
        if (!userId.equals(getUserIdByOrderId(orderId))) {
            log.warn("訂單 {} 不屬於 userId {}", orderId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Integer getUserIdByOrderId(Integer orderId) {
        return orderMapper.getUserIdByOrderId(orderId);
    }
}
