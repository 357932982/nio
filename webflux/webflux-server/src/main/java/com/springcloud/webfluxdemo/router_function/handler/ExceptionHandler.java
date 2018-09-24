package com.springcloud.webfluxdemo.router_function.handler;

import com.springcloud.webfluxdemo.springMVC.exception.CheckException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;


@Component
@Order(-2) //设置优先级，数值越小。优先级越高。
public class ExceptionHandler implements WebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        ServerHttpResponse response = serverWebExchange.getResponse();
        //设置响应头400
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        //设置返回类型
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        //异常信息
        String errorMsg = this.toString(throwable);

        DataBuffer db = response.bufferFactory().wrap(errorMsg.getBytes());

        return response.writeWith(Mono.just(db));
    }

    private String toString(Throwable ex){
        //已知异常
        if(ex instanceof CheckException){
            CheckException e = (CheckException) ex;
            return e.getFiendName() + ": invalid value " + e.getFieldValue();
        }
        //未知异常，需要打印堆栈，方便定位
        else {
            ex.printStackTrace();
            return ex.toString();
        }
    }
}
