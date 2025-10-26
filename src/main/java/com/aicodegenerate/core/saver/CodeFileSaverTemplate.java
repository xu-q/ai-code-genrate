package com.aicodegenerate.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aicodegenerate.constant.AppConstant;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import com.aicodegenerate.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存器模板
 */
public abstract class CodeFileSaverTemplate<T> {

    public final File saveCode(T t, Long appId) {
        //1.验证输入
        virifyInput(t);
        //2.构建文件的唯一路径（tmp/code_output/bizType_雪花ID）
        String baseDirPath = buildFilePath(appId);
        //3.保存文件(由子类去实现)
        saveFiles(t, baseDirPath);
        //4.返回文件
        return new File(baseDirPath);
    }

    /**
     * 构建文件的唯一路径（tmp/code_output/bizType_雪花ID）
     */
    private String buildFilePath(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appId不能为空");
        }
        String codeType = getCodeType().getValue();
        //String uniqueDirName = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextIdStr());
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 验证输入
     */
    private void virifyInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成代码结果对象不能为空");
        }
    }

    /**
     * 保存文件
     */
    protected abstract void saveFiles(T result, String baseDirPath);

    /**
     * 获取代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存单个文件
     */
    public final void wirteToFile(String dirPath, String filename, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + filename;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }
}
