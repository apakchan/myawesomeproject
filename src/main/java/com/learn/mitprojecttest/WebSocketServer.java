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

public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
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
                            pipeline.addLast(new WebSocketHandler());
                        }
                    });

            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("WebSocket服务器已启动，端口号：" + port);

            // 可以在需要的时候主动向前端发送通知消息
            sendNotificationToAllClients(channel);

            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static void sendNotificationToAllClients(Channel channel) {
        // 模拟向所有连接的客户端发送通知消息
        String notification = "这是一个来自服务器的通知消息";
        WebSocketFrame frame = new TextWebSocketFrame(notification);
        channel.writeAndFlush(frame);
    }
}

