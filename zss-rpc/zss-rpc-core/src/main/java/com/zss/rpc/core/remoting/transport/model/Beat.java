package com.zss.rpc.core.remoting.transport.model;

/**
 * 心跳
 */
public final class Beat {

    /**
     * 心跳间隔时间
     */
    public static final int BEAT_INTERVAL = 30;

    /**
     * 心跳标识id
     */
    public static final String BEAT_ID = "BEAT_PING_PONG";

    public final static RpcRequest BEAT_PING;

    static {
        BEAT_PING = new RpcRequest(){};
        BEAT_PING.setRequestId(BEAT_ID);
    }
}
