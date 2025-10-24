package com.aicodegenerate.core;

import com.aicodegenerate.ai.AiCodeGeneratorService;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MutilFileCodeResult;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * AI 代码生成器门面类,生成代码并保存
 */
@Service
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    public File generateCodeAndSave(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateHtmlCodeAndSave(userMessage);
            case MULTI_FILE -> generateMutilFileCodeAndSave(userMessage);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式: " + codeGenTypeEnum.getValue());
        };
    }

    private File generateMutilFileCodeAndSave(String userMessage) {
        MutilFileCodeResult mutilFileCodeResult = aiCodeGeneratorService.generateMutilFileCode(userMessage);
        return CodeFileSaver.saveMutilFileCodeResult(mutilFileCodeResult);
    }

    private File generateHtmlCodeAndSave(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }
}
