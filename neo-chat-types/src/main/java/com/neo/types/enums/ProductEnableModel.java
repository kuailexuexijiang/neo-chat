package com.neo.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductEnableModel {

    CLOSE(0, "无效，已关闭"),
    OPEN(1,"有效，使用中"),
    ;

    private final Integer code;

    private final String info;

    public static ProductEnableModel get(Integer code){
        if (code == 1) {
            return ProductEnableModel.OPEN;
        }
        return ProductEnableModel.CLOSE;
    }
}
