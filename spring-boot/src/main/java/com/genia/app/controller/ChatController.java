package com.genia.app.controller;

import com.genia.app.tool.CommonTool;
import com.genia.app.vector.LuceneVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173/", "https://genia-webapp.onrender.com/"})
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    private LuceneVectorStore vectorStore;

    public ChatController(ChatModel chatModel, CommonTool commonTool) {
        chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(getTools(commonTool))
                .defaultAdvisors(getInMemoryChatAdvisor())
                .build();
    }

    private List<ToolCallback> getTools(CommonTool commonTool) {
        MethodToolCallbackProvider provider = MethodToolCallbackProvider.builder().toolObjects(commonTool).build();
        return List.of(provider.getToolCallbacks());
    }

    private MessageChatMemoryAdvisor getInMemoryChatAdvisor() {
        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @PostMapping
    public String chat(@RequestBody String message) {
        log.debug("Received chat request: " + message);

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        log.debug("Chat response: " + response);
        return response;
    }

    @PostMapping("/rag")
    public String rag(@RequestBody String question) {
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