package com.aicodegenerate.ai;

import com.aicodegenerate.ai.tools.*;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import com.aicodegenerate.service.impl.ChatHistoryServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    /**
     * 普通对话模型（流式）
     */
    @Resource
    private StreamingChatModel openAiStreamingChatModel;

    /**
     * 推理模型（流式）
     */
    @Resource
    private StreamingChatModel reasoningStreamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryServiceImpl chatHistoryService;

    @Resource
    private ToolManager toolManager;

    /**
     * 根据appId创建AiCodeGeneratorService
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        log.info("创建AiCodeGeneratorService，appId: {}", appId);
        //chatMemory 根据appId创建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(100)
                .build();
        //从数据库中加载对话历史到ai记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        AiCodeGeneratorService aiCodeGeneratorService = null;
        switch (codeGenType) {
            case VUE_PROJECT -> {
                aiCodeGeneratorService = AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        //框架的问题只有在对话方法上有@MemoryId，就必须这样写
                        .chatMemoryProvider(meoryId -> chatMemory)
                        .tools(toolManager.getAllTools())
                        // 处理工具调用幻觉问题
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                ToolExecutionResultMessage.from(toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name())
                        )
                        .build();
            }
            case HTML, MULTI_FILE -> {
                aiCodeGeneratorService = AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .build();
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型：" + codeGenType.getValue());
        }
        ;
        return aiCodeGeneratorService;
    }

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> aiServiceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存key: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        //根据appId从缓存获取服务，如果没有则创建并且自动的加入缓存
        return aiServiceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.builder()
                                .id(memoryId)
                                .chatMemoryStore(redisChatMemoryStore)
                                .maxMessages(100) //对话记忆容量，调大防止AI在对话中断片，不知道之前干了什么，循环生成
                                .build())
                .build();
    }

    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }

    /*    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }*/
}
