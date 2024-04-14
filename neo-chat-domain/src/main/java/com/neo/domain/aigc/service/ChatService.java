package com.neo.domain.aigc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neo.domain.aigc.model.aggregates.ChatProcessAggregate;
import com.neo.domain.aigc.model.entity.RuleLogicEntity;
import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;
import com.neo.domain.aigc.model.valobj.LogicCheckTypeVO;
import com.neo.domain.aigc.service.rule.ILogicFilter;
import com.neo.domain.aigc.service.rule.factory.DefaultLogicFactory;
import com.neo.sdk.chatgpt.common.Constants;
import com.neo.sdk.chatgpt.domain.ChatChoice;
import com.neo.sdk.chatgpt.domain.ChatCompletionRequest;
import com.neo.sdk.chatgpt.domain.ChatCompletionResponse;
import com.neo.sdk.chatgpt.domain.Message;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService extends AbstractChatService {

    @Resource
    private DefaultLogicFactory logicFactory;

    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception {
        Map<String, ILogicFilter> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatProcessAggregate> entity = null;
        for (String code : logics) {
            if (DefaultLogicFactory.LogicModel.NULL.getCode().equals(code)) continue;
            entity = logicFilterMap.get(code).filter(chatProcess, userAccountQuotaEntity);
            if (!LogicCheckTypeVO.SUCCESS.equals(entity.getType())) return entity;
        }
        return entity != null ? entity : RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
    }

    @Override
    protected void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws JsonProcessingException {
        // 1. 请求消息
        List<Message> messages = chatProcess.getMessages().stream().map(entity -> Message.builder().role(Constants.Role.valueOf(entity.getRole().toUpperCase())).content(entity.getContent()).name(entity.getName()).build()).collect(Collectors.toList());

        // 2. 封装参数
        ChatCompletionRequest chatCompletion = ChatCompletionRequest.builder().stream(true).messages(messages).model(ChatCompletionRequest.Model.GPT_3_5_TURBO.getCode()).build();

        // 3.2 请求应答
        openAiSession.chatCompletions(chatCompletion, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    if (Constants.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = chatChoice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                        emitter.complete();
                        break;
                    }

                    // 发送信息
                    try {
                        emitter.send(delta.getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }

}
