package com.zss.rpc.core.remoting.transport;

import com.zss.rpc.core.remoting.invoker.reference.RpcReferenceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient implements Client{
    protected static final Logger logger = LoggerFactory.getLogger(Client.class);

    protected volatile RpcReferenceBean rpcReferenceBean;

    public void init(RpcReferenceBean rpcReferenceBean){
        this.rpcReferenceBean = rpcReferenceBean;
    }
}
