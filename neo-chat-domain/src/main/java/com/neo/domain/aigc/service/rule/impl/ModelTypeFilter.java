package com.neo.domain.aigc.service.rule.impl;

import com.neo.domain.aigc.annotation.LogicStrategy;
import com.neo.domain.aigc.model.aggregates.ChatProcessAggregate;
import com.neo.domain.aigc.model.entity.RuleLogicEntity;
import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;
import com.neo.domain.aigc.model.valobj.LogicCheckTypeVO;
import com.neo.domain.aigc.service.rule.ILogicFilter;
import com.neo.domain.aigc.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 1. 用户可用模型
        List<String> allowModelTypeList = data.getAllowModelTypeList();
        String modelType = chatProcess.getModel();

        // 2. 模型校验通过
        if (allowModelTypeList.contains(modelType)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcess)
                    .build();
        }

        // 3. 模型校验拦截
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.REFUSE)
                .info("当前账户不支持使用 " + modelType + " 模型！可以联系客服升级账户。")
                .data(chatProcess)
                .build();
    }

}
