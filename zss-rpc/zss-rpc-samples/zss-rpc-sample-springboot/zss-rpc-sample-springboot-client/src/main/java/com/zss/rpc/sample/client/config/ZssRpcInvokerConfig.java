package com.zss.rpc.sample.client.config;

import com.zss.rpc.core.registry.impl.LocalRegister;
import com.zss.rpc.core.remoting.invoker.impl.RpcSpringInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ZssRpcInvokerConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZssRpcInvokerConfig.class);

    @Bean
    public RpcSpringInvoker rpcSpringInvoker(){
        RpcSpringInvoker rpcSpringInvoker = new RpcSpringInvoker();
        rpcSpringInvoker.setServiceRegistryClass(LocalRegister.class);
        rpcSpringInvoker.setServiceRegistryParam(new HashMap<String,String>(){{
            //put()
        }});
        logger.info(">>>>>>>>>>> zss-rpc invoker config init finish.");
        return rpcSpringInvoker;
    }
}
