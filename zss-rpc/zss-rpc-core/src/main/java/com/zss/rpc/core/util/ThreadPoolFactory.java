package com.zss.rpc.core.util;

import com.zss.rpc.core.exception.RpcException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {

    /**
     * 构建 server 线程池
     * @return
     */
    public static ThreadPoolExecutor buildServerThreadPool(final String serverType,int corePoolSize,int maxPoolSize){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> {
                    return new Thread(r,String.format("zss-rpc,%s-serverHandlerPool-%d",serverType,r.hashCode()));
                },
                (r, executor) -> {
                    throw new RpcException(String.format("zss-rpc %s Thread pool is EXHAUSTED!",serverType));
                }
        );
        return threadPoolExecutor;
    }
}
