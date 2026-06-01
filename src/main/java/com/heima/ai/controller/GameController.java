package com.heima.ai.controller;

import com.heima.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * ClassName:GameController
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/29 0:33
 * @Version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class GameController {
    private final  ChatClient girlfriendChatClient;

    private final ChatMemory chatMemory;



    @RequestMapping(value = "/game",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam String prompt,
                             @RequestParam(defaultValue = "default-chat") String chatId){

        //2，请求模型
        return girlfriendChatClient.prompt()
                .user(prompt)
                .advisors(advisor -> advisor.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();

    }
}
