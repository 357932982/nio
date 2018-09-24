package com.springcloud.webfluxdemo.reactor_demo;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class Dog{
    private String name = "大黄";

    private int food = 10;

    public Dog() {
    }

    public Dog(String name) {
        this.name = name;
    }

    public static void bark(Dog dog){
        System.out.println(dog + "叫了");
    }

    public int eat(int num){
        System.out.println("吃了"+ num + "斤狗粮");
        this.food -= num;
        return this.food;
    }

    @Override
    public String toString() {
        return this.name = name;
    }
}

public class MethodRetrenceDemo {
    public static void main(String[] args) {
        // 方法引用
        Consumer<String> consumer = System.out::println;
        consumer.accept("测试方法引用");

        // 静态方法的引用
        Consumer<Dog> consumer1 = Dog::bark;
        consumer1.accept(new Dog());

        // 非静态方法的引用,使用对象实例的方法引用
        Dog dog = new Dog();
        Function<Integer, Integer> function = dog::eat;
        System.out.println("还剩下"+ function.apply(2) +"斤狗粮");

        // 使用类名来引用非静态方法
        BiFunction<Dog, Integer, Integer> eatFunction = Dog::eat;
        System.out.println("还剩下"+eatFunction.apply(dog,2)+"斤狗粮");

        // 构造方法的引用
        Supplier<Dog> supplier = Dog::new;
        System.out.println("创建了新对象：" + supplier.get());

        // 构造方法（带参的）
        Function<String, Dog> function1 = Dog::new;
        System.out.println("创建了新对象："+ function1.apply("旺财"));

    }
}
