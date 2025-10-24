package com.aicodegenerate.core;

import com.aicodegenerate.ai.AiCodeGeneratorService;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成器门面类,生成代码并保存
 */
@Service
@Slf4j
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
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式: " + codeGenTypeEnum.getValue());
        };
    }


    private File generateMutilFileCodeAndSave(String userMessage) {
        MultiFileCodeResult mutilFileCodeResult = aiCodeGeneratorService.generateMutilFileCode(userMessage);
        return CodeFileSaver.saveMutilFileCodeResult(mutilFileCodeResult);
    }

    private File generateHtmlCodeAndSave(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    public Flux<String> generateCodeAndSaveStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateHtmlCodeAndSaveStream(userMessage);
            case MULTI_FILE -> generateMutilFileCodeAndSaveStream(userMessage);
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式: " + codeGenTypeEnum.getValue());
        };
    }

    /**
     * 生成单文件代码并保存为文件(流式)
     */
    private Flux<String> generateHtmlCodeAndSaveStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        //字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder stringBuilder = new StringBuilder();
        //实时收集代码片段
        return result.doOnNext(stringBuilder::append)
                .doOnComplete(() -> {
                    try {
                        //流式返回完成后，保存代码
                        String completeCode = stringBuilder.toString();
                        //解析代码
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeCode);
                        //保存代码
                        File file = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                        log.info("保存文件成功: {}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存文件失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 生成多文件代码并保存为文件(流式)
     */
    private Flux<String> generateMutilFileCodeAndSaveStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateMutilFileCodeStream(userMessage);
        //字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder stringBuilder = new StringBuilder();
        //实时收集代码片段
        return result.doOnNext(stringBuilder::append)
                .doOnComplete(() -> {
                    try {
                        //流式返回完成后，保存代码
                        String completeCode = stringBuilder.toString();
                        //解析代码
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeCode);
                        //保存代码
                        File file = CodeFileSaver.saveMutilFileCodeResult(multiFileCodeResult);
                        log.info("保存文件成功: {}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存文件失败: {}", e.getMessage());
                    }
                });
    }

}
