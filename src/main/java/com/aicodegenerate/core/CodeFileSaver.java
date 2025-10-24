package com.aicodegenerate.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Deprecated
public class CodeFileSaver {

    /**
     * 文件保存根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output/";

    /**
     * 保存 HTML 模式生成的代码
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildFilePath(CodeGenTypeEnum.HTML.getValue());
        wirteToFile(baseDirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存多个文件
     */
    public static File saveMutilFileCodeResult(MultiFileCodeResult mutilFileCodeResult) {
        String baseDirPath = buildFilePath(CodeGenTypeEnum.MULTI_FILE.getValue());
        wirteToFile(baseDirPath, "index.html", mutilFileCodeResult.getHtmlCode());
        wirteToFile(baseDirPath, "style.css", mutilFileCodeResult.getCssCode());
        wirteToFile(baseDirPath, "script.js", mutilFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }


    /**
     * 构建文件的唯一路径（tmp/code_output/bizType_雪花ID）
     */
    private static String buildFilePath(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;

    }

    /**
     * 保存单个文件
     */
    private static void wirteToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
