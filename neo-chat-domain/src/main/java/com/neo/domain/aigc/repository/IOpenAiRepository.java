package com.neo.domain.aigc.repository;

import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;

public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);
}
