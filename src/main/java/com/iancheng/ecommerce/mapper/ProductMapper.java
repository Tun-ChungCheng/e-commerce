package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE product_id = #{productId}")
    Product getProductById(Integer productId);
}
