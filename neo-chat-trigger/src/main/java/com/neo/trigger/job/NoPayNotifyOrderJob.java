package com.neo.trigger.job;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.eventbus.EventBus;
import com.neo.domain.order.model.entity.PayOrderEntity;
import com.neo.domain.order.model.valobj.PayTypeVO;
import com.neo.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    @Resource
    private IOrderService orderService;
    @Resource
    private EventBus eventBus;
    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void exec() {
        try {
            List<PayOrderEntity> payOrderEntityList = orderService.queryNoPayNotifyOrder();
            if (payOrderEntityList.isEmpty()) {
                log.info("定时任务，订单支付状态更新，暂无未更新订单 orderId is null");
                return;
            }

            for (PayOrderEntity payOrderEntity : payOrderEntityList) {
                Integer payType = payOrderEntity.getPayType();
                if (PayTypeVO.ALIPAY_SANBOX.getCode().equals(payType)) {
                    // 查询支付状态
                    if (alipayClient == null) {
                        log.error("支付宝沙箱客户端为空");
                        continue;
                    }

                    // 构造请求参数以调用接口
                    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                    AlipayTradeQueryModel model = new AlipayTradeQueryModel();

                    // 设置订单支付时传入的商户订单号
                    model.setOutTradeNo(payOrderEntity.getOrderId());
                    request.setBizModel(model);
                    AlipayTradeQueryResponse response = alipayClient.execute(request);
                    log.info("支付宝沙箱查询订单支付状态，orderid={}, response is {}", payOrderEntity.getOrderId(), response);
                    if (response.isSuccess()) {
                        String body = response.getBody();
                        JSONObject trade = JSONObject.parseObject(body);

                        JSONObject alipayTradeQueryResponse = trade.getJSONObject("alipay_trade_query_response");

                        String tradeStatus = alipayTradeQueryResponse.getString("trade_status");
                        if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                            log.info("定时任务，订单支付状态更新，当前订单未支付 orderId is {}, 订单支付状态为{}", payOrderEntity.getOrderId(), tradeStatus);
                            continue;
                        }
                        boolean isSuccess = orderService.changeOrderPaySuccess(payOrderEntity.getOrderId(), ""
                                , alipayTradeQueryResponse.getBigDecimal("total_amount")
                                , LocalDateTime.parse(alipayTradeQueryResponse.getString("send_pay_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        if (isSuccess) {
                            // 发布消息
                            eventBus.post(payOrderEntity.getOrderId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("定时任务，订单支付状态更新失败", e);
        }
    }

}