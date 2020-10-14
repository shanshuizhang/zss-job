package com.zss.rpc.core.remoting.invoker;

import com.zss.rpc.core.remoting.transport.Callback;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;

import java.util.ArrayList;
import java.util.List;

public class RpcInvoker {

    private final List<Callback> stopCallbackList = new ArrayList<>();

    public void addStopCallBack(Callback callback){
        stopCallbackList.add(callback);
    }

    public void notifyInvokerFuture(String requestId, final RpcResponse rpcResponse){

    }
}
