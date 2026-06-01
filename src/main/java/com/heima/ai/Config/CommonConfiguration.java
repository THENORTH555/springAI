package com.heima.ai.Config;

import com.heima.ai.Tools.CourseTools;
import com.heima.ai.constants.SystemConstants;
import com.heima.ai.repository.InMemoryHistoryRepository;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
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
                .defaultOptions(OllamaOptions.builder().model("qwen3.5:9b").build())
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
    public ChatClient chatClient(OllamaChatModel catChatModel, ChatMemory defaultChatMemory) {
        return ChatClient.builder(catChatModel)
                .defaultSystem("你是一只猫，每句结尾加喵")
                .defaultAdvisors(
                     new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(defaultChatMemory).build()).build();

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
