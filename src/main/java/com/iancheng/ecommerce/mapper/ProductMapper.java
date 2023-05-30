package com.iancheng.ecommerce.mapper;

import com.iancheng.ecommerce.model.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE product_id = #{productId}")
    Product getProductById(Integer productId);

    @Insert("INSERT INTO product(product_name, category, image_url, price, stock, description, created_date, last_modified_date) " +
            "VALUES (#{productName}, #{category}, #{imageUrl}, #{price}, #{stock}, #{description}, #{createdDate}, #{lastModifiedDate})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    void createProduct(Product product);
}
