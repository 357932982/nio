package com.springcloud.webfluxdemo.reactor_demo;

public class ThreadDemo {
    public static void main(String[] args) {
        new Thread(() -> System.out.println("Hello WebFlux!")).start();
    }
}
