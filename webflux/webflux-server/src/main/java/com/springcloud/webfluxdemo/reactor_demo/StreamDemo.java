package com.springcloud.webfluxdemo.reactor_demo;


import java.util.stream.IntStream;

public class StreamDemo {
    public static void main(String[] args) {
        int[] nums = {1,2,3};
        //外部迭代
        int sum = 0;
        for (int i : nums){
            sum += i;
        }
        System.out.println("结果为： "+sum);

        // 使用stream内部迭代
        // map是中间操作(返回stream的操作)
        // sum就是终止操作
        int sum1 = IntStream.of(nums).map(i -> i*2).sum();
        System.out.println("结果为： "+sum1);


    }
}
