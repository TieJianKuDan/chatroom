package net.lemonplan.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Request {
    private String id;
    private String sendUserId;
    private String acceptUserId;
    private Date requestTime;
}
