package com.neo.domain.order.service;

import com.neo.domain.order.model.entity.PayOrderEntity;
import com.neo.domain.order.model.entity.ShopCartEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单服务
 */
public interface IOrderService {

    /**
     * 用户下单，通过购物车信息，返回下单后的支付单
     *
     * @param shopCartEntity 简单购物车
     * @return 支付单实体对象
     */
    PayOrderEntity createOrder(ShopCartEntity shopCartEntity);

    /**
     * 变更；订单支付成功
     */
    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, LocalDateTime payTime);
}
