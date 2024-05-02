package com.neo.infrastructure.repository;

import com.neo.domain.order.model.entity.ProductEntity;
import com.neo.domain.order.repository.IProductRepository;
import com.neo.infrastructure.dao.IProductDao;
import com.neo.infrastructure.po.ProductPO;
import com.neo.types.enums.ProductEnableModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository implements IProductRepository {
    @Resource
    private IProductDao productDao;

    @Override
    public List<ProductEntity> queryProductList() {
        List<ProductPO> productList = productDao.queryProductList();
        List<ProductEntity> productEntityList = new ArrayList<>(productList.size());
        for (ProductPO product : productList) {
            productEntityList.add(ProductEntity.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productDesc(product.getProductDesc())
                    .price(product.getPrice())
                    .quota(product.getQuota())
                    .build());
        }
        return productEntityList;
    }

    @Override
    public ProductEntity queryProduct(Integer productId) {
        ProductPO productPO = productDao.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(productPO.getProductId())
                .productName(productPO.getProductName())
                .productDesc(productPO.getProductDesc())
                .productModelTypes(productPO.getProductModelTypes())
                .price(productPO.getPrice())
                .quota(productPO.getQuota())
                .enable(ProductEnableModel.get(productPO.getIsEnabled()))
                .build();
    }
}
