package com.springcloud.webfluxdemo.reactor_demo;

@FunctionalInterface
interface InterfaceDemo{
    int doubleNum(int i);

    default int add(int x, int y){
        return x+y;
    }

}

public class LambdaDemo {

    public static void main(String[] args) {
        InterfaceDemo i1 = (i) -> i*2;

        System.out.println(i1.add(3, 7));
        System.out.println(i1.doubleNum(2));

        InterfaceDemo i2 = i -> i*2; //常用方法

        InterfaceDemo i3 = (int i) -> i*2;

        InterfaceDemo i4 = (int i) -> {
            return i*2;
        };

    }
}
