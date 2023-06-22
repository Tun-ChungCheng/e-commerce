package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Trade;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeMapper {
    void saveTrade(Trade trade);
}
