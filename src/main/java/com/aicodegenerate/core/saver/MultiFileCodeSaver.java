package com.aicodegenerate.core.saver;

import com.aicodegenerate.ai.model.MultiFileCodeResult;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

/**
 * 多文件模式代码保存器
 */
public class MultiFileCodeSaver extends CodeFileSaverTemplate<MultiFileCodeResult> {

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        wirteToFile(baseDirPath, "index.html", result.getHtmlCode());
        wirteToFile(baseDirPath, "style.css", result.getCssCode());
        wirteToFile(baseDirPath, "script.js", result.getJsCode());
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }
}
