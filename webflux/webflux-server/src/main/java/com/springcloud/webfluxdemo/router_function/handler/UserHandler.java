package com.springcloud.webfluxdemo.router_function.handler;

import com.springcloud.webfluxdemo.springMVC.domain.User;
import com.springcloud.webfluxdemo.springMVC.repository.UserRepository;
import com.springcloud.webfluxdemo.utils.CheckUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class UserHandler {

    private final UserRepository repository;

    public UserHandler(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * 得到所有用户
     * @param request 请求
     * @return Users
     */
    public Mono<ServerResponse> getAllUser(ServerRequest request){
        return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(this.repository.findAll(), User.class);
    }

    /**
     * 增加用户
     * @param request 请求
     * @return 返回增加成功的用户信息
     */
    public Mono<ServerResponse> createUser(ServerRequest request){
        Mono<User> user = request.bodyToMono(User.class);

        return user.flatMap(u -> {
            CheckUtil.checkName(u.getName());
            return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(this.repository.save(u), User.class);
        });
    }

    /**
     * 根据id值删除用户
     * @param request 请求
     * @return 返回删除成功或没找到
     */
    public Mono<ServerResponse> deleteUser(ServerRequest request){
        String id = request.pathVariable("id");

        return this.repository.findById(id)
            .flatMap(user -> this.repository.delete(user)
                .then(ok().build()))
            .switchIfEmpty(notFound().build());
    }

}
