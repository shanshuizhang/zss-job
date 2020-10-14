package com.zss.rpc.core.remoting.transport.common;

import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.invoker.reference.RpcReferenceBean;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 客户端连接模板类
 */
public abstract class ConnectClient {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectClient.class);

    private static volatile ConcurrentMap<String,ConnectClient> connectClientMap;

    private static volatile ConcurrentMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();

    public abstract void init(String address, final Serializer serializer, final RpcInvoker rpcInvoker) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    public abstract void send(RpcRequest rpcRequest) throws Exception ;

    public static void asyncSend(RpcRequest rpcRequest, String address,
                                 Class<? extends ConnectClient> connectClientImpl,
                                 final RpcReferenceBean rpcReferenceBean) throws Exception{
        ConnectClient connectClient = getConnectClient(address,connectClientImpl,rpcReferenceBean);
        connectClient.send(rpcRequest);
    }

    private static ConnectClient getConnectClient(String address, Class<? extends ConnectClient> connectClientImpl,
                                                  final RpcReferenceBean rpcReferenceBean) throws Exception{
        //初始化客户端连接缓存，双重校验+锁+volatile 防止创建多个
        if(connectClientMap == null){
            synchronized (ConnectClient.class){
                if(connectClientMap == null){
                    //初始化客户端连接缓存
                    connectClientMap = new ConcurrentHashMap<>();
                    //add callback 同时添加停止回调方法，当停止时，关闭连接，清空缓存
                    rpcReferenceBean.getRpcInvoker().addStopCallBack(()->{
                        if(connectClientMap.size() > 0){
                            for(String key:connectClientMap.keySet()){
                                ConnectClient connectClient = connectClientMap.get(key);
                                connectClient.close();
                            }
                            connectClientMap.clear();
                        }
                    });
                }
            }
        }
        //获取有效的连接
        ConnectClient connectClient = connectClientMap.get(address);
        if (connectClient != null && connectClient.isValidate()) {
            return connectClient;
        }

        //lock
        Object clientLock = connectClientLockMap.get(address);
        if(clientLock == null){
            connectClientLockMap.putIfAbsent(address,new Object());
            clientLock = connectClientLockMap.get(address);
        }

        synchronized (clientLock){
            connectClient = connectClientMap.get(address);
            if (connectClient != null && connectClient.isValidate()) {
                return connectClient;
            }
            //无效，删掉
            if (connectClient != null) {
                connectClient.close();
                connectClientMap.remove(address);
            }

            // 创建新连接
            ConnectClient connectClient_new = connectClientImpl.newInstance();
            try {
                connectClient_new.init(address, rpcReferenceBean.getSerializerInstance(), rpcReferenceBean.getRpcInvoker());
                connectClientMap.put(address, connectClient_new);
            } catch (Exception e) {
                connectClient_new.close();
                throw e;
            }
            return connectClient_new;
        }
    }
}
