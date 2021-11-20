package net.lemonplan.netty;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import net.lemonplan.pojo.Envelope;

import java.util.Map;

@Slf4j
class TextWebSocketFrameChannelInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Gson gson = new Gson();
    /**
     * 记录用户和对应的channel
     */
    private Map<String, Channel> map;

    private String userId;

    public TextWebSocketFrameChannelInboundHandler(Map<String, Channel> map) {
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Envelope envelope = gson.fromJson(textWebSocketFrame.text(), Envelope.class);
        if (envelope.getAction() == 1) {
            // 保存连接
            map.put(envelope.getChatMsg().getSenderId(), channelHandlerContext.channel());
            this.userId = envelope.getChatMsg().getSenderId();
        } else if (envelope.getAction() == 2){
            // 转发聊天
            String receiverId = envelope.getChatMsg().getReceiverId();
            Channel channel = map.get(receiverId);
            if (channel == null) {
                // 保存消息至数据库
                log.debug("保存消息至数据库");
            } else {
                // 直接转发(textWebSocketFrame属于临界区内容，所以new一个新的)
                TextWebSocketFrame frame = new TextWebSocketFrame(textWebSocketFrame.text());
                channel.writeAndFlush(frame);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug(ctx.channel().id().asShortText() + "断开连接，对应的用户id是：" + userId);
        map.remove(userId);
        ChannelFuture future = ctx.channel().close();
        future.addListener(future1 -> log.debug("用户" + userId + "已断开连接"));
    }
}
