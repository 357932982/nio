package netty_demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (msg instanceof ByteBuf){
            System.out.println(((ByteBuf) msg).readableBytes());
            byte[] butfs = new byte[((ByteBuf) msg).readableBytes()];
            System.out.println(new String(butfs, "utf-8"));
        }

        ctx.channel().writeAndFlush("is ok");
    }


}
