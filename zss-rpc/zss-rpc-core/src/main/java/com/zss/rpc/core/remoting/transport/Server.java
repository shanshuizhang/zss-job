package com.zss.rpc.core.remoting.transport;

import com.zss.rpc.core.remoting.provider.RpcProvider;

public interface Server {

    /**
     * 启动服务
     * @param rpcProvider
     * @throws Exception
     */
    void start(final RpcProvider rpcProvider) throws Exception;

    /**
     * 停止服务
     * @throws Exception
     */
    void stop() throws Exception;

}
