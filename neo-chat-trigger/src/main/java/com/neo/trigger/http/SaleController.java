package com.neo.trigger.http;

import com.google.common.eventbus.EventBus;
import com.neo.domain.auth.service.IAuthService;
import com.neo.domain.order.model.entity.PayOrderEntity;
import com.neo.domain.order.model.entity.ProductEntity;
import com.neo.domain.order.model.entity.ShopCartEntity;
import com.neo.domain.order.service.IOrderService;
import com.neo.domain.order.service.IProductService;
import com.neo.trigger.http.dto.PayOrderDTO;
import com.neo.trigger.http.dto.SaleProductDTO;
import com.neo.types.common.Constants;
import com.neo.types.model.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售卖服务
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/sale/")
public class SaleController {

    @Resource
    private IAuthService authService;

    @Resource
    private IProductService productService;

    @Resource
    private IOrderService orderService;

    @Resource
    private EventBus eventBus;

    /**
     * 商品列表
     *
     * @param token 凭证
     * @return 商品列表
     */
    @GetMapping("query_product_list")
    public Response<List<SaleProductDTO>> queryProductList(@RequestHeader("Authorization") String token) {
        try {
            // 验签
            boolean tokenEffective = authService.checkToken(token);
            if (!tokenEffective) {
                return Response.<List<SaleProductDTO>>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 查询商品
            List<ProductEntity> productList = productService.queryProductList();

            List<SaleProductDTO> saleProductList = productList.stream()
                    .map(product -> SaleProductDTO.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDesc(product.getProductDesc())
                            .quota(product.getQuota())
                            .price(product.getPrice())
                            .build())
                    .toList();

            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(saleProductList)
                    .build();
        } catch (Exception e) {
            log.error("商品查询失败", e);
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 商品下单
     *
     * @param token     凭证
     * @param productId 商品id
     * @return 下单结果
     */
    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    public Response<PayOrderDTO> createParOrder(@RequestHeader("Authorization") String token, @RequestParam Integer productId) {
        try {
            // 1. Token 校验
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<PayOrderDTO>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 解析token
            String openid = authService.openid(token);
            if (openid == null) {
                return Response.<PayOrderDTO>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 购物车对象
            ShopCartEntity shopCartEntity = ShopCartEntity.builder()
                    .openid(openid)
                    .productId(productId).build();

            // 创建订单，返回支付单信息
            PayOrderEntity payOrderEntity = orderService.createOrder(shopCartEntity);
            log.info("用户商品下单，根据商品ID创建支付单完成 openid: {} productId: {} orderPay: {}", openid, productId, payOrderEntity.toString());

            PayOrderDTO payOrderResult = PayOrderDTO.builder()
                    .payUrl(payOrderEntity.getPayUrl())
                    .payType(payOrderEntity.getPayType())
                    .build();

            return Response.<PayOrderDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrderResult)
                    .build();
        } catch (Exception e) {
            log.error("用户商品下单，根据商品ID创建支付单失败", e);
            return Response.<PayOrderDTO>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "pay_notify", method = RequestMethod.POST)
    public String payNotify(HttpServletRequest request) {
        try {
            log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));
            if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();
                for (String name : requestParams.keySet()) {
                    params.put(name, request.getParameter(name));
                }

                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");

//                String sign = params.get("sign");
//                String content = AlipaySignature.getSignCheckContentV1(params);
//                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
//                // 支付宝验签
//                if (checkSignature) {
//                    // 验签通过
//                    log.info("支付回调，交易名称: {}", params.get("subject"));
//                    log.info("支付回调，交易状态: {}", params.get("trade_status"));
//                    log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
//                    log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
//                    log.info("支付回调，交易金额: {}", params.get("total_amount"));
//                    log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
//                    log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
//                    log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
//                    log.info("支付回调，支付回调，更新订单 {}", tradeNo);
//                    // 更新订单未已支付
//                    orderService.changeOrderPaySuccess(tradeNo);
//                    // 推送消息【自己的业务场景中可以使用MQ消息】
//                    eventBus.post(tradeNo);
//                }
            }
            return "success";
        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
            return "false";
        }
    }

}
