package com.neo.domain.order.service.impl.pay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.neo.domain.order.model.aggregates.CreateOrderAggregate;
import com.neo.domain.order.model.entity.OrderEntity;
import com.neo.domain.order.model.entity.PayOrderEntity;
import com.neo.domain.order.model.entity.ProductEntity;
import com.neo.domain.order.model.valobj.OrderStatusVO;
import com.neo.domain.order.model.valobj.PayStatusVO;
import com.neo.domain.order.model.valobj.PayTypeVO;
import com.neo.domain.order.service.AbstractOrderService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AliPayService extends AbstractOrderService {
    @Value("${alipay.notify_url}")
    private String notifyUrl;

    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private AlipayClient alipayClient;

    @Override
    protected PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal totalAmount) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOpenid(openid);
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setPayStatus(PayStatusVO.WAIT);
        payOrderEntity.setPayType(PayTypeVO.ALIPAY_SANBOX.getCode());

        // 更新订单支付信息
        orderRepository.updateOrderPayInfo(payOrderEntity);
        return payOrderEntity;
    }

    @Override
    protected OrderEntity doSaveOrder(String openid, ProductEntity productEntity) {
        OrderEntity orderEntity = new OrderEntity();
        // 数据库有幂等拦截，如果有重复的订单ID会报错主键冲突。如果是公司里一般会有专门的雪花算法UUID服务
        orderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        orderEntity.setOrderTime(LocalDateTime.now());
        orderEntity.setOrderStatus(OrderStatusVO.CREATE);
        orderEntity.setTotalAmount(productEntity.getPrice());
        orderEntity.setPayTypeVO(PayTypeVO.ALIPAY_SANBOX);
        // 聚合信息
        CreateOrderAggregate aggregate = CreateOrderAggregate.builder()
                .openid(openid)
                .product(productEntity)
                .order(orderEntity)
                .build();
        // 保存订单；订单和支付，是2个操作。
        // 一个是数据库操作，一个是HTTP操作。所以不能一个事务处理，只能先保存订单再操作创建支付单，如果失败则需要任务补偿
        orderRepository.saveOrder(aggregate);
        return orderEntity;
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, LocalDateTime payTime) {
        return orderRepository.changeOrderPaySuccess(orderId, transactionId, totalAmount, payTime);
    }
}
