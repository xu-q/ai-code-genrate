package com.aicodegenerate.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.aicodegenerate.enums.UserRoleEnum;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.mapper.UserMapper;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.entity.dto.user.UserRegisterRequest;
import com.aicodegenerate.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}
