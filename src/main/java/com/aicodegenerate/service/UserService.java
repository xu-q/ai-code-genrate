package com.aicodegenerate.service;

import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.entity.dto.user.UserRegisterRequest;
import com.mybatisflex.core.service.IService;

/**
 * 用户 服务层。
 *
 * @author xu
 */
public interface UserService extends IService<User> {

    Long userRegister(UserRegisterRequest userRegisterRequest);

    String getEncryptPassword(String password);
}
