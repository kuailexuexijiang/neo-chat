package com.neo.domain.order.service.impl;

import com.neo.domain.order.model.entity.ProductEntity;
import com.neo.domain.order.repository.IProductRepository;
import com.neo.domain.order.service.IProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService implements IProductService {

    @Resource
    private IProductRepository productRepository;

    @Override
    public List<ProductEntity> queryProductList() {
        return productRepository.queryProductList();
    }

    @Override
    public ProductEntity queryProduct(Integer productId) {
        return productRepository.queryProduct(productId);
    }
}
