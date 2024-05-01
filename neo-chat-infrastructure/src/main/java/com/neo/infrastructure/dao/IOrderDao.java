package com.neo.infrastructure.dao;

import com.neo.infrastructure.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IOrderDao {
    OrderPO queryUnpaidOrder(OrderPO orderPO);

    void insert(OrderPO orderPO);

    void updateOrderPayInfo(OrderPO orderPO);

    int changeOrderPaySuccess(OrderPO orderPO);
}
