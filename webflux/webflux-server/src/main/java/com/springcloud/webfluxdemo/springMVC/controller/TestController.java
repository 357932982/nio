package com.springcloud.webfluxdemo.springMVC.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@CommonsLog
@RestController
public class TestController {

    @GetMapping("/1")
    private String get1(){

        log.info("get1 start");
        String str = createStr();
        log.info("get1 end");
        return str;

    }

    /**
     * Mono: 产生0-1个序列
     * @return
     */
    @GetMapping("/2")
    private Mono<String> get2(){
        log.info("get2 start");
        Mono<String> result = Mono.fromSupplier(() -> createStr());
        log.info("get2 end");
        return result;
    }

    @GetMapping(value = "/3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  //produces = "text/event-stream"
    public Flux<String> get3(){
        log.info("get3 start");
        Flux<String> result = Flux.fromStream(IntStream.range(1,5).mapToObj(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "flux data---" + i;
        }));
        log.info("get3 end");
        return result;
    }

    private String createStr(){
        try{
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e){

        }
        return "some String";
    }

}
