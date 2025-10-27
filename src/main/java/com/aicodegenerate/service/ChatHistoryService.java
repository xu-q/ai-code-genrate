package com.aicodegenerate.service;

import com.aicodegenerate.model.dto.chathistory.ChatHistoryQueryRequest;
import com.aicodegenerate.model.entity.ChatHistory;
import com.aicodegenerate.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     * 添加对话历史
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据应用 id 删除对话历史
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询某 APP 的对话记录
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 加载对话历史到内存
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 构造查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

}
