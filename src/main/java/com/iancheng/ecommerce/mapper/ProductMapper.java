package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.dto.ProductQueryParams;
import com.iancheng.ecommerce.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> getProducts(ProductQueryParams productQueryParams);

    Integer countProduct(ProductQueryParams productQueryParams);

    Product getProductById(Integer productId);

    void createProduct(Product product);

    void updateProduct(Product product);

    void updateStock(Integer productId, Integer stock, Date now);

    void deleteProductById(Integer productId);

}
