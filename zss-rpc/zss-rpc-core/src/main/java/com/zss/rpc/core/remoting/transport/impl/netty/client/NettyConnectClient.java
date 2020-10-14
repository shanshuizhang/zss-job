package com.zss.rpc.core.remoting.transport.impl.netty.client;

import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.transport.common.ConnectClient;
import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyDecoder;
import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyEncoder;
import com.zss.rpc.core.remoting.transport.model.Beat;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.serialize.Serializer;
import com.zss.rpc.core.util.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyConnectClient extends ConnectClient {

    private static NioEventLoopGroup nioEventLoopGroup;

    private Channel channel;

    @Override
    public void init(String address, Serializer serializer, RpcInvoker rpcInvoker) throws Exception {
        // address
        Object[] array = IpUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];
        // init group
        if(nioEventLoopGroup == null){
            synchronized (NettyConnectClient.class){
                if(nioEventLoopGroup == null){
                    nioEventLoopGroup = new NioEventLoopGroup();
                    rpcInvoker.addStopCallBack(()->{
                        nioEventLoopGroup.shutdownGracefully();
                    });
                }
            }
        }
        // init client
        final NettyConnectClient connectClient = this;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0,0, Beat.BEAT_INTERVAL, TimeUnit.SECONDS))
                                .addLast(new NettyEncoder(serializer))
                                .addLast(new NettyDecoder(serializer))
                                .addLast(new NettyClientHandler(connectClient,rpcInvoker));
                    }
                })
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10 * 1000);
        this.channel = bootstrap.connect(host,port).sync().channel();
        //如果是无效连接，则关闭
        if(!isValidate()){
            close();
            return;
        }
        logger.debug("zss-rpc netty client proxy, connect to server success at host:{}, port:{}", host, port);
    }

    @Override
    public void close() {
        if(this.channel != null && this.channel.isActive()){
            this.channel.close();
        }
        logger.debug("zss-rpc netty client close.");
    }

    @Override
    public boolean isValidate() {
        if(this.channel != null){
            return this.channel.isActive();
        }
        return false;
    }

    @Override
    public void send(RpcRequest rpcRequest) throws Exception {
        this.channel.writeAndFlush(rpcRequest).sync();
    }
}
