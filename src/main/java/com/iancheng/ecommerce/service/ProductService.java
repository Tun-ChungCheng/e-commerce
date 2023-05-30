package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.constant.ProductCategory;
import com.iancheng.ecommerce.dto.ProductRequest;
import com.iancheng.ecommerce.model.Product;

import java.util.List;

public interface ProductService {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

    List<Product> getProducts(ProductCategory category, String search);
}
