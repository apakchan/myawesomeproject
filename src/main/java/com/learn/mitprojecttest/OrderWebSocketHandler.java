package com.learn.mitprojecttest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class OrderWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 处理从客户端接收的WebSocket消息
        String message = msg.text();
        System.out.println("收到消息：" + message);

        // 模拟服务器向客户端发送通知
        String notification = "来自服务器的新通知！";
        ctx.channel().writeAndFlush(new TextWebSocketFrame(notification));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

