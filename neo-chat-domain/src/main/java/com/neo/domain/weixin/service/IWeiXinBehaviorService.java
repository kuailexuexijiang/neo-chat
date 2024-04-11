package com.neo.domain.weixin.service;

import com.neo.domain.weixin.model.entity.UserBehaviorMessageEntity;

public interface IWeiXinBehaviorService {
    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);
}
