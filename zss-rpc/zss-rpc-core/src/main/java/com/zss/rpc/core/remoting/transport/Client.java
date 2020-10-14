package com.zss.rpc.core.remoting.transport;

import com.zss.rpc.core.remoting.transport.model.RpcRequest;

public interface Client {

    /**
     * 异步发送请求
     * @param address
     * @param rpcRequest
     * @throws Exception
     */
    void asyncSend(String address, RpcRequest rpcRequest) throws Exception;
}
