package com.aicodegenerate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.mapper.UserMapper;
import com.aicodegenerate.model.dto.user.UserQueryRequest;
import com.aicodegenerate.model.dto.user.UserRegisterRequest;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.enums.UserRoleEnum;
import com.aicodegenerate.model.vo.LoginUserVO;
import com.aicodegenerate.model.vo.UserVO;
import com.aicodegenerate.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import static com.aicodegenerate.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author xu
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    static final String SALT = "aicodegenerate";

    @Override
    public Long userRegister(UserRegisterRequest user) {
        if (StrUtil.hasBlank(user.getUserAccount(), user.getUserPassword(), user.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (user.getUserAccount().length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (user.getUserPassword().length() < 4 || user.getUserPassword().length() > 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度为4到8位");
        }
        if (!user.getUserPassword().equals(user.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", user.getUserAccount());
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在");
        }

        User newUser = new User();
        newUser.setUserAccount(user.getUserAccount());
        newUser.setUserPassword(getEncryptPassword(user.getUserPassword()));
        newUser.setUserAccount(user.getUserAccount());
        newUser.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(newUser);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        }
        return newUser.getId();
    }

    public String getEncryptPassword(String userPassword) {
        return DigestUtil.md5Hex(SALT + userPassword, "UTF-8");
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 4 || userPassword.length() > 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度为4到8位");
        }
        User user = this.mapper.selectOneByQuery(new QueryWrapper().eq("userAccount", userAccount).eq("userPassword", getEncryptPassword(userPassword)));
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = this.mapper.selectOneById(user.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}
