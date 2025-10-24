package com.aicodegenerate.service;

import com.aicodegenerate.model.dto.app.AppAddRequest;
import com.aicodegenerate.model.dto.app.AppQueryRequest;
import com.aicodegenerate.model.dto.user.UserQueryRequest;
import com.aicodegenerate.model.entity.App;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 应用 服务层。
 */
public interface AppService extends IService<App> {

    /**
     * 创建应用
     */
    Long createApp(AppAddRequest appAddRequest, User loginUser);

    /**
     * 获取应用封装类
     */
    AppVO getAppVO(App app);

    /**
     * 根据查询条件构造数据查询参数
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取应用封装类列表
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);


}
