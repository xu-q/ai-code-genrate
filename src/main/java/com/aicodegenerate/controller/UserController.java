package com.aicodegenerate.controller;

import com.aicodegenerate.annotation.AuthCheck;
import com.aicodegenerate.common.BaseResponse;
import com.aicodegenerate.common.ResultUtils;
import com.aicodegenerate.constant.UserConstant;
import com.aicodegenerate.enums.UserRoleEnum;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.exception.ThrowUtils;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.entity.dto.user.UserLoginRequest;
import com.aicodegenerate.model.entity.dto.user.UserRegisterRequest;
import com.aicodegenerate.model.entity.vo.LoginUserVO;
import com.aicodegenerate.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 控制层。
 *
 * @author xu
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest user) {
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userRegister(user));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getUserLogin(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

}
