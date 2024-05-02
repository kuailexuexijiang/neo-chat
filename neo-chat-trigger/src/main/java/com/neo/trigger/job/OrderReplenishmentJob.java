package com.neo.trigger.job;

import com.google.common.eventbus.EventBus;
import com.neo.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description 订单补货任务
 */
@Slf4j
@Component()
public class OrderReplenishmentJob {

    @Resource
    private IOrderService orderService;
    @Resource
    private EventBus eventBus;

    /**
     * 执行订单补货，超时3分钟，已支付，待发货未发货的订单
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryReplenishmentOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单补货不存在，查询 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                log.info("定时任务，订单补货开始。orderId: {}", orderId);
                eventBus.post(orderId);
            }
        } catch (Exception e) {
            log.error("定时任务，订单补货失败。", e);
        }
    }

}