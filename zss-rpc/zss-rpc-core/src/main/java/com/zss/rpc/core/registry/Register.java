package com.zss.rpc.core.registry;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 注册顶级抽象
 */
public interface Register {

    /**
     * 开始注册
     */
    void start(Map<String, String> param);

    /**
     * 停止注册
     */
    void stop();


    /**
     * 注册服务
     *
     * @param keys      service key
     * @param value     service value/ip:port
     * @return
     */
    boolean registry(Set<String> keys, String value);


    /**
     * 删除服务
     *
     * @param keys
     * @param value
     * @return
     */
    boolean remove(Set<String> keys, String value);

    /**
     * 发现服务，多个
     *
     * @param keys
     * @return
     */
    Map<String, TreeSet<String>> discovery(Set<String> keys);

    /**
     * 发现服务，单个
     *
     * @param key   service key
     * @return      service value/ip:port
     */
    TreeSet<String> discovery(String key);
}
