package com.learn.mitprojecttest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        int port = 6379;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final OrderWebSocketHandler orderWebSocketHandler = new OrderWebSocketHandler();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 添加HTTP协议编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 添加HTTP对象聚合器，用于处理HTTP消息的聚合
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // 添加WebSocket协议处理器，用于处理WebSocket握手和帧的处理
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            // 添加自定义的WebSocket处理器
                            pipeline.addLast(orderWebSocketHandler);
                        }
                    });
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("WebSocket服务器已启动，端口号：" + port);


            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}

