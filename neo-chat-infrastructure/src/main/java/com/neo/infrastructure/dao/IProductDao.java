package com.neo.infrastructure.dao;

import com.neo.infrastructure.po.ProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IProductDao {

    List<ProductPO> queryProductList();

    ProductPO queryProductByProductId(Integer productId);
}
