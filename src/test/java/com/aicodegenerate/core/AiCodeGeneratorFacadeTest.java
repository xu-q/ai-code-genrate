package com.aicodegenerate.core;

import com.aicodegenerate.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateCodeAndSave() {
        File file = aiCodeGeneratorFacade.generateCodeAndSave("生成一个登录界面，不超过20行代码", CodeGenTypeEnum.MULTI_FILE, 1111L);
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getName());
        System.out.println(file.getPath());
        Assertions.assertTrue(file.exists());
    }

    @Test
    void generateCodeAndSaveStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateCodeAndSaveStream("生成一个登录界面，不超过20行代码", CodeGenTypeEnum.MULTI_FILE, 1111L);
        //阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String completeContent = String.join("\n", result);
        Assertions.assertNotNull(completeContent);
    }
}