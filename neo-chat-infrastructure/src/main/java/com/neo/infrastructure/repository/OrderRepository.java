package com.neo.infrastructure.repository;

import com.neo.domain.order.model.aggregates.CreateOrderAggregate;
import com.neo.domain.order.model.entity.*;
import com.neo.domain.order.model.valobj.PayStatusVO;
import com.neo.domain.order.repository.IOrderRepository;
import com.neo.infrastructure.dao.IOrderDao;
import com.neo.infrastructure.po.OrderPO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOrderDao orderDao;

    @Override
    public UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity) {
        OrderPO orderPO = new OrderPO();
        orderPO.setOpenid(shopCartEntity.getOpenid());
        orderPO.setProductId(shopCartEntity.getProductId());
        OrderPO unpaidOrder = orderDao.queryUnpaidOrder(orderPO);
        if (unpaidOrder == null) return null;

        return UnpaidOrderEntity.builder()
                .openid(shopCartEntity.getOpenid())
                .orderId(unpaidOrder.getOrderId())
                .productName(unpaidOrder.getProductName())
                .totalAmount(unpaidOrder.getTotalAmount())
                .payType(unpaidOrder.getPayType())
                .payUrl(unpaidOrder.getPayUrl())
                .payStatus(PayStatusVO.get(unpaidOrder.getPayStatus()))
                .build();
    }

    @Override
    public void saveOrder(CreateOrderAggregate aggregate) {
        String openid = aggregate.getOpenid();
        ProductEntity product = aggregate.getProduct();
        OrderEntity order = aggregate.getOrder();
        OrderPO saveOrder = new OrderPO();
        saveOrder.setOpenid(openid);
        saveOrder.setProductId(product.getProductId());
        saveOrder.setProductName(product.getProductName());
        saveOrder.setProductQuota(product.getQuota());
        saveOrder.setOrderId(order.getOrderId());
        saveOrder.setOrderTime(order.getOrderTime());
        saveOrder.setOrderStatus(order.getOrderStatus().getCode());
        saveOrder.setTotalAmount(order.getTotalAmount());
        saveOrder.setPayType(order.getPayTypeVO().getCode());
        saveOrder.setPayStatus(PayStatusVO.WAIT.getCode());
        orderDao.insert(saveOrder);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        OrderPO orderPO = new OrderPO();
        orderPO.setOpenid(payOrderEntity.getOpenid());
        orderPO.setOrderId(payOrderEntity.getOrderId());
        orderPO.setPayUrl(payOrderEntity.getPayUrl());
        orderPO.setPayStatus(payOrderEntity.getPayStatus().getCode());
        orderDao.updateOrderPayInfo(orderPO);
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, LocalDateTime payTime) {
        OrderPO orderPO = new OrderPO();
        orderPO.setOrderId(orderId);
        orderPO.setPayAmount(totalAmount);
        orderPO.setPayTime(payTime);
        orderPO.setTransactionId(transactionId);
        int count = orderDao.changeOrderPaySuccess(orderPO);
        return count == 1;
    }
}
