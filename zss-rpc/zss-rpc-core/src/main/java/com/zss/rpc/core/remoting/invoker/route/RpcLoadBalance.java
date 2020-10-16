package com.zss.rpc.core.remoting.invoker.route;

import java.util.TreeSet;

/**
 * Rpc负载均衡顶级接口，策略模式
 */
public interface RpcLoadBalance {

    /**
     * 根据服务key,服务实例地址集合，选取服务地址
     * @param serviceKey
     * @param addressSet
     * @return
     */
    String route(String serviceKey, TreeSet<String> addressSet);
}
