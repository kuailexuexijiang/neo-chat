package com.neo.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AIGCModel {

    /** gpt-3.5-turbo */
    GPT_3_5_TURBO("gpt-3.5-turbo"),

    ;
    private final String code;

}
