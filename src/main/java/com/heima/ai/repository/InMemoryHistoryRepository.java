package com.heima.ai.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:InmemoryHistoryRepository
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/28 14:40
 * @Version 1.0
 */
@Component
public class InMemoryHistoryRepository implements ChatHistoryRepository {

        private final Map<String, List<String>> chatHistory = new HashMap<>();
    @Override
    public void save(String type, String chatID) {
//        if (!chatHistory.containsKey(type)){
//            chatHistory.put(type, new ArrayList<>());
//        }
//        List<String> chatIds = chatHistory.get(type);
        List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
        if (chatIds.contains(chatID)){
            return;
        }
        chatIds.add(chatID);
    }

    @Override
    public List<String> getChatIds(String type) {
        //getOrDefault()根据type去取chatidslist，有就返回没有就返回默认空集合
        return chatHistory.getOrDefault(type, List.of());

    }
}
