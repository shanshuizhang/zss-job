package com.zss.rpc.core.remoting.invoker.route.impl;

import com.zss.rpc.core.remoting.invoker.route.RpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * 负载均衡，轮询策略
 */
public class RpcLoadBalanceRoundStrategy implements RpcLoadBalance {

    private ConcurrentMap<String, LongAdder> servieCount = new ConcurrentHashMap<>();

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private long cacheValidTime = 0;

    private int count(String serviceKey){
        //服务调用次数缓存一天
        if(System.currentTimeMillis() > cacheValidTime){
            servieCount.clear();
            cacheValidTime = System.currentTimeMillis() + ONE_DAY;
        }
        //每个服务调用的次数
        LongAdder longAdder = servieCount.get(serviceKey);
        if(longAdder == null || longAdder.longValue() > 1000000){
            longAdder = new LongAdder();
            longAdder.add(new Random().nextInt(100));
        } else{
            longAdder.increment();
        }
        servieCount.put(serviceKey,longAdder);
        return longAdder.intValue();
    }

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        String[] addresses = addressSet.toArray(new String[addressSet.size()]);
        return addresses[count(serviceKey) % addresses.length];
    }
}
