package com.zss.rpc.core.remoting.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServer implements Server{
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
}
