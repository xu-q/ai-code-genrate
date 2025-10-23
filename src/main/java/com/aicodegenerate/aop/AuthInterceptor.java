package com.aicodegenerate.aop;

import com.aicodegenerate.annotation.AuthCheck;
import com.aicodegenerate.enums.UserRoleEnum;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserServiceImpl userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        User loginUser = userService.getUserLogin(requestAttributes.getRequest());
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //为空表示不需要权限
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须是管理员
        if (UserRoleEnum.ADMIN.getValue().equals(mustRole) && !(UserRoleEnum.ADMIN.equals(userRoleEnum))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
