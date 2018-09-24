package com.springcloud.webfluxdemo.reactor_demo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class StreamDemo4 {

    public static void main(String[] args) {

        // IntStream.range(1, 100).peek(StreamDemo4::debug).count();
        // 调用parallel产生一个并行流
        // IntStream.range(1, 100).parallel().peek(StreamDemo4::debug).count();

        // 多次调用 parallel（并行）/sequential（串行），以最后一次调用为准。
//        IntStream.range(1, 100)
//                // 调用parallel产生并行流
//                .parallel().peek(StreamDemo4::debug)
//                .sequential().peek(StreamDemo4::debug1)
//                .count();

//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
//        IntStream.range(1, 100).parallel().peek(StreamDemo4::debug).count();

        // 使用自己的线程池，不使用默认的，防止任务被阻塞
        ForkJoinPool pool = new ForkJoinPool(10);
        pool.submit(() -> IntStream.range(1, 100).parallel().peek(StreamDemo4::debug).count());
        pool.shutdown();

        synchronized (pool){
            try {
                pool.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    public static void debug(int i){
        System.out.println(Thread.currentThread().getName()+" debug "+ i);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void debug1(int i){
        System.err.println(Thread.currentThread().getName()+" debug "+ i);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
