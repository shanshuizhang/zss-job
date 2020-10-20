package com.zss.rpc.core.remoting.transport;

import com.zss.rpc.core.remoting.invoker.reference.RpcReferenceBean;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient {
    protected static final Logger logger = LoggerFactory.getLogger(BaseClient.class);

    protected volatile RpcReferenceBean rpcReferenceBean;

    public void init(RpcReferenceBean rpcReferenceBean){
        this.rpcReferenceBean = rpcReferenceBean;
    }

    /**
     * 异步发送请求
     * @param address
     * @param rpcRequest
     * @throws Exception
     */
    public abstract void asyncSend(String address, RpcRequest rpcRequest) throws Exception;
}
