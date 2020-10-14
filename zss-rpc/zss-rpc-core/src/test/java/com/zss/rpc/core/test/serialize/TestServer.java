package com.zss.rpc.core.test.serialize;

import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.remoting.transport.BaseServer;
import com.zss.rpc.core.remoting.transport.impl.netty.server.NettyServer;

public class TestServer {
    public static void main(String[] args) throws Exception {
        BaseServer server = new NettyServer();
        server.setStopedCallback(()->{
            throw new Exception("哈哈哈");
            //System.out.println("启动时回调函数成功执行");
        });
        server.start(new RpcProvider());

        server.stop();
    }
}
