package com.neo.domain.aigc.service.rule.impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.neo.domain.aigc.annotation.LogicStrategy;
import com.neo.domain.aigc.model.aggregates.ChatProcessAggregate;
import com.neo.domain.aigc.model.entity.MessageEntity;
import com.neo.domain.aigc.model.entity.RuleLogicEntity;
import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;
import com.neo.domain.aigc.model.valobj.LogicCheckTypeVO;
import com.neo.domain.aigc.service.rule.ILogicFilter;
import com.neo.domain.aigc.service.rule.factory.DefaultLogicFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 敏感词过滤
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private SensitiveWordBs words;

    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 白名单用户不做敏感词处理
        if (chatProcess.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenid(chatProcess.getOpenid());
        newChatProcessAggregate.setModel(chatProcess.getModel());

        List<MessageEntity> newMessages = chatProcess.getMessages().stream()
                .map(message -> {
                    String content = message.getContent();
                    String replace = words.replace(content);
                    return MessageEntity.builder()
                            .role(message.getRole())
                            .name(message.getName())
                            .content(replace)
                            .build();
                })
                .collect(Collectors.toList());

        newChatProcessAggregate.setMessages(newMessages);

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS)
                .data(newChatProcessAggregate)
                .build();
    }

}
