package com.neo.domain.order.service;

import com.neo.domain.order.model.entity.ProductEntity;

import java.util.List;

public interface IProductService {

    /**
     * 查询所有商品
     * @return 商品列表
     */
    List<ProductEntity> queryProductList();

    /**
     * 根据商品id查询商品
     * @param productId 商品id
     * @return 商品信息
     */
    ProductEntity queryProduct(Integer productId);
}
