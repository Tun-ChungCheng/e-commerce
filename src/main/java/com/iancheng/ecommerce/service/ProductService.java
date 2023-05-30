package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.ProductRequest;
import com.iancheng.ecommerce.model.Product;

public interface ProductService {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);
}
