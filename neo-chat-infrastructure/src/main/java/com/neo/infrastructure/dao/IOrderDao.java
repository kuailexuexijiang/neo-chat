package com.neo.infrastructure.dao;

import com.neo.infrastructure.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOrderDao {
    OrderPO queryUnpaidOrder(OrderPO orderPO);

    void insert(OrderPO orderPO);

    void updateOrderPayInfo(OrderPO orderPO);

    int changeOrderPaySuccess(OrderPO orderPO);

    OrderPO queryOrder(String orderId);

    int updateOrderStatusDeliverGoods(String orderId);

    List<OrderPO> queryNoPayNotifyOrder();

    List<String> queryReplenishmentOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);
}
