package com.zss.rpc.core.remoting.invoker.call;

public abstract class RpcInvokeCallback<T> {

    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable throwable);

    private static ThreadLocal<RpcInvokeCallback> invokeCallbackThreadLocal = new ThreadLocal<>();

    public static RpcInvokeCallback getInvokeCallback(){
        RpcInvokeCallback rpcInvokeCallback = invokeCallbackThreadLocal.get();
        invokeCallbackThreadLocal.remove();
        return rpcInvokeCallback;
    }

    public static void setInvokeCallback(RpcInvokeCallback rpcInvokeCallback){
        invokeCallbackThreadLocal.set(rpcInvokeCallback);
    }

    public static void removeInvokeCallback(){
        invokeCallbackThreadLocal.remove();
    }
}
