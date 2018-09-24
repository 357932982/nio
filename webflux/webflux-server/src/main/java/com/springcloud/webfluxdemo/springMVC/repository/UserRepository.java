package com.springcloud.webfluxdemo.springMVC.repository;

import com.springcloud.webfluxdemo.springMVC.domain.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    /**
     * 根据年龄查询用户
     * @param start 起始年龄
     * @param end 结束年龄
     * @return user
     */
    Flux<User> findByAgeBetween(int start, int end);

    /**
     * 自动以查找，包含头尾
     * @param start 开始年龄
     * @param end 结束年龄
     * @return user
     */
    @Query("{'age':{'$gte': ?0, '$lte': ?1}}")
    Flux<User> findAgeIn(int start, int end);
}
