package com.zss.rpc.core.test.serialize;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
@Component
//@Configuration
public class TestSpringBeanPostProcess implements ApplicationContextAware,InstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean {
    public TestSpringBeanPostProcess(){
        System.out.println("我是构造方法");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("我是方法postConstruct");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("我是方法preDestroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("我是销毁方法destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("我是初始化afterPropertiesSet");
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("我是方法postProcessAfterInstantiation");
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("我是方法setApplicationContext");
    }


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestCompantScan.class);
        context.refresh();

        context.close();
    }
}

