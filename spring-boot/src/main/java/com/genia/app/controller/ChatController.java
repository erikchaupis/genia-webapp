package com.genia.app.controller;

import com.genia.app.tool.CommonTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"*"})
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private VectorStore vectorStore;

    Map<String, ChatClient> clients;

    public ChatController(@Qualifier("gemini") ChatClient geminiChatClient,
                          @Qualifier("vertex") ChatClient vertexChatClient,
                          @Qualifier("openai") ChatClient openaiChatClient) {
        clients = new HashMap<>();
        clients.put("openai", openaiChatClient);
        clients.put("gemini", geminiChatClient);
        clients.put("vertex", vertexChatClient);
    }

    @PostMapping
    public String chat(@RequestBody String message, @RequestParam String client) {
        log.debug("Received chat request: " + message + ", client: " + client);
        ChatClient chatClient = getChatClient(client);
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        log.debug("Chat response: " + response);
        return response;
    }

    private ChatClient getChatClient(String client) {
        if (client == null && !clients.containsKey(client)) {
            throw new IllegalStateException("No client found");
        }
        return clients.get(client);
    }

    @PostMapping("/rag")
    public String rag(@RequestBody String question, @RequestParam String client) {
        log.debug("Received chat request: " + question + ", client: " + client);
        ChatClient chatClient = getChatClient(client);
        Prompt prompt = getDomainTemplate().create(Map.of("question", question));
        String response = chatClient.prompt(prompt)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
        return response;
    }

    private PromptTemplate getDomainTemplate() {
        return PromptTemplate.builder().template("""
                You are a legal assistant specialized in US labor laws.
                Use the following context to answer the question precisely.
                If the answer is not in the context, say "I don't know". 
                But it the question is about some tool use the tool available

                Question:
                {question}

                Answer:
                """).build();
    }
}