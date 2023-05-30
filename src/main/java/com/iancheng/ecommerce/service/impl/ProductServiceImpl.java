package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.mapper.ProductMapper;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

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
}
