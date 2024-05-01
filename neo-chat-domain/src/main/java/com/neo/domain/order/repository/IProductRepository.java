package com.neo.domain.order.repository;

import com.neo.domain.order.model.entity.ProductEntity;

import java.util.List;

public interface IProductRepository {
    List<ProductEntity> queryProductList();

    ProductEntity queryProduct(Integer productId);
}
