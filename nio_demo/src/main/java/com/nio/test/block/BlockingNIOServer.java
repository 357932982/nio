package main.java.com.nio.test.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingNIOServer {
    private int port = 8000;
    private ServerSocketChannel serverSocketChannel = null;
    private ExecutorService executorService = null;
    private static int DEFAULT_POOI_SIZE = 4;

    public BlockingNIOServer() throws IOException {
        super();
        this.executorService = Executors.newFixedThreadPool(DEFAULT_POOI_SIZE
                * Runtime.getRuntime().availableProcessors());
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.executorService = executorService;
    }

    public void service() {
        while (true) {
            SocketChannel channel = null;
            try {
                channel = serverSocketChannel.accept();
                executorService.execute(new Handler(channel));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new BlockingNIOServer().service();
    }

    private class Handler implements Runnable {
        private SocketChannel channel;
        public Handler(SocketChannel channel) {
            super();
            this.channel = channel;
        }

        @Override
        public void run() {
            handler(channel);
        }

        public void handler(SocketChannel channel) {
            Socket socket = null;
            try {
                socket = channel.socket();
                System.out.println("接收到来自：" + socket.getInetAddress() + " 端口："
                        + socket.getPort() + "的请求");
                BufferedReader bufferedReader = NIOUtil
                        .getBufferedReader(socket);
                PrintWriter printWriter = NIOUtil.getPrintWriter(socket);
                String msg = null;

                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println(msg);
                    printWriter.println(Echo(msg));
                    if ("bye".equalsIgnoreCase(msg))
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        private String Echo(String msg) {
            return "ECHO:" + msg;
        }
    }
}