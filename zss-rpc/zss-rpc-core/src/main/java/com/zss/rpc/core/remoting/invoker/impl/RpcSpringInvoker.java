package com.zss.rpc.core.remoting.invoker.impl;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.registry.Register;
import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.invoker.annotation.RpcReference;
import com.zss.rpc.core.remoting.invoker.reference.RpcReferenceBean;
import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.util.ServiceKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RpcSpringInvoker implements InstantiationAwareBeanPostProcessor,InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcSpringInvoker.class);

    private RpcInvoker rpcInvoker;

    private Class<? extends Register> serviceRegistryClass;

    private Map<String,String> serviceRegistryParam;

    public void setServiceRegistryClass(Class<? extends Register> serviceRegistryClass) {
        this.serviceRegistryClass = serviceRegistryClass;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rpcInvoker = new RpcInvoker(serviceRegistryClass,serviceRegistryParam);
        rpcInvoker.start();
    }

    @Override
    public void destroy() throws Exception {
        rpcInvoker.stop();
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        final Set<String> serviceKeySet = new HashSet<>();
        ReflectionUtils.doWithFields(bean.getClass(),field -> {
            if(field.isAnnotationPresent(RpcReference.class)){
                //valid
                Class<?> clazz = field.getType();
                if(!clazz.isInterface()){
                    throw new RpcException("zss-rpc, reference(RpcReference) must be interface.");
                }
                //获取注解属性，构建引用bean
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                RpcReferenceBean rpcReferenceBean = new RpcReferenceBean();
                rpcReferenceBean.setClient(rpcReference.client());
                rpcReferenceBean.setSerializer(rpcReference.serializer());
                rpcReferenceBean.setCallType(rpcReference.calltype());
                rpcReferenceBean.setLoadBalance(rpcReference.loadBalance());
                rpcReferenceBean.setVersion(rpcReference.version());
                rpcReferenceBean.setTimeout(rpcReference.timeout());
                rpcReferenceBean.setAddress(rpcReference.address());
                rpcReferenceBean.setAccessToken(rpcReference.accessToken());
                rpcReferenceBean.setIface(clazz);
                rpcReferenceBean.setRpcInvoker(rpcInvoker);
                rpcReferenceBean.setRpcInvokeCallback(null);
                //生成服务代理对象
                Object serviceProxy = null;
                try {
                    serviceProxy = rpcReferenceBean.getProxyObject();
                } catch (Exception e){
                    throw new RpcException(e);
                }
                //给引用bean（接口）设置值（代理对象）
                field.setAccessible(true);
                field.set(bean,serviceProxy);
                //存储servicekey
                String serviceKey = ServiceKeyBuilder.buildServiceKey(clazz.getName(),rpcReference.version());
                serviceKeySet.add(serviceKey);
                logger.info("zss-rpc, invoker init reference bean success. serviceKey = {}, bean.field = {}.{}",
                        serviceKey,beanName,field.getName());
            }
        });
        //发现服务
        Register register = rpcInvoker.getRegister();
        if(register != null){
            try {
                register.discovery(serviceKeySet);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
        return true;
    }
}
