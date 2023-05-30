package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Product;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {

    Product getProductById(Integer productId);

    void createProduct(Product product);

    void updateProduct(Product product);
}
