package com.springcloud.webfluxdemo.router_function.routers;

import com.springcloud.webfluxdemo.router_function.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AllRouters {

    @Bean
    RouterFunction<ServerResponse> userRouter(UserHandler handler){
        return nest(
                path("/router/user"),
                route(GET("/"), handler::getAllUser)
                    .andRoute(POST("/").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::createUser)
                    .andRoute(DELETE("/{id}"), handler::deleteUser)
        );
    }
}
