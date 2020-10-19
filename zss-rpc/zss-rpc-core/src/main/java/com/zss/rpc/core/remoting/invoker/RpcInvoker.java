package com.zss.rpc.core.remoting.invoker;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.registry.Register;
import com.zss.rpc.core.registry.impl.LocalRegister;
import com.zss.rpc.core.remoting.transport.Callback;
import com.zss.rpc.core.remoting.transport.model.RpcFutureResponse;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RpcInvoker {
    private static final Logger logger = LoggerFactory.getLogger(RpcInvoker.class);

    private final Class<? extends Register> serviceRegistryClass;

    private final Map<String,String> serviceRegistryParam;

    private Register register;

    private static volatile RpcInvoker instance = new RpcInvoker(LocalRegister.class, null);

    public static RpcInvoker getInstance() {
        return instance;
    }

    public RpcInvoker(Class<? extends Register> serviceRegistryClass, Map<String, String> serviceRegistryParam) {
        this.serviceRegistryClass = serviceRegistryClass;
        this.serviceRegistryParam = serviceRegistryParam;
    }

    private final List<Callback> stopCallbackList = new ArrayList<>();

    private ConcurrentMap<String, RpcFutureResponse> futureResponseMap = new ConcurrentHashMap<>();

    private ThreadPoolExecutor responseCallbackThreadPool = null;

    public Register getRegister() {
        return register;
    }

    public void addStopCallBack(Callback callback){
        stopCallbackList.add(callback);
    }

    public void setInvokerFuture(String requestId, RpcFutureResponse rpcFutureResponse){
        futureResponseMap.put(requestId, rpcFutureResponse);
    }

    public void removeInvokerFuture(String requestId){
        futureResponseMap.remove(requestId);
    }

    public void start() throws Exception{
        // start registry
        if(serviceRegistryClass != null){
            register = serviceRegistryClass.newInstance();
            register.start(serviceRegistryParam);
        }
    }

    public void  stop() throws Exception {
        // stop registry
        if(register != null){
            register.stop();
        }
        // stop callback
        if (stopCallbackList.size() > 0) {
            for (Callback callback: stopCallbackList) {
                try {
                    callback.run();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        // stop CallbackThreadPool
        stopCallbackThreadPool();
    }

    public void notifyInvokerFuture(String requestId, final RpcResponse rpcResponse){
        // get rpcFutureResponse
        RpcFutureResponse rpcFutureResponse = futureResponseMap.get(requestId);
        logger.info("client获取返回结果");
        if(rpcFutureResponse == null){
            return;
        }

        if(rpcFutureResponse.getRpcInvokeCallback() != null){
            try{
                executeResponseCallback(()->{
                    if(rpcResponse.getErrorMsg() != null){
                        rpcFutureResponse.getRpcInvokeCallback().onFailure(new RpcException(rpcResponse.getErrorMsg()));
                    } else{
                        rpcFutureResponse.getRpcInvokeCallback().onSuccess(rpcResponse.getResult());
                    }
                });
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        } else{
            rpcFutureResponse.setRpcResponse(rpcResponse);
        }

        futureResponseMap.remove(requestId);
    }

    private void executeResponseCallback(Runnable runnable){
        if(responseCallbackThreadPool == null){
            synchronized (this){
                if(responseCallbackThreadPool == null){
                    responseCallbackThreadPool = new ThreadPoolExecutor(
                            10,
                            100,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(1000),
                            r -> {
                                return new Thread(r,"zss-rpc, RpcInvoker-responseCallbackThreadPool-" + r.hashCode());
                            },
                            (r,executor)->{
                                throw new RpcException("zss-rpc Invoke Callback Thread pool is EXHAUSTED!");
                            }
                    );
                }
            }
        }
        responseCallbackThreadPool.execute(runnable);
    }

    private void stopCallbackThreadPool(){
        if(responseCallbackThreadPool != null){
            responseCallbackThreadPool.shutdown();
        }
    }
}
