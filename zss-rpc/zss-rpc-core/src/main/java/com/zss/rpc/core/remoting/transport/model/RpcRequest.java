package com.zss.rpc.core.remoting.transport.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * rpc请求
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 99L;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 请求创建时毫秒数
     */
    private long createMillisTime;

    /**
     * 访问token
     */
    private String accessToken;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数
     */
    private Object[] parameters;

    /**
     * 版本号
     */
    private String version;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getCreateMillisTime() {
        return createMillisTime;
    }

    public void setCreateMillisTime(long createMillisTime) {
        this.createMillisTime = createMillisTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", createMillisTime=" + createMillisTime +
                ", accessToken='" + accessToken + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", version='" + version + '\'' +
                '}';
    }
}
