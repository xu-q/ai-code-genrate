package com.aicodegenerate.core;

import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateCodeAndSave() {
        File file = aiCodeGeneratorFacade.generateCodeAndSave("生成一个登录界面，不超过20行代码", CodeGenTypeEnum.MULTI_FILE);
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getName());
        System.out.println(file.getPath());
        Assertions.assertTrue(file.exists());
    }
}