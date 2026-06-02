package com.heima.ai.Config;

import com.heima.ai.Tools.CourseTools;
import com.heima.ai.constants.SystemConstants;
import com.heima.ai.repository.InMemoryHistoryRepository;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;



/**
 * ClassName:CommonConfiguration
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/26 18:50
 * @Version 1.0
 */
@Configuration
public class CommonConfiguration {

    //创建一个会话记忆bean存储会话记忆
    // 通用对话记忆：最多保留10条消息
    // 通用对话记忆
    @Bean("defaultChatMemory")
    public ChatMemory defaultChatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public OllamaApi ollamaApi() {
        // 只写根地址！！！
        return new OllamaApi("http://localhost:11434");
    }

    @Bean
    public OllamaChatModel girlfriendChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model("qwen3.5:2b").build())
                .build();
    }
    @Bean
    public OllamaChatModel catChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model("deepseek-r1:1.5b").build())
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel girlfriendChatModel, ChatMemory defaultChatMemory) {
        return ChatClient.builder(girlfriendChatModel)
                .defaultSystem("你是一个助手，请根据上下文回答问题，遇到上下文没有的问题不要随便编造")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(defaultChatMemory).build())
                .build();

    }
    @Bean
    public ChatClient pdfClient(OllamaChatModel catChatModel, ChatMemory defaultChatMemory,VectorStore vectorStore) {
        return ChatClient.builder(catChatModel)
                .defaultSystem("请根据上下文回答问题，遇到上下文没有的问题不要随便编造")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(
                                vectorStore,
                                SearchRequest.builder()
                                        .similarityThreshold(0.6)
                                        .topK(2)
                                        .build()
                        ),
                        MessageChatMemoryAdvisor.builder(defaultChatMemory)
                                .build()).build();

    }
    @Bean
    public ChatClient ServiceClient(OllamaChatModel girlfriendChatModel, ChatMemory chatMemory, CourseTools courseTools) {
        return ChatClient.builder(girlfriendChatModel )
                .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT)
                .defaultTools(courseTools)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

    }
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel openAiEmbeddingModel) {
        return SimpleVectorStore.builder(openAiEmbeddingModel).build();
    }

//    // -------------------------- 4. 女友专用ChatClient（新增） --------------------------
//    @Bean
//    public ChatClient girlfriendChatClient(OllamaChatModel girlfriendChatModel, ChatMemory girlfriendChatMemory) {
//        return ChatClient.builder(girlfriendChatModel)
//                // 女友人设System Prompt
//                .defaultSystem(SystemConstants.GIRL_FRIEND_GAME_PROMPT)
//                // 强制指定女友模型：qwen2:1.5b
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor(),
//                        MessageChatMemoryAdvisor.builder(girlfriendChatMemory).build()).build();
//
//
//    }
    // -------------------------- 4.1 女友专用ChatClient（2号，使用千问3） --------------------------
    @Bean
    public ChatClient girlfriendChatClient(OllamaChatModel catChatModel, ChatMemory defaultChatMemory) {
        return ChatClient.builder(catChatModel)
                // 女友人设System Prompt
                .defaultSystem(SystemConstants.GIRL_FRIEND_GAME_PROMPT)
                // 强制指定女友模型：deepseek-r1
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(defaultChatMemory)
                                .build()).build();


    }
}
