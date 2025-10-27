package com.aicodegenerate.core;

import com.aicodegenerate.ai.AiCodeGeneratorService;
import com.aicodegenerate.ai.AiCodeGeneratorServiceFactory;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import com.aicodegenerate.core.parser.CodeParserExecutor;
import com.aicodegenerate.core.saver.CodeFileSaverExecutor;
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
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    public File generateCodeAndSave(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        //根据appId获取响应的AI服务实例（支持缓存）
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult mutilFileCodeResult = aiCodeGeneratorService.generateMutilFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(mutilFileCodeResult, codeGenTypeEnum, appId);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式: " + codeGenTypeEnum.getValue());
        };
    }

    public Flux<String> generateCodeAndSaveStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        //根据appId获取响应的AI服务实例（支持缓存）
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMutilFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式: " + codeGenTypeEnum.getValue());
        };
    }

    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        //字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder stringBuilder = new StringBuilder();
        return codeStream.doOnNext(stringBuilder::append)
                .doOnComplete(() -> {
                    try {
                        //流式返回完成后，保存代码
                        String completeCode = stringBuilder.toString();
                        //解析代码
                        Object parserResult = CodeParserExecutor.executeParser(completeCode, codeGenTypeEnum);
                        //保存代码
                        File file = CodeFileSaverExecutor.executeSaver(parserResult, codeGenTypeEnum, appId);
                        log.info("保存文件成功: {}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存文件失败: {}", e.getMessage());
                    }
                });
    }
}
