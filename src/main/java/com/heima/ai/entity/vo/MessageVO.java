package com.heima.ai.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

import java.awt.*;

/**
 * ClassName:MessageVO
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/28 16:03
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class MessageVO {
    private String role;
    private String content;

    public MessageVO(Message message){
        switch (message.getMessageType()){
            case USER:
                role = "user";
                break;
            case ASSISTANT:
                role = "assistant";
                break;
            default:
              role = "";
              break;
        }
      this.content = message.getText();
    }


}
