package com.aicodegenerate.core.saver;

import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

/**
 * HTML 模式代码保存器
 */
public class HtmlCodeFileSaver extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        wirteToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    public CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }
}