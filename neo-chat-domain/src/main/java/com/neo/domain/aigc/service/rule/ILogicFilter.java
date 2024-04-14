package com.neo.domain.aigc.service.rule;


import com.neo.domain.aigc.model.aggregates.ChatProcessAggregate;
import com.neo.domain.aigc.model.entity.RuleLogicEntity;

/**
 * @description 规则过滤接口
 */
public interface ILogicFilter<T> {

    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, T data) throws Exception;

}
