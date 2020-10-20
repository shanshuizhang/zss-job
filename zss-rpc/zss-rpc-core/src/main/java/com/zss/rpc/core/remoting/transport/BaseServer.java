package com.zss.rpc.core.remoting.transport;

import com.zss.rpc.core.remoting.provider.RpcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServer {
    protected static final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    private Callback startedCallback;

    private Callback stopedCallback;

    public void setStartedCallback(Callback startedCallback) {
        this.startedCallback = startedCallback;
    }

    public void setStopedCallback(Callback stopedCallback) {
        this.stopedCallback = stopedCallback;
    }

    /**
     * 启动时调用
     */
    public void onStarted(){
        if(startedCallback != null){
            try {
                startedCallback.run();
            } catch (Exception e) {
                logger.error("zss-rpc, server startedCallback error.",e);
            }
        }
    }

    /**
     * 停止时调用
     */
    public void onStoped(){
        if(stopedCallback != null){
            try {
                stopedCallback.run();
            } catch (Exception e) {
                logger.error("zss-rpc,server stopedCallback error.",e);
            }
        }
    }

    /**
     * 启动服务
     * @param rpcProvider
     * @throws Exception
     */
    public abstract void start(final RpcProvider rpcProvider) throws Exception;

    /**
     * 停止服务
     * @throws Exception
     */
    public abstract void stop() throws Exception;
}
