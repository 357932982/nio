package com.springcloud.webflux_client.proxy;

import com.springcloud.webflux_client.Controller.ApiServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xxwy
 * 使用jdk动态代理实现类
 */
@Slf4j
public class JDKProxtCreator implements ProxyCreator {
    @Override
    public Object createProxy(Class<?> type) {
        log.info("createProxy: {}  " + type);
        //根据借口得到Api服务器信息
        ServerInfo serverInfo = extractServerInfo(type);
        log.info("serverInfo: " + serverInfo);
        //给每一个代理类一个实现
        RestHandler handler = new WebClientRestHandler();

        //初始化服务器信息
        handler.init(serverInfo);
        return Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //根据方法和参数得到调用信息
                        MethodInfo methodInfo = extractMethodInfo(method, args);
                        log.info("methodInfo: " + methodInfo);

                        //调用rest
                        return handler.invokeRest(methodInfo);
                    }


                    /**
                     * 得到方法定义，调用参数得到调用的相关信息
                     * @param method
                     * @param args
                     * @return
                     */
                    private MethodInfo extractMethodInfo(Method method, Object[] args) {
                        MethodInfo methodInfo = new MethodInfo();
                        //得到url ， method
                        extractUrlAndMethod(method, methodInfo);
                        //得到参数和body
                        extractRequestParamsAndBody(method, args, methodInfo);
                        //得到返回对象信息
                        extractReturnInfo(method,methodInfo);
                        return methodInfo;

                    }

                    /**
                     * 返回对象信息
                     * isAssignableFrom : 判断类型是否是某个子类
                     * instanceof : 判断对象是否是某个子类
                     * @param method
                     * @param methodInfo
                     */
                    private void extractReturnInfo(Method method, MethodInfo methodInfo) {
                        boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
                        methodInfo.setReturnFlux(isFlux);
                        Class<?> elementType = extraceElementType(
                                method.getGenericReturnType()
                        );
                        methodInfo.setReturnElementType(elementType);

                    }

                    /**
                     * 得到返回类型的信息
                     * @param genericReturnType
                     * @return
                     */
                    private Class<?> extraceElementType(Type genericReturnType) {
                        ParameterizedType parameterizedType = (ParameterizedType)genericReturnType;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        return (Class<?>)actualTypeArguments[0];

                    }


                    /**
                     * 得到参数和body
                     * @param method
                     * @param args
                     * @param methodInfo
                     */
                    private void extractRequestParamsAndBody(Method method, Object[] args, MethodInfo methodInfo) {
                        Parameter[] parameters = method.getParameters();
                        Map<String, Object> parames = new LinkedHashMap<>();
                        methodInfo.setParams(parames);
                        for (int i = 0; i < parameters.length; i++) {
                            //是否带@PathVariable
                            PathVariable annoPath = parameters[i].getAnnotation(PathVariable.class);
                            if (annoPath != null) {
                                parames.put(annoPath.value(), args[i]);
                            }
                            //是否带requestBody,返回类型
                            RequestBody annoBody = parameters[i].getAnnotation(RequestBody.class);
                            if (annoBody != null) {
                                methodInfo.setBody((Mono<?>) args[i]);
                                methodInfo.setBodyElementType(
                                        extraceElementType(parameters[i].getParameterizedType()));
                            }
                        }
                    }

                    /**
                     * 得到url ， method
                     * @param method
                     * @param methodInfo
                     */
                    private void extractUrlAndMethod(Method method, MethodInfo methodInfo) {
                        Annotation[] annotations = method.getAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof GetMapping) {
                                GetMapping a = (GetMapping) annotation;
                                methodInfo.setUrl(a.value()[0]);
                                methodInfo.setMethod(HttpMethod.GET);
                            }
                            if (annotation instanceof DeleteMapping) {
                                DeleteMapping a = (DeleteMapping) annotation;
                                methodInfo.setUrl(a.value()[0]);
                                methodInfo.setMethod(HttpMethod.DELETE);
                            }
                            if (annotation instanceof PostMapping) {
                                PostMapping a = (PostMapping) annotation;
                                methodInfo.setUrl(a.value()[0]);
                                methodInfo.setMethod(HttpMethod.POST);
                            }
                            if (annotation instanceof PutMapping) {
                                PutMapping a = (PutMapping) annotation;
                                methodInfo.setUrl(a.value()[0]);
                                methodInfo.setMethod(HttpMethod.PUT);
                            }
                        }
                    }
                }
        );
    }

    /**
     * 获取注解上的信息
     *
     * @param type
     * @return
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ServerInfo info = new ServerInfo();
        ApiServer anno = type.getAnnotation(ApiServer.class);
        info.setUrl(anno.value());
        return info;
    }
}
