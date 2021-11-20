package net.lemonplan.pojo;

import lombok.Data;

@Data
public class ChatMsg {
    private String senderId;
    private String receiverId;
    private String content;
}
