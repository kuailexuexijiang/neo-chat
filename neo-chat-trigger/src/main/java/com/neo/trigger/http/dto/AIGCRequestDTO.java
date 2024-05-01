package com.neo.trigger.http.dto;

import com.neo.types.enums.AIGCModel;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AIGCRequestDTO {

    /**
     * 默认模型
     */
    private String model = AIGCModel.GPT_3_5_TURBO.getCode();

    /**
     * 问题描述
     */
    private List<MessageEntity> messages;

}
