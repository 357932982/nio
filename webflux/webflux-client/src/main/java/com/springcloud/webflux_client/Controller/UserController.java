package com.springcloud.webflux_client.Controller;

import com.springcloud.webflux_client.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class UserController {

    @Autowired
    IUserApi userApi;

    @GetMapping("/")
    public void getAll(){
        Flux<User> users = userApi.getAllUser();
        users.subscribe(System.out::println);
    }
}
