package com.zss.rpc.core.remoting.invoker.reference;

import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.serialize.Serializer;

public class RpcReferenceBean {

    private RpcInvoker rpcInvoker;

    private Serializer serializerInstance;

    public RpcInvoker getRpcInvoker() {
        return rpcInvoker;
    }

    public void setRpcInvoker(RpcInvoker rpcInvoker) {
        this.rpcInvoker = rpcInvoker;
    }

    public Serializer getSerializerInstance() {
        return serializerInstance;
    }
}
