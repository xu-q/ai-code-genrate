package com.aicodegenerate.ai;

import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    @SystemMessage(fromResource = "prompt/mutil-file-system-prompt.txt")
    MultiFileCodeResult generateMutilFileCode(String userMessage);

    @SystemMessage(fromResource = "prompt/html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/mutil-file-system-prompt.txt")
    Flux<String> generateMutilFileCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    Flux<String> generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);
}
