package main.java.com.nio.test.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NoBlockingServer {
    private int port = 8000;
    private ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;

    public NoBlockingServer() throws IOException {
        super();
        selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.configureBlocking(false);//设置为非阻塞
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("服务器启动成功");
    }

    public void service() throws IOException {
        //给serverSocketChannel注册OP_ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //注意selector.select()将会阻塞
        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = null;
                try {
                    selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();

                    if (selectionKey.isAcceptable()) {
                        dealWithAcceptable(selectionKey);
                    }
                    if (selectionKey.isReadable()) {
                        dealWithReadable(selectionKey);
                    }
                    if (selectionKey.isWritable()) {
                        dealWithWritable(selectionKey);
                    }
                } catch (Exception e) {
                    if (selectionKey != null) {
                        selectionKey.cancel();
                        selectionKey.channel().close();
                    }
                }
            }
        }
    }

    private void dealWithAcceptable(SelectionKey selectionKey)
            throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey
                .channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("接收到来自：" + socketChannel.socket().getInetAddress()
                + " 端口" + socketChannel.socket().getPort() + "的请求");
        socketChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ
                | SelectionKey.OP_WRITE, buffer);
    }

    private void dealWithReadable(SelectionKey selectionKey) throws IOException{
        ByteBuffer buffer=(ByteBuffer) selectionKey.attachment();
        SocketChannel channel=(SocketChannel) selectionKey.channel();
        ByteBuffer readBuffer=ByteBuffer.allocate(32);
        channel.read(readBuffer);
        readBuffer.flip();

        buffer.limit(buffer.capacity());
        buffer.put(readBuffer);
    }

    private void dealWithWritable(SelectionKey selectionKey) throws IOException{
        ByteBuffer buffer=(ByteBuffer) selectionKey.attachment();
        SocketChannel channel=(SocketChannel) selectionKey.channel();
        buffer.flip();

        String msg=CharSetUtil.decode(buffer, "UTF-8");

        if(msg.indexOf("\r\n")==-1){
            return;
        }

        String outPutData=msg.substring(0, msg.indexOf("\n")+1);
        System.out.println("接收来自客户端的数据："+outPutData);

        ByteBuffer outbyteBuffer=CharSetUtil.encode("echo:"+outPutData, "UTF-8");
        while (outbyteBuffer.hasRemaining()) {
            channel.write(outbyteBuffer);
        }

        ByteBuffer tmp=CharSetUtil.encode(outPutData, "UTF-8");
        buffer.position(tmp.limit());
        buffer.compact();
        if("bye\r\n".equalsIgnoreCase(outPutData)){
            selectionKey.cancel();
            channel.close();
            System.out.println("关闭与客户端的连接");
        }
    }

    public static void main(String[] args) throws Exception {
        new NoBlockingServer().service();
    }
}
