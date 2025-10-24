package com.aicodegenerate.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;


@Description("生成多个代码文件的结果")
@Data
public class MultiFileCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("css代码")
    private String cssCode;

    @Description("js代码")
    private String jsCode;

    @Description("生成的代码描述")
    private String description;
}
