package com.aicodegenerate.service;

import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.user.UserRegisterRequest;
import com.aicodegenerate.model.vo.LoginUserVO;
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

    User getUserLogin(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    boolean userLogout(HttpServletRequest request);
}
