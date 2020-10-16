package com.zss.rpc.core.remoting.invoker.route;

import com.zss.rpc.core.remoting.invoker.route.impl.RpcLoadBalanceRoundStrategy;

public enum LoadBalance {

    ROUND(new RpcLoadBalanceRoundStrategy());

    public final RpcLoadBalance rpcLoadBalance;

    LoadBalance(RpcLoadBalance rpcLoadBalance){
        this.rpcLoadBalance = rpcLoadBalance;
    }

    public static LoadBalance match(String name,LoadBalance defaultRouter){
        for(LoadBalance loadBalance : LoadBalance.values()){
            if(loadBalance.name().equals(name)){
                return loadBalance;
            }
        }
        return defaultRouter;
    }
}
