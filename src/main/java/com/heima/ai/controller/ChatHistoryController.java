package com.heima.ai.controller;

import com.heima.ai.entity.vo.MessageVO;
import com.heima.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:ChatHistoryController
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/28 15:03
 * @Version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    private final ChatHistoryRepository chathistoryrepository;
    private final ChatMemory chatMemory;
    @GetMapping("/{type}")
    public List<String> getChatIds( @PathVariable("type") String type){
        return chathistoryrepository.getChatIds(type);
    }
    @GetMapping("/{type}/{chatId}")
    public List<MessageVO> getChatHisTory(@PathVariable("type") String type,
                                        @PathVariable("chatId") String chatId){
       List<Message> messages = chatMemory.get(chatId,10);
       if (messages ==null){
           return List.of();
       }
       return messages.stream().map(MessageVO::new).toList();
    }

}
