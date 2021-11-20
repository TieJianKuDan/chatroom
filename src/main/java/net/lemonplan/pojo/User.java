package net.lemonplan.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * 用户信息类
 */
@Data
@ToString
public class User {
    private String id;
    private String username;
    private String password;
    private String faceImage;
    private String faceImageBig;
    private String nickname;
    private String qrcode;
    private String cid;
}
