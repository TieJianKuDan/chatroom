package net.lemonplan.pojo;

import lombok.Data;

@Data
public class Envelope {
    private int action;
    private ChatMsg chatMsg;
}
