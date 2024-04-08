package com.neo.trigger.http.dto;

import com.neo.types.enums.ChatGPTModel;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatGPTRequestDTO {

    /**
     * 默认模型
     */
    private String model = ChatGPTModel.GPT_3_5_TURBO.getCode();

    /**
     * 问题描述
     */
    private List<MessageEntity> messages;

}
