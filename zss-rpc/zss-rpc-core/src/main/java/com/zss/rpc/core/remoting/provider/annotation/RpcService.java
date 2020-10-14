package com.zss.rpc.core.remoting.provider.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    String version() default "";
}
