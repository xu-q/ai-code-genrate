package com.aicodegenerate.ai;

import com.aicodegenerate.ai.guardrail.PromptSafetyInputGuardrail;
import com.aicodegenerate.ai.guardrail.RetryOutputGuardrail;
import com.aicodegenerate.ai.model.HtmlCodeResult;
import com.aicodegenerate.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.guardrail.OutputGuardrails;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    @SystemMessage(fromResource = "prompt/mutil-file-system-prompt.txt")
    MultiFileCodeResult generateMutilFileCode(String userMessage);

    //@InputGuardrails(PromptSafetyInputGuardrail.class)
    //@OutputGuardrails(RetryOutputGuardrail.class)
    @SystemMessage(fromResource = "prompt/html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/mutil-file-system-prompt.txt")
    Flux<String> generateMutilFileCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);
}
