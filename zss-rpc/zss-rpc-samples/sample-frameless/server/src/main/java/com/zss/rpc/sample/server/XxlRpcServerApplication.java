package com.zss.rpc.sample.server;


import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.remoting.transport.impl.netty.server.NettyServer;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import com.zss.rpc.sample.api.DemoService;
import com.zss.rpc.sample.server.service.DemoServiceImpl;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcServerApplication {

    public static void main(String[] args) throws Exception {

        // init
        RpcProvider providerFactory = new RpcProvider();
        providerFactory.setServer(NettyServer.class);
        providerFactory.setSerializer(Hessian2Serializer.class);
        providerFactory.setCorePoolSize(-1);
        providerFactory.setMaxPoolSize(-1);
        providerFactory.setIp(null);
        providerFactory.setPort(7080);
        providerFactory.setAccessToken(null);
        providerFactory.setServiceRegistry(null);
        providerFactory.setServiceRegistryParam(null);

        // add services
        providerFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

        // start
        providerFactory.start();

        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        // stop
        providerFactory.stop();

    }

}
