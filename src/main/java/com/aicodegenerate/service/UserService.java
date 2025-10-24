package com.aicodegenerate.service;

import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.dto.user.UserRegisterRequest;
import com.aicodegenerate.model.vo.LoginUserVO;
import com.aicodegenerate.model.vo.UserVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author xu
 */
public interface UserService extends IService<User> {

    Long userRegister(UserRegisterRequest userRegisterRequest);

    String getEncryptPassword(String password);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     */
    UserVO getUserVO(User user);
}
