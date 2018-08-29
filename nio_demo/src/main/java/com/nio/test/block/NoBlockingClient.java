package main.java.com.nio.test.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NoBlockingClient {
    private SocketChannel channel = null;
    private ByteBuffer send = ByteBuffer.allocate(1024);
    private ByteBuffer rece = ByteBuffer.allocate(1024);
    private Selector selector;

    public NoBlockingClient() throws IOException {
        super();
        channel = SocketChannel.open();
        channel.socket().connect(new InetSocketAddress("localhost", 8000));
        channel.configureBlocking(false);

        System.out.println("与服务器建立连接成功");
        selector = Selector.open();
    }

    public void talk() throws IOException {
        channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = null;
                try {
                    selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();

                    if (selectionKey.isReadable()) {
                        dealWithReadable(selectionKey);
                    }
                    if (selectionKey.isWritable()) {
                        dealWithWritable(selectionKey);
                    }
                } catch (Exception e) {
                    if (selectionKey != null) {
                        selectionKey.cancel();
                        try {
                            selectionKey.channel().close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void receFromUser() throws IOException{
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        String msg=null;
        while ((msg=bufferedReader.readLine())!=null) {
            synchronized (send) {
                send.put(CharSetUtil.encode(msg+"\r\n", "UTF-8"));
            }
            if ("bye".equalsIgnoreCase(msg)) {
                break;
            }
        }
    }

    private void dealWithWritable(SelectionKey selectionKey) throws IOException{
        SocketChannel channel=(SocketChannel) selectionKey.channel();
        synchronized (send) {
            send.flip();
            channel.write(send);
            send.compact();
        }
    }

    private void dealWithReadable(SelectionKey selectionKey) throws IOException{
        SocketChannel channel=(SocketChannel) selectionKey.channel();
        channel.read(rece);
        rece.flip();
        String msg=CharSetUtil.decode(rece, "UTF-8");

        if(msg.indexOf("\r\n")==-1){
            return;
        }

        String outPutData=msg.substring(0, msg.indexOf("\n")+1);
        System.out.println(outPutData);

        if("echo:bye\r\n".equalsIgnoreCase(outPutData)){
            selectionKey.cancel();
            channel.close();
            selector.close();
            System.out.println("关闭与客户端的连接");
        }

        ByteBuffer tmp=CharSetUtil.encode(outPutData, "UTF-8");
        rece.position(tmp.limit());
        rece.compact();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("file.encoding"));
        final NoBlockingClient noBlockingClient=new NoBlockingClient();
        Thread thread=new Thread(){
            public void run() {
                try {
                    noBlockingClient.receFromUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        };

        thread.start();
        noBlockingClient.talk();
    }
}