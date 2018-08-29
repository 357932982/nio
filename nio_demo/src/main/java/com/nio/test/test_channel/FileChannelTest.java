package main.java.com.nio.test.test_channel;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class FileChannelTest {

    public static void main(String[] args) throws IOException {
        // 随机访问文件，指的是可以任意访问，访问任意位置
        // mode 的值可选 "r"：可读，"w" ：可写，"rw"：可读写；
        RandomAccessFile file = new RandomAccessFile("D:\\test_File.txt", "rw");
        FileChannel inChannel = file.getChannel();
        // 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1){
            System.out.println("Read: "+bytesRead);
            buf.flip(); // 切换读取数据的模式
            while (buf.hasRemaining()){
                System.out.print((char)buf.get());
            }
            buf.clear();
            bytesRead = inChannel.read(buf);
        }
        file.close();
    }

    /**
     * 读取文件中的内容，练习channel和ByteBuffer
     * @throws IOException
     */
    @Test
    public void bufferTest() throws IOException {
        RandomAccessFile file = new RandomAccessFile("D:/test_File.txt", "rw");
        RandomAccessFile file1 = new RandomAccessFile("D:/test_File1.txt", "rw");
        // 获取通道
        FileChannel channel = file.getChannel();
        FileChannel channel1 = file1.getChannel();
        // 获取缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (channel.read(buffer) != -1){
            buffer.flip();
            while (buffer.hasRemaining()){
                channel1.write(buffer);
//                System.out.print((char) buffer.get());

            }
            buffer.compact();
        }
        channel.close();
        file.close();
    }

    /**
     * 文件拷贝练习
     */
    @Test
    public void fileCopy() throws IOException {
        RandomAccessFile from = new RandomAccessFile("D:/from_file.txt", "rw");
        RandomAccessFile to = new RandomAccessFile("D:/to_file.txt", "rw");
        FileChannel fromChannel = from.getChannel();
        FileChannel toChannel = to.getChannel();

        long position = 0;
        long count = fromChannel.size();
        // 第一种方法
//        toChannel.transferFrom(fromChannel, position, count);
        //第二种方法
        fromChannel.transferTo(position, count, toChannel);
    }

    /**
     * Select 练习
     */
    public void testSelect() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));

        // 通过调用Selector.open()方法创建Selector
        Selector selector = Selector.open();

        // 向Selector注册通道
        // 注意register()方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型
        // Connect  SelectionKey.OP_CONNECT 连接就绪
        // Accept  SelectionKey.OP_ACCEPT  接收就绪
        // Read  SelectionKey.OP_READ  读就绪
        // Write  SelectionKey.OP_WRITE  写就绪
        socketChannel.configureBlocking(false);
//        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);

        // 判断是哪种模式
//        int interestSet = selectionKey.interestOps();
//        boolean isInterestedInAccept = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
//        boolean isInterestedInConnect = (interestSet & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT;
//        boolean isInterestedInRead    = (interestSet & SelectionKey.OP_READ) == SelectionKey.OP_READ;
//        boolean isInterestedInWrite   = (interestSet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;

        // 判断哪种就绪
//        boolean isAcceptable = selectionKey.isAcceptable();
//        boolean isConnectable = selectionKey.isConnectable();
//        boolean isReadable = selectionKey.isReadable();
//        boolean isWritable = selectionKey.isWritable();

        while (true){
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;
            //获取已注册的通道
            Set selectedKeys = selector.selectedKeys();
            Iterator keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = (SelectionKey) keyIterator.next();
                if (key.isAcceptable()){
                    // 接收就绪
                } else if (key.isConnectable()){
                    // 连接就绪
                } else if (key.isReadable()){
                    // 读就绪
                } else if (key.isWritable()){
                    // 写就绪
                }
                keyIterator.remove();
            }

        }

    }

    /**
     * 向FileChannel写数据
     */
    @Test
    public void writeFile() throws IOException {
        RandomAccessFile file = new RandomAccessFile("D:/test_write_file.txt", "rw");
        FileChannel fileChannel = file.getChannel();
        String data = "New String to wite, 你好。。。";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer.put(data.getBytes());
        buffer.flip();
        while(buffer.hasRemaining()){
            fileChannel.write(buffer);
        }
        fileChannel.close();
        file.close();
    }

    /**
     * SocketChannel练习（阻塞模式）
     */
    public void testSocketChannel()throws IOException{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));

        String message = "New String to write, 你好，小明。。。"+System.currentTimeMillis();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(buffer) != -1){
            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            while(buffer.hasRemaining()){
                socketChannel.write(buffer);
            }
        }
        socketChannel.close();
    }

    /**
     * SocketChannel练习（非阻塞模式）
     */
    public void testSocket2() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        // 改为非阻塞模式
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));

        String message = "hello world!";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (!socketChannel.finishConnect()){
            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        }
        socketChannel.close();
    }

    /**
     * ServerSocket练习
     */
    public void testServerSocket() throws IOException {

        //阻塞式
        /*
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8000));

        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            //阻塞式
        }
        */


        // 非阻塞式
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null){
                //非阻塞式
                
            }
        }


    }

    /**
     * DatagramChannel练习
     */
    @Test
    public void testDatagramServer() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.socket().bind(new InetSocketAddress(9000));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        SocketAddress receive = datagramChannel.receive(byteBuffer);
        System.out.println(receive);
//        byteBuffer.clear();
        // 收数据
//        while(datagramChannel.read(byteBuffer) != -1){
//            if (byteBuffer.hasRemaining()){
//                System.out.println((char)byteBuffer.get());
//            }
//        };

    }

    /**
     * DatagramChannel发送端
     */
    @Test
    public void testDatagrameClient() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.socket().connect(new InetSocketAddress("127.0.0.1", 9000));
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 发数据
        String newData = "New String to write to file..." + System.currentTimeMillis();
        buffer.clear();
        buffer.put(newData.getBytes());
        buffer.flip();
        System.out.println("Send: "+newData);
        datagramChannel.write(buffer);
        datagramChannel.close();


    }




}
