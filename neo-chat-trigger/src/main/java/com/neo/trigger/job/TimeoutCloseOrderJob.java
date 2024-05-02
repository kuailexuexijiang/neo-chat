package com.neo.trigger.job;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.neo.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description 超时关单任务
 */
@Slf4j
@Component()
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0/10 * * * * ?")
    public void exec() {
        try {
            if (null == alipayClient) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (orderIds.isEmpty()) {
                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);

                AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                JSONObject bizContent = new JSONObject();
                bizContent.put("out_trade_no", orderId);
                request.setBizContent(bizContent.toString());
                AlipayTradeCloseResponse response = alipayClient.execute(request);
                if (response.isSuccess()) {
                    log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
                } else {
                    log.error("定时任务，超时30分钟订单关闭失败 orderId: {}", orderId);
                }
            }
        } catch (Exception e) {
            log.error("定时任务，超时30分钟订单关闭失败", e);
        }
    }

}