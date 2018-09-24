package com.springcloud.webflux_client.Controller;

import java.lang.annotation.*;

/**
 * 服务器相关信息，类上，运行时起作用
 * @author xxwy
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiServer {

    String value() default "";
}
