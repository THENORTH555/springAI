package com.heima.ai.controller;

import com.heima.ai.repository.ChatHistoryRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

import org.springframework.ai.model.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:ChatController
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/26 18:53
 * @Version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

   private final ChatMemory chatMemory;

   private final ChatHistoryRepository chathistoryrepository;


    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                             @RequestParam(defaultValue = "default-chat") String chatId,
                             @RequestParam(value = "files",required = false)List<MultipartFile>  files){


        //1,保存会话id
        chathistoryrepository.save("chat", chatId);

        if (files == null|| files.isEmpty()){
            return textChat(prompt,chatId);
        }else {
            return multiModalChat( prompt, chatId,files);
        }
    }

    private Flux<String> multiModalChat(String prompt, String chatId, List<MultipartFile> files) {
        //解析多媒体文件方法
        List<Media> medias = files.stream().map(file->new Media(MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                file.getResource()
                )
        )
                .toList();

// 2. 请求模型
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();

    }

    private Flux<String> textChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }

}
