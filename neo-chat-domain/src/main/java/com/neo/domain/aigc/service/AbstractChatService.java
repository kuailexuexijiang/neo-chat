package com.neo.domain.aigc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neo.domain.aigc.model.aggregates.ChatProcessAggregate;
import com.neo.domain.aigc.model.entity.RuleLogicEntity;
import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;
import com.neo.domain.aigc.model.valobj.LogicCheckTypeVO;
import com.neo.domain.aigc.repository.IOpenAiRepository;
import com.neo.domain.aigc.service.rule.factory.DefaultLogicFactory;
import com.neo.sdk.chatgpt.session.OpenAiSession;
import com.neo.types.common.Constants;
import com.neo.types.exception.NeoChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
public abstract class AbstractChatService implements IChatService {

    @Autowired
    protected OpenAiSession openAiSession;

    @Autowired
    private IOpenAiRepository openAiRepository;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            emitter.onCompletion(() -> log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel()));

            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

            // 2. 账户获取
            UserAccountQuotaEntity userAccountQuotaEntity = openAiRepository.queryUserAccount(chatProcess.getOpenid());

            // 2. 规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccountQuotaEntity,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode()
            );

            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 3. 应答处理
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new NeoChatException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }


        // 4. 返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception;

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;

}
