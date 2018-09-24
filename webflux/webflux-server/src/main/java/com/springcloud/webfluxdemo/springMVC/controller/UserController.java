package com.springcloud.webfluxdemo.springMVC.controller;

import com.springcloud.webfluxdemo.springMVC.domain.User;
import com.springcloud.webfluxdemo.springMVC.repository.UserRepository;
import com.springcloud.webfluxdemo.utils.CheckUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository){
        this.repository = repository;
    }

    /**
     * 以数组形式一次性返回数据
     */
    @GetMapping("/")
    public Flux<User> getAll(){
        return repository.findAll();
    }

    /**
     * 使用sse方式获取数据
     */
    @GetMapping(value = "/stream/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamGetAll(){
        return repository.findAll();
    }

    /**
     * 新增数据
     */
    @PostMapping("/")
    public Mono<User> createUser(@Valid @RequestBody User user){
        user.setId(null);
        CheckUtil.checkName(user.getName());
        return this.repository.save(user);
    }

    /**
     * 根据id值删除用户
     * 存在的时候返回200，不存在返回404
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("id") String id){

        //deleteByID没有返回值。不能判断是否有数据存在
        //this.repository.deleteById(id);
        return this.repository.findById(id)
            //当要操作数据，并返回一个Mono这个时候使用flatMap
            //如果不操作数据，只转换数据，使用map
            .flatMap(user -> this.repository.delete(user)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable("id") String id,
            @Valid @RequestBody User user){
        CheckUtil.checkName(user.getName());
        return this.repository.findById(id)
                //flatMap操作数据
                .flatMap(u -> {
                    u.setAge(user.getAge());
                    u.setName(user.getName());
                    return this.repository.save(u);
                })
                //map转换数据
                .map(u -> new ResponseEntity<User>(u, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findUserById(@PathVariable("id") String id){
        return this.repository.findById(id).map(user -> new ResponseEntity<User>(user, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据年龄查找用户(一次性返回)
     */
    @GetMapping("/age/{start}/{end}")
    public Flux<User> findByAge(@PathVariable("start") int start,
                                @PathVariable("end") int end){
        return this.repository.findByAgeBetween(start, end);
    }

    /**
     * 根据年龄查找用户（流的形式返回）
     */
    @GetMapping(value = "/stream/age/{start}/{end}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamFindByAge(@PathVariable("start") int start,
                                @PathVariable("end") int end){
        return this.repository.findByAgeBetween(start, end);
    }



    /**
     * 根据年龄自定义方法查找用户(一次性返回)
     */
    @GetMapping("/age1/{start}/{end}")
    public Flux<User> findAgeIn(@PathVariable("start") int start,
                                @PathVariable("end") int end){
        return this.repository.findAgeIn(start, end);
    }

    /**
     * 根据年龄自定义方法查找用户（流的形式返回）
     */
    @GetMapping(value = "/stream/age1/{start}/{end}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamFindAgeIn(@PathVariable("start") int start,
                                      @PathVariable("end") int end){
        return this.repository.findAgeIn(start, end);
    }



}
