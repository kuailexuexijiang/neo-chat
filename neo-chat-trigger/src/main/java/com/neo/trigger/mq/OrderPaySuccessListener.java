package com.neo.trigger.mq;

import com.google.common.eventbus.Subscribe;
import com.neo.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private IOrderService orderService;

    @Subscribe
    public void handleEvent(String orderId) {
        try {
            log.info("支付完成，发货并记录，开始。订单：{}", orderId);
//            orderService.deliverGoods(orderId);
        } catch (Exception e) {
            log.error("支付完成，发货并记录，失败。订单：{}", orderId, e);
        }
    }

}