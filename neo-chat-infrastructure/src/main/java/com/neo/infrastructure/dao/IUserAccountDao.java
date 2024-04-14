package com.neo.infrastructure.dao;

import com.neo.infrastructure.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserAccountDao {

    int subAccountQuota(String openid);

    UserAccountPO queryUserAccount(String openid);

}