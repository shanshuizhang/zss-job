package com.zss.rpc.sample.server.config;

import com.zss.rpc.core.registry.impl.LocalRegister;
import com.zss.rpc.core.remoting.provider.impl.RpcSpringProvider;
import com.zss.rpc.core.remoting.transport.impl.netty.server.NettyServer;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcProviderConfig {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderConfig.class);

    @Value("${zss-rpc.remoting.port}")
    private int port;

    @Bean
    public RpcSpringProvider rpcSpringProvider(){
        RpcSpringProvider rpcSpringProvider = new RpcSpringProvider();
        rpcSpringProvider.setServer(NettyServer.class);
        rpcSpringProvider.setSerializer(Hessian2Serializer.class);
        rpcSpringProvider.setIp(null);
        rpcSpringProvider.setPort(port);
        rpcSpringProvider.setCorePoolSize(-1);
        rpcSpringProvider.setMaxPoolSize(-1);
        rpcSpringProvider.setAccessToken(null);
        rpcSpringProvider.setServiceRegistry(LocalRegister.class);
        rpcSpringProvider.setServiceRegistryParam(null);
        logger.info(">>>>>>>>>>> zss-rpc provider config init finish.");
        return rpcSpringProvider;
    }
}
