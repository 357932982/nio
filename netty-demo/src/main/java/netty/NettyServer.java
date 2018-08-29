package netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class NettyServer {

    private final int port ;

    public NettyServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 1){
//            System.err.println("Usage: "+NettyServer.class.getSimpleName() + "<port>");
//            return;
//        }
//        int port = Integer.parseInt(args[0]);
        new NettyServer(8000).start();
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstarp = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstarp.group(group) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("initChannel ch: "+ socketChannel);
                            socketChannel.pipeline()
                                    .addLast("decoder", new HttpRequestDecoder())
                                    .addLast("encoder", new HttpResponseEncoder())
                                    .addLast("aggregator", new HttpObjectAggregator(512*1024))
                                    .addLast("handler", new HttpHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            ChannelFuture cf = bootstarp.bind(port).sync(); // 服务器异步创建绑定
            System.out.println(NettyServer.class + " started and listen on " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道,当得到客户端关闭信息后关闭连接
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }


    }
}
