package com.zss.rpc.core.remoting.invoker.reference;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.invoker.call.CallType;
import com.zss.rpc.core.remoting.invoker.call.RpcInvokeCallback;
import com.zss.rpc.core.remoting.invoker.generic.RpcGenericService;
import com.zss.rpc.core.remoting.invoker.route.LoadBalance;
import com.zss.rpc.core.remoting.transport.BaseClient;
import com.zss.rpc.core.remoting.transport.impl.netty.client.NettyClient;
import com.zss.rpc.core.remoting.transport.model.RpcFutureResponse;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import com.zss.rpc.core.serialize.Serializer;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import com.zss.rpc.core.util.ClassUtil;
import com.zss.rpc.core.util.ServiceKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RpcReferenceBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcReferenceBean.class);

    private RpcInvoker rpcInvoker = null;

    private RpcInvokeCallback rpcInvokeCallback = null;

    private Class<? extends BaseClient> client = NettyClient.class;

    private BaseClient clientInstance = null;

    private Class<? extends Serializer> serializer = Hessian2Serializer.class;

    private CallType callType = CallType.SYNC;

    private LoadBalance loadBalance = LoadBalance.ROUND;

    private Class<?> iface = null;

    private String version = null;

    private long timeout = 1000;

    private String address = null;

    private String accessToken = null;

    private Serializer serializerInstance;

    public RpcInvoker getRpcInvoker() {
        return rpcInvoker;
    }

    public void setRpcInvoker(RpcInvoker rpcInvoker) {
        this.rpcInvoker = rpcInvoker;
    }

    public void setRpcInvokeCallback(RpcInvokeCallback rpcInvokeCallback) {
        this.rpcInvokeCallback = rpcInvokeCallback;
    }

    public void setClient(Class<? extends BaseClient> client) {
        this.client = client;
    }

    public void setSerializer(Class<? extends Serializer> serializer) {
        this.serializer = serializer;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Serializer getSerializerInstance() {
        return serializerInstance;
    }

    public RpcReferenceBean initClient() throws Exception{
        // valid
        if (this.client == null) {
            throw new RpcException("zss-rpc reference client missing.");
        }
        if (this.serializer == null) {
            throw new RpcException("zss-rpc reference serializer missing.");
        }
        if (this.callType == null) {
            throw new RpcException("zss-rpc reference callType missing.");
        }
        if (this.loadBalance == null) {
            throw new RpcException("zss-rpc reference loadBalance missing.");
        }
        if (this.iface == null) {
            throw new RpcException("zss-rpc reference iface missing.");
        }
        if (this.timeout < 0) {
            this.timeout = 0;
        }
        if (this.rpcInvoker == null) {
            this.rpcInvoker = RpcInvoker.getInstance();
        }
        // init serializerInstance
        this.serializerInstance = serializer.newInstance();
        // init client
        clientInstance = client.newInstance();
        clientInstance.init(this);
        return this;
    }
    /**
     * 创建代理对象
     * @return
     * @throws Exception
     */
    public Object getProxyObject() throws Exception{
        initClient();

        Object proxyObject = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{iface},
                (proxy, method, args) -> {
                    // 请求参数
                    String className = method.getDeclaringClass().getName();
                    String versionTmp = version;
                    String methodName = method.getName();
                    Class[] parameterTypes = method.getParameterTypes();
                    Object[] parameters = args;

                    // 如果是Object类方法，则抛异常
                    if(className.equals(Object.class.getName())){
                        logger.error("zss-rpc proxy class-method not support [{}#{}]", className, methodName);
                        throw new RpcException("zss-rpc proxy class-method not support");
                    }

                    //如果是通用调用方法类型，则使用通用方法提供的参数，不在使用注解提供的参数
                    if(className.equals(RpcGenericService.class.getName()) && "invoke".equals(methodName)){
                        Class[] paramTypes = null;
                        if(args[3] != null){
                            String[] types = (String[])args[3];
                            int length = types.length;
                            if(length > 0){
                                paramTypes = new Class[length];
                                for(int i = 0;i < length;i++){
                                    paramTypes[i] = ClassUtil.resolveClass(types[i]);
                                }
                            }
                        }
                        className = (String)args[0];
                        versionTmp = (String)args[1];
                        methodName = (String)args[2];
                        parameterTypes = paramTypes;
                        parameters = (Object[]) args[4];
                    }
                    //获取调用地址
                    String finalAddress = address;
                    if(finalAddress == null || finalAddress.trim().length() == 0){
                        if(rpcInvoker != null && rpcInvoker.getRegister() != null){
                            //根据服务key获取服务实例地址集合
                            String serviceKey = ServiceKeyBuilder.buildServiceKey(className,versionTmp);
                            TreeSet<String> addressSet = rpcInvoker.getRegister().discovery(serviceKey);
                            if(addressSet == null || addressSet.size() == 0){

                            }else if(addressSet.size() == 1){
                                finalAddress = addressSet.first();
                            }else {
                                finalAddress = loadBalance.rpcLoadBalance.route(serviceKey,addressSet);
                            }
                        }
                    }
                    if(finalAddress == null || finalAddress.trim().length() == 0){
                        logger.error("zss-rpc reference bean[{}] address empty",className);
                        throw new RpcException("zss-rpc reference bean["+ className +"] address empty");
                    }
                    //构建请求
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setAccessToken(accessToken);
                    rpcRequest.setClassName(className);
                    rpcRequest.setCreateMillisTime(System.currentTimeMillis());
                    rpcRequest.setMethodName(methodName);
                    rpcRequest.setParameters(parameters);
                    rpcRequest.setParameterTypes(parameterTypes);
                    rpcRequest.setVersion(versionTmp);
                    //发送请求
                    if(callType == CallType.SYNC){
                        RpcFutureResponse rpcFutureResponse = new RpcFutureResponse(rpcInvoker,rpcRequest,null);
                        try{
                            clientInstance.asyncSend(finalAddress,rpcRequest);
                            RpcResponse rpcResponse = rpcFutureResponse.get(timeout, TimeUnit.MILLISECONDS);
                            if(rpcResponse.getErrorMsg() != null){
                                throw new RpcException(rpcResponse.getErrorMsg());
                            }
                            return rpcResponse.getResult();
                        }catch (Exception e){
                            logger.error("zss-rpc, invoke error, address:{}, RpcRequest{}", finalAddress, rpcRequest,e);
                            throw (e instanceof RpcException) ? e : new RpcException(e);
                        } finally {
                            rpcFutureResponse.removeInvokerFuture();
                        }
                    } else {
                        throw new RpcException("zss-rpc callType["+ callType +"] invalid");
                    }
                });
        return proxyObject;
    }
}
