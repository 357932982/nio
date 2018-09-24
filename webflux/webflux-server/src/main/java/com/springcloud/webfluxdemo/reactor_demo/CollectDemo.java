package com.springcloud.webfluxdemo.reactor_demo;

import java.util.*;
import java.util.stream.Collectors;

class Student{
    private String name;
    private Integer age;
    private Gender gender;
    private Grade grade;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Student() {
    }

    public Student(String name, Integer age, Gender gender, Grade grade) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", grade=" + grade +
                '}';
    }
}

enum Gender{
    MALE, FEMALE;
}

enum Grade{
    ONE, TWO, TREE, FOUR;
}

public class CollectDemo {

    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("小明", 10, Gender.FEMALE, Grade.FOUR),
                new Student("小明1", 11, Gender.MALE, Grade.ONE),
                new Student("小明2", 13, Gender.FEMALE, Grade.FOUR),
                new Student("小明3", 9, Gender.MALE, Grade.TREE),
                new Student("小明4", 9, Gender.MALE, Grade.TREE)
        );

        //得到所有学生的年龄列表
        //s -> s.getAge() --> Student::getAge,将lambda表达式改成方法引用，不会产生一个类似lambda$0的函数
        List<Integer> ages = students.stream().map(Student::getAge).collect(Collectors.toList());
        System.out.println("所有学生的年龄： "+ages);

        System.out.println("----------");
        Set<Integer> ages1 = students.stream().map(Student::getAge).collect(Collectors.toSet());
//        Set<Integer> ages1 = students.stream().map(Student::getAge).collect(Collectors.toCollection(TreeSet::new));
        System.out.println("所有学生的年龄： "+ages1);

        //统计汇总
        IntSummaryStatistics agesSummaryStatistics = students.stream()
                .collect(Collectors.summarizingInt(Student::getAge));
        System.out.println("年龄汇总信息："+ agesSummaryStatistics);

        //分块
        Map<Boolean, List<Student>> genders = students.stream().collect(Collectors.partitioningBy(s -> s.getGender() == Gender.MALE));
        System.out.println("男女生列表："+ genders);


    }
}

