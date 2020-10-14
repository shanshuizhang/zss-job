package com.zss.rpc.core.remoting.provider.impl;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.remoting.provider.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 基于spring 容器 实现rpcProvider
 */
public class RpcSpringProvider extends RpcProvider implements ApplicationContextAware, InitializingBean, DisposableBean {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(serviceBeanMap != null && serviceBeanMap.size() > 0){
            for (Object serviceBean : serviceBeanMap.values()){
                //valid
                if(serviceBean.getClass().getInterfaces().length == 0){
                    throw new RpcException("zss-rpc, service(RpcService) must inherit interface.");
                }
                //add service,存在漏洞，实现接口必须位于第一个
                String className = serviceBean.getClass().getInterfaces()[0].getName();
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String version = rpcService.version();
                super.addService(className,version,serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.stop();
    }
}
