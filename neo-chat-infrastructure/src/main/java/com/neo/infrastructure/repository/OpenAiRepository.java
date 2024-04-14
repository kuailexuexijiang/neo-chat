package com.neo.infrastructure.repository;

import com.neo.domain.aigc.model.entity.UserAccountQuotaEntity;
import com.neo.domain.aigc.model.valobj.UserAccountStatusVO;
import com.neo.domain.aigc.repository.IOpenAiRepository;
import com.neo.infrastructure.dao.IUserAccountDao;
import com.neo.infrastructure.po.UserAccountPO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class OpenAiRepository implements IOpenAiRepository {

    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public int subAccountQuota(String openai) {
        return userAccountDao.subAccountQuota(openai);
    }

    @Override
    public UserAccountQuotaEntity queryUserAccount(String openid) {
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openid);
        if (null == userAccountPO) return null;
        UserAccountQuotaEntity userAccountQuotaEntity = new UserAccountQuotaEntity();
        userAccountQuotaEntity.setOpenid(userAccountPO.getOpenid());
        userAccountQuotaEntity.setTotalQuota(userAccountPO.getTotalQuota());
        userAccountQuotaEntity.setSurplusQuota(userAccountPO.getSurplusQuota());
        userAccountQuotaEntity.setUserAccountStatusVO(UserAccountStatusVO.get(userAccountPO.getStatus()));
        userAccountQuotaEntity.genModelTypes(userAccountPO.getModelTypes());
        return userAccountQuotaEntity;
    }

}