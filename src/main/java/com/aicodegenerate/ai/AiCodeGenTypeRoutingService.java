package com.aicodegenerate.ai;

import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * AI 代码生成器类型路由服务
 * 使用结构化输出直接返回定义的枚举类型
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户输入，智能选择对应的生成类型
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userMessage);
}
