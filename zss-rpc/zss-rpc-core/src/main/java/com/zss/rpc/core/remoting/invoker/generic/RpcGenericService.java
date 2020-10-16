package com.zss.rpc.core.remoting.invoker.generic;

public interface RpcGenericService {

    /**
     * 通用调用方法
     * @param iface
     * @param version
     * @param method
     * @param parameterTypes
     * @param args
     * @return
     */
    Object invoke(String iface,String version,String method,String[] parameterTypes,Object[] args);
}
