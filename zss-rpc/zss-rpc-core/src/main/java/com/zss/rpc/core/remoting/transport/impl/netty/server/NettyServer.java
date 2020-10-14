package com.zss.rpc.core.remoting.transport.impl.netty.server;

import com.zss.rpc.core.remoting.provider.RpcProvider;
import com.zss.rpc.core.remoting.transport.BaseServer;
import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyDecoder;
import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyEncoder;
import com.zss.rpc.core.remoting.transport.model.Beat;
import com.zss.rpc.core.util.ThreadPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyServer extends BaseServer {

    private Thread thread;

    @Override
    public void start(RpcProvider rpcProvider) throws Exception {
        thread = new Thread(()->{
            final int port = rpcProvider.getPort();
            final ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.buildServerThreadPool(
                    NettyServer.class.getSimpleName(),
                    rpcProvider.getCorePoolSize(),
                    rpcProvider.getMaxPoolSize()
            );
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                // start server
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup,workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new IdleStateHandler(0,0, Beat.BEAT_INTERVAL * 3, TimeUnit.SECONDS))
                                        .addLast(new NettyDecoder(rpcProvider.getSerializerInstance()))
                                        .addLast(new NettyEncoder(rpcProvider.getSerializerInstance()))
                                        .addLast(new NettyServerHandler(rpcProvider,threadPoolExecutor));
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY,true)
                        .childOption(ChannelOption.SO_KEEPALIVE,true);
                // bind port
                ChannelFuture future = serverBootstrap.bind(port).sync();
                logger.info("zss-rpc remoting server start success, nettype = {}, port = {}", NettyServer.class.getName(), port);
                // 启动服务时，回调
                onStarted();
                // wait util stop
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if(e instanceof InterruptedException){
                    logger.info("zss-rpc remoting server stop.");
                } else{
                    logger.error("zss-rpc remoting server error.",e);
                }
            } finally {
                // 关闭线程池，释放资源
                try{
                    threadPoolExecutor.shutdown();
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
                try{
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        });
        thread.setName("netty-server-start");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        //中断服务线程
        if(thread != null && thread.isAlive()){
            thread.interrupt();
        }
        //停止服务时，回调
        onStoped();
        logger.info("zss-rpc remoting server destroy success.");
    }
}
