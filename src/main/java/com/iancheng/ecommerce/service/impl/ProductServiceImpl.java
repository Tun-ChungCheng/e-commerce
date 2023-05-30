package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.ProductRequest;
import com.iancheng.ecommerce.mapper.ProductMapper;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }


    @Override
    public Product getProductById(Integer productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setDescription(productRequest.getDescription());

        Date now = new Date();
        product.setCreatedDate(now);
        product.setLastModifiedDate(now);

        productMapper.createProduct(product);

        return product.getProductId();
    }
}
