package com.springcloud.webflux_client.proxy;

public interface ProxyCreator {

    /**
     * 创建代理类
     */
    Object createProxy(Class<?> type);
}
