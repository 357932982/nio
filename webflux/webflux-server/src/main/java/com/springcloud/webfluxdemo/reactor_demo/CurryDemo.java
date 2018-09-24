package com.springcloud.webfluxdemo.reactor_demo;

import java.util.function.Function;

/**
 * 级联表达式和柯里化
 * 柯里化：把多个参数的函数转换为只有一个参数的函数
 * 柯里化的目的：函数标准化
 */
public class CurryDemo {
    public static void main(String[] args) {
        Function<Integer, Function<Integer, Integer>> function = x ->y -> x + y;
        System.out.println(function.apply(2).apply(3));
    }
}
