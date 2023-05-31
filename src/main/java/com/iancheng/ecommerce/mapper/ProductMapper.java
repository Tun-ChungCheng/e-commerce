package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.dto.ProductQueryParams;
import com.iancheng.ecommerce.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> getProducts(ProductQueryParams queryParams);

    Integer countProduct(ProductQueryParams queryParams);

    Product getProductById(Integer productId);

    void createProduct(Product product);

    void updateProduct(Product product);

    void deleteProductById(Integer productId);



}
