package com.learn.mitprojecttest;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class OrderWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Data
    @ToString
    private static class OperateAndMessage {
        String operateType;
        String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OrderEntity {
        String orderId;
        String message;
    }

    private static final Map<String, Channel> userChannels = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 处理客户端发送的消息
        String message = msg.text();
        OperateAndMessage operateAndMessage = JSONObject.parseObject(message, OperateAndMessage.class);
        System.out.println(operateAndMessage);
        if ("create".equals(operateAndMessage.getOperateType())) {
            userChannels.put(operateAndMessage.getMessage(), ctx.channel());
        } else if ("close".equals(operateAndMessage.getOperateType())) {
            userChannels.remove(operateAndMessage.getMessage());
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 处理客户端连接建立
        Channel channel = ctx.channel();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 处理客户端连接断开
        Channel channel = ctx.channel();
//         ctx.read().
    }

    public void notifyUser(String orderId) {
        Channel channel = userChannels.get(orderId);
        if (channel == null) {
            System.out.println("orderId: " + orderId + " doesnt exist");
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(new OrderEntity(orderId, UUID.randomUUID().toString()))));
    }
}

