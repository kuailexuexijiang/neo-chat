package com.neo.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayTypeVO {

    WEIXIN_NATIVE(0, "微信Native支付"),
    ALIPAY_SANBOX(1, "支付宝沙箱支付"),
            ;

    private final Integer code;
    private final String desc;

    public static PayTypeVO get(Integer code){
        switch (code){
            case 0:
                return PayTypeVO.WEIXIN_NATIVE;
            default:
                return PayTypeVO.WEIXIN_NATIVE;
        }
    }

}