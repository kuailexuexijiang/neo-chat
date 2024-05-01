package com.neo.domain.order.service;

import com.alipay.api.AlipayApiException;
import com.neo.domain.order.model.entity.*;
import com.neo.domain.order.model.valobj.PayStatusVO;
import com.neo.domain.order.repository.IOrderRepository;
import com.neo.types.common.Constants;
import com.neo.types.exception.NeoChatException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    @Resource
    protected IOrderRepository orderRepository;

    @Resource
    protected IProductService productService;


    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) {
        try {
            String openid = shopCartEntity.getOpenid();
            Integer productId = shopCartEntity.getProductId();

            // 查询有效的未支付订单
            UnpaidOrderEntity unpaidOrderEntity = orderRepository.queryUnpaidOrder(shopCartEntity);
            if (unpaidOrderEntity != null && PayStatusVO.WAIT.equals(unpaidOrderEntity.getPayStatus()) && null != unpaidOrderEntity.getPayUrl()) {
                log.info("创建订单-存在，已生成支付，返回 openid: {} orderId: {} payUrl: {}", openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getPayUrl());
                return PayOrderEntity.builder()
                        .openid(openid)
                        .orderId(unpaidOrderEntity.getOrderId())
                        .payType(unpaidOrderEntity.getPayType())
                        .payUrl(unpaidOrderEntity.getPayUrl())
                        .payStatus(unpaidOrderEntity.getPayStatus())
                        .build();
            } else if (null != unpaidOrderEntity && null == unpaidOrderEntity.getPayUrl()) {
                log.info("创建订单-存在，未生成支付，返回 openid: {} orderId: {}", openid, unpaidOrderEntity.getOrderId());
                PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getProductName(), unpaidOrderEntity.getTotalAmount());
                log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, payOrderEntity.getOrderId(), payOrderEntity.getPayUrl());
                return payOrderEntity;
            }

            // 商品查询
            ProductEntity productEntity = productService.queryProduct(productId);
            if (!productEntity.isAvailable()) {
                throw new NeoChatException(Constants.ResponseCode.ORDER_PRODUCT_ERR.getCode(), Constants.ResponseCode.ORDER_PRODUCT_ERR.getInfo());
            }

            // 保存订单
            OrderEntity orderEntity = this.doSaveOrder(openid, productEntity);

            // 创建支付单
            PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, orderEntity.getOrderId(), productEntity.getProductName(), productEntity.getPrice());
            log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, payOrderEntity.getOrderId(), payOrderEntity.getPayUrl());
            return payOrderEntity;
        } catch (Exception e) {
            log.error("创建订单，已生成支付，返回 openid: {} productId: {}", shopCartEntity.getOpenid(), shopCartEntity.getProductId());
            throw new NeoChatException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

    }

    protected abstract PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal totalAmount) throws AlipayApiException;

    protected abstract OrderEntity doSaveOrder(String openid, ProductEntity productEntity);

}
