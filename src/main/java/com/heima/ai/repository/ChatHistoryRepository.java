package com.heima.ai.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName:chathistoryrepository
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/28 14:38
 * @Version 1.0
 */

public interface ChatHistoryRepository {
    void save(String type ,String chatID);

    List<String> getChatIds(String type);
}
