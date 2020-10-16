package com.zss.rpc.core.remoting.invoker.annotation;

import com.zss.rpc.core.remoting.invoker.call.CallType;
import com.zss.rpc.core.remoting.invoker.route.LoadBalance;
import com.zss.rpc.core.remoting.transport.BaseClient;
import com.zss.rpc.core.remoting.transport.Client;
import com.zss.rpc.core.remoting.transport.impl.netty.client.NettyClient;
import com.zss.rpc.core.serialize.Serializer;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    Class<? extends BaseClient> client() default NettyClient.class;

    Class<? extends Serializer> serializer() default Hessian2Serializer.class;

    CallType calltype() default CallType.SYNC;

    LoadBalance loadBalance() default LoadBalance.ROUND;

    String version() default "";

    long timeout() default 1000;

    String address() default "";

    String accessToken() default "";
}
