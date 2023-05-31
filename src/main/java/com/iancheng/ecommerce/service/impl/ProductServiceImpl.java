package com.iancheng.ecommerce.service.impl;

import com.iancheng.ecommerce.dto.ProductQueryParams;
import com.iancheng.ecommerce.dto.ProductRequest;
import com.iancheng.ecommerce.mapper.ProductMapper;
import com.iancheng.ecommerce.model.Product;
import com.iancheng.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }


    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        return productMapper.getProducts(productQueryParams);
    }

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        return productMapper.countProduct(productQueryParams);
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

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productRequest.getProductName());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setDescription(productRequest.getDescription());
        product.setLastModifiedDate(new Date());

        productMapper.updateProduct(product);
    }

    @Override
    public void deleteProductById(Integer productId) {
        productMapper.deleteProductById(productId);
    }

}