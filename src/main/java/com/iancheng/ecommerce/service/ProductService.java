package com.iancheng.ecommerce.service;

import com.iancheng.ecommerce.dto.ProductQueryParams;
import com.iancheng.ecommerce.dto.ProductRequest;
import com.iancheng.ecommerce.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts(ProductQueryParams queryParams);

    Integer countProduct(ProductQueryParams queryParams);

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);


}
