package com.zss.rpc.core.remoting.transport.impl.netty.server;

import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.remoting.transport.model.Beat;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private RpcProvider rpcProvider;
    private ThreadPoolExecutor threadPoolExecutor;

    public NettyServerHandler(final RpcProvider rpcProvider, final ThreadPoolExecutor threadPoolExecutor) {
        this.rpcProvider = rpcProvider;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        if(Beat.BEAT_ID.equalsIgnoreCase(rpcRequest.getRequestId())){
            logger.debug("zss-rpc provider netty server read beat-ping.");
            return;
        }
        try {
            threadPoolExecutor.execute(()->{
                RpcResponse rpcResponse = rpcProvider.invokeService(rpcRequest);
                channelHandlerContext.writeAndFlush(rpcResponse);
            });
        } catch (Exception e){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(rpcRequest.getRequestId());
            rpcResponse.setErrorMsg(e.getMessage());
            channelHandlerContext.writeAndFlush(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("zss-rpc provider netty server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            ctx.channel().close();      // beat 3N, close if idle
            logger.debug("zss-rpc provider netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
