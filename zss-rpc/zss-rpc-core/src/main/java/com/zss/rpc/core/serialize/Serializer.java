package com.zss.rpc.core.serialize;


public interface Serializer {

    /**
     * 对象序列化字节数组
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes);
}
