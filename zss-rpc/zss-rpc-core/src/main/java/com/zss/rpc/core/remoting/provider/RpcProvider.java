package com.zss.rpc.core.remoting.provider;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.registry.Register;
import com.zss.rpc.core.remoting.transport.BaseServer;
import com.zss.rpc.core.remoting.transport.Server;
import com.zss.rpc.core.remoting.transport.impl.netty.server.NettyServer;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import com.zss.rpc.core.serialize.Serializer;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import com.zss.rpc.core.util.IpUtil;
import com.zss.rpc.core.util.NetUtil;
import com.zss.rpc.core.util.ServiceKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * rpc 服务提供者
 */
public class RpcProvider {
    private static final Logger logger = LoggerFactory.getLogger(RpcProvider.class);

    private Class<? extends BaseServer> server = NettyServer.class;

    private Class<? extends Serializer> serializer = Hessian2Serializer.class;

    private Serializer serializerInstance;

    private String accessToken = null;

    private static final long TIMEOUT = 3 * 60 * 1000;

    private BaseServer serverInstance;

    private Register registerInstance;

    private String ip = null;

    private int port = 9999;

    private String registryAddress;

    private int corePoolSize = 60;

    private int maxPoolSize = 300;

    private Class<? extends Register> serviceRegistry = null;

    private Map<String, String> serviceRegistryParam = null;

    private Map<String,Object> serviceData = new HashMap<>();

    public Serializer getSerializerInstance() {
        return serializerInstance;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setServer(Class<? extends BaseServer> server) {
        this.server = server;
    }

    public void setSerializer(Class<? extends Serializer> serializer) {
        this.serializer = serializer;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setServiceRegistry(Class<? extends Register> serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    /**
     * 启动服务
     * @throws Exception
     */
    public void start() throws Exception {
        //valid
        if(this.server == null){
            throw new RpcException("zss-rpc provider server missing.");
        }
        if(this.serializer == null){
            throw new RpcException("zss-rpc provider serializer missing.");
        }
        if(!(this.corePoolSize > 0 && this.maxPoolSize > 0 && this.maxPoolSize >= this.corePoolSize)) {
            this.corePoolSize = 60;
            this.maxPoolSize = 300;
        }
        if(this.ip == null) {
            this.ip = IpUtil.getIp();
        }
        if(this.port <= 0) {
            this.port = 9999;
        }
        if(this.registryAddress == null || this.registryAddress.trim().length() == 0) {
            this.registryAddress = IpUtil.getIpPort(this.ip, this.port);
        }
        if (NetUtil.isPortUsed(this.port)) {
            throw new RpcException("zss-rpc provider port["+ this.port +"] is used.");
        }
        //init
        this.serverInstance = server.newInstance();
        this.serializerInstance = serializer.newInstance();
        //set callback
        this.serverInstance.setStartedCallback(()->{
            // 开始注册
            if(serviceRegistry != null){
                registerInstance = serviceRegistry.newInstance();
                registerInstance.start(serviceRegistryParam);
                if(serviceData.size() > 0){
                    registerInstance.registry(serviceData.keySet(),registryAddress);
                }
            }
        });
        this.serverInstance.setStopedCallback(()->{
            // 停止注册
            if(registerInstance != null){
                if(serviceData.size() > 0){
                    registerInstance.remove(serviceData.keySet(),registryAddress);
                }
                registerInstance.stop();
                registerInstance = null;
            }
        });
        //启动服务
        this.serverInstance.start(this);
    }

    /**
     * 停止服务
     * @throws Exception
     */
    public void stop() throws Exception {
        serverInstance.stop();
    }

    /**
     * 添加服务到本地缓存
     * @param className
     * @param version
     * @param serviceBean
     */
    public void addService(String className,String version,Object serviceBean){
        String serviceKey = ServiceKeyBuilder.buildServiceKey(className,version);
        serviceData.put(serviceKey,serviceBean);
        logger.info("zss-rpc, provider add service success. serviceKey = [{}], serviceBean = [{}]", serviceKey, serviceBean.getClass());
    }

    /**
     * 通过反射方式调用服务
     * @param rpcRequest
     * @return
     */
    public RpcResponse invokeService(RpcRequest rpcRequest){
        // make response
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        // valid
        if(accessToken != null && accessToken.trim().length() > 0
                && !accessToken.trim().equals(rpcRequest.getAccessToken())){
            rpcResponse.setErrorMsg(String.format("The access token [%s] is wrong",rpcRequest.getAccessToken()));
            return rpcResponse;
        }
        if(System.currentTimeMillis() - rpcRequest.getCreateMillisTime() > TIMEOUT){
            rpcResponse.setErrorMsg("The timestamp difference between admin and executor exceeds the limit.");
            return rpcResponse;
        }

        //根据serviceKey获取serviceBean
        String serviceKey = ServiceKeyBuilder.buildServiceKey(rpcRequest.getClassName(),rpcRequest.getVersion());
        Object serviceBean = serviceData.get(serviceKey);
        if(serviceBean == null){
            rpcResponse.setErrorMsg(String.format("The service key [%s] not found.",serviceKey));
            return rpcResponse;
        }

        //invoke
        try{
            Class<?> serviceClass = serviceBean.getClass();
            Method method = serviceClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            method.setAccessible(true);
            Object result = method.invoke(serviceBean,rpcRequest.getParameters());
            rpcResponse.setResult(result);
        }catch (Throwable t){
            logger.error("zss-rpc provider invokeService error.", t);
            rpcResponse.setErrorMsg(t.getMessage());
        }
        return rpcResponse;
    }
}
