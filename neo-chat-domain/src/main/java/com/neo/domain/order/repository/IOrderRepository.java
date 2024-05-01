package com.neo.domain.order.repository;

import com.neo.domain.order.model.aggregates.CreateOrderAggregate;
import com.neo.domain.order.model.entity.PayOrderEntity;
import com.neo.domain.order.model.entity.ShopCartEntity;
import com.neo.domain.order.model.entity.UnpaidOrderEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IOrderRepository {
    UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity);

    void saveOrder(CreateOrderAggregate aggregate);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, LocalDateTime payTime);
}
