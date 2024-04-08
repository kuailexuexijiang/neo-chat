package com.neo.domain.chatgpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neo.domain.chatgpt.model.aggregates.ChatProcessAggregate;
import com.neo.sdk.chatgpt.session.OpenAiSession;
import com.neo.types.common.Constants;
import com.neo.types.exception.NeoChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public abstract class AbstractChatService implements IChatService {

    @Autowired
    protected OpenAiSession openAiSession;

    @Override
    public ResponseBodyEmitter completions(ChatProcessAggregate chatProcess) {
        // 1. 校验权限
        if (!"b8b6".equals(chatProcess.getToken())) {
            throw new NeoChatException(Constants.ResponseCode.TOKEN_ERROR.getCode(), Constants.ResponseCode.TOKEN_ERROR.getInfo());
        }

        // 2. 请求应答
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });

        emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

        // 3. 应答处理
        try {
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new NeoChatException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 4. 返回结果
        return emitter;
    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;

}
