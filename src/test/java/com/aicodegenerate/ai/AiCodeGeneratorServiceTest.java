package com.aicodegenerate.ai;

import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("生成一个程序员徐庆的博客，不超过20行");
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMutilFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMutilFileCode("生成一个程序员徐庆的留言板，不超过50行");
        Assertions.assertNotNull(result);
    }
}