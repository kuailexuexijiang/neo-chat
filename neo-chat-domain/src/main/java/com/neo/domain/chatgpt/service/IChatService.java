package com.neo.domain.chatgpt.service;

import com.neo.domain.chatgpt.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {

    ResponseBodyEmitter completions(ChatProcessAggregate chatProcess);

}