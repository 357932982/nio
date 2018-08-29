package netty_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        // 1.绑定两个线程组，分别用来处理客户端通道的accept和读写
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        server.group(parentGroup, childGroup);

        // 2.绑定服务端通道NioServerSocketChannel
        server.channel(NioServerSocketChannel.class);

        // 3.给读写事件绑定handler去处理读写
        server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new SimpleServerHandler());
            }
        });

        // 4.监听端口（同步）
        ChannelFuture cf = server.bind(8000).sync();
        // 当通道关闭后，关闭监听端口
        cf.channel().closeFuture().sync();
    }
}
