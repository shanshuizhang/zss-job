package com.zss.rpc.core.remoting.transport.impl.netty.client;

import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.transport.model.Beat;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private NettyConnectClient nettyConnectClient;

    private RpcInvoker rpcInvoker;

    public NettyClientHandler(NettyConnectClient nettyConnectClient,final RpcInvoker rpcInvoker){
        this.nettyConnectClient = nettyConnectClient;
        this.rpcInvoker = rpcInvoker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        logger.info("client channelRead0 执行了");
        rpcInvoker.notifyInvokerFuture(rpcResponse.getRequestId(),rpcResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            nettyConnectClient.send(Beat.BEAT_PING);
            logger.debug("zss-rpc netty client send beat-ping.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("zss-rpc netty client caught exception", cause);
        ctx.close();
    }
}
