package com.zss.rpc.core.remoting.transport.impl.netty.client;

import com.zss.rpc.core.remoting.transport.BaseClient;
import com.zss.rpc.core.remoting.transport.common.ConnectClient;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;

/**
 * netty 客户端
 */
public class NettyClient extends BaseClient {

    private Class<? extends ConnectClient> connectClientImpl = NettyConnectClient.class;

    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest,address,connectClientImpl,rpcReferenceBean);
    }
}
