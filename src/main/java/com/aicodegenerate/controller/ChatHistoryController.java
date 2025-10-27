package com.aicodegenerate.controller;

import com.aicodegenerate.annotation.AuthCheck;
import com.aicodegenerate.common.BaseResponse;
import com.aicodegenerate.common.ResultUtils;
import com.aicodegenerate.constant.UserConstant;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.exception.ThrowUtils;
import com.aicodegenerate.model.dto.chathistory.ChatHistoryQueryRequest;
import com.aicodegenerate.model.entity.ChatHistory;
import com.aicodegenerate.model.entity.User;
import com.aicodegenerate.service.ChatHistoryService;
import com.aicodegenerate.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 对话历史。
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {
    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private UserService userService;

    /**
     * 分页查询某个应用的对话历史（游标查询）
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false) LocalDateTime lastCreateTime, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询所有对话历史
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = chatHistoryQueryRequest.getPageNum();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }
}
