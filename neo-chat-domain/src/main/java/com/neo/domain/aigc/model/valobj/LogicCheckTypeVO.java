package com.neo.domain.aigc.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 逻辑校验类型，值对象
 */
@Getter
@AllArgsConstructor
public enum LogicCheckTypeVO {

    SUCCESS("0000", "校验通过"),
    REFUSE("0001","校验拒绝"),
            ;

    private final String code;
    private final String info;

}
