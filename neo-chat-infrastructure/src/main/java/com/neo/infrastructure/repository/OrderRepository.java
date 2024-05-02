package com.neo.infrastructure.repository;

import com.neo.domain.aigc.model.valobj.UserAccountStatusVO;
import com.neo.domain.order.model.aggregates.CreateOrderAggregate;
import com.neo.domain.order.model.entity.*;
import com.neo.domain.order.model.valobj.PayStatusVO;
import com.neo.domain.order.repository.IOrderRepository;
import com.neo.infrastructure.dao.IOrderDao;
import com.neo.infrastructure.dao.IUserAccountDao;
import com.neo.infrastructure.po.OrderPO;
import com.neo.infrastructure.po.UserAccountPO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOrderDao orderDao;

    @Resource
    private IUserAccountDao userAccountDao;

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
        saveOrder.setProductModelTypes(product.getProductModelTypes());
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

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 350, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void deliverGoods(String orderId) {
       OrderPO orderPO = orderDao.queryOrder(orderId);

        // 1. 变更发货状态
        int updateOrderStatusDeliverGoodsCount = orderDao.updateOrderStatusDeliverGoods(orderId);
        if (1 != updateOrderStatusDeliverGoodsCount) throw new RuntimeException("updateOrderStatusDeliverGoodsCount update count is not equal 1");

        // 2. 账户额度变更
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(orderPO.getOpenid());
        UserAccountPO userAccountPOReq = new UserAccountPO();
        userAccountPOReq.setOpenid(orderPO.getOpenid());
        userAccountPOReq.setTotalQuota(orderPO.getProductQuota());
        userAccountPOReq.setSurplusQuota(orderPO.getProductQuota());
        // todo 待处理
        userAccountPOReq.setModelTypes(orderPO.getProductModelTypes());
        if (null != userAccountPO){
            int addAccountQuotaCount = userAccountDao.addAccountQuota(userAccountPOReq);
            if (1 != addAccountQuotaCount) throw new RuntimeException("addAccountQuotaCount update count is not equal 1");
        } else {
            userAccountPOReq.setStatus(UserAccountStatusVO.AVAILABLE.getCode());
            userAccountDao.insert(userAccountPOReq);
        }
    }

    @Override
    public List<PayOrderEntity> queryNoPayNotifyOrder() {
        List<OrderPO> orderPOList = orderDao.queryNoPayNotifyOrder();
        return orderPOList.stream().map(orderPO -> PayOrderEntity.builder()
                   .orderId(orderPO.getOrderId())
                   .payType(orderPO.getPayType())
                   .build()).collect(Collectors.toList());
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return orderDao.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }
}
