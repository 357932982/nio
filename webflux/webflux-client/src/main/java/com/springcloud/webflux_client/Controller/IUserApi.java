package com.springcloud.webflux_client.Controller;

import com.springcloud.webflux_client.vo.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ApiServer(value = "http://localhost:8080/user")
public interface IUserApi {

    @GetMapping("/")
    Flux<User> getAllUser();

    @GetMapping("/{id}")
    Mono<User> getUserById(@PathVariable("id") String id);

    @DeleteMapping("/{id}")
    Mono<Void> deleteUserById(@PathVariable("id") String id);

    @PostMapping("/")
    Mono<User> createUser(@RequestBody Mono<User> user);

}
