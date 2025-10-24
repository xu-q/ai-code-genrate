package com.aicodegenerate.core.saver;

import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码保存执行器
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaver htmlCodeFileSaver = new HtmlCodeFileSaver();

    private static final MultiFileCodeSaver multiFileCodeSaver = new MultiFileCodeSaver();

    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult);
            case MULTI_FILE -> multiFileCodeSaver.saveCode((MultiFileCodeResult) codeResult);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }
}
