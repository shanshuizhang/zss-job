package com.zss.rpc.core.test.serialize;

import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyDecoder;
import com.zss.rpc.core.remoting.transport.impl.netty.codec.NettyEncoder;
import com.zss.rpc.core.remoting.transport.model.Beat;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TestNettyServer {

    public static void main(String[] args) throws Exception {
        //启动server，client服务
        NettyServer server = new NettyServer();
        NettyClient client = new NettyClient();

        //test(server,client);
        testTimeout(server,client);
    }

    public static void test(NettyServer server,NettyClient client) throws Exception {
        server.start();
        client.start();
        //client发送请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123456");
        rpcRequest.setVersion("V0");
        //main 线程休眠2秒，等待client channel初始化成功，不然报NPE
        TimeUnit.SECONDS.sleep(2);
        client.send(rpcRequest);
        //client循环发送10此请求
        int i = 0;
        while (i < 10){
            TimeUnit.SECONDS.sleep(3);
            i++;
            rpcRequest.setVersion(rpcRequest.getVersion() + "-" + i);
            client.send(rpcRequest);
            System.out.println(String.format("client 第 %d 次 发送请求 当前时间 %d",i,System.currentTimeMillis()));
        }
    }

    public static void testTimeout(NettyServer server,NettyClient client) throws InterruptedException {
        server.start();
        TimeUnit.SECONDS.sleep(100);
        //client.start();
        TimeUnit.SECONDS.sleep(3);
    }
}

class NettyServer{

    private Thread thread;

    private static final int PORT = 8888;

    public void start(){
        thread = new Thread(()->{
            System.out.println(String.format("%s 线程启动成功",Thread.currentThread().getName()));
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup,workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(new IdleStateHandler(0,0,90,TimeUnit.SECONDS))
                                        .addLast(new NettyDecoder(new Hessian2Serializer()))
                                        .addLast(new NettyEncoder(new Hessian2Serializer()))
                                        .addLast(new NettyServerHandler());
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY,true)
                        .childOption(ChannelOption.SO_KEEPALIVE,true);

                ChannelFuture future = serverBootstrap.bind(PORT).sync();
                System.out.println(String.format("netty server start success ,port:%d",PORT));
                // wait util stop
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if(e instanceof InterruptedException){
                    System.out.println("netty server stop.");
                } else{
                    System.out.println("netty server error.,errorMsg:"+e.getMessage());
                }
            } finally {
                try{
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }catch (Exception e){
                    System.out.println("错误信息e:"+e.getMessage());
                }
            }
        });
        thread.setName("netty-server-thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(){
        if(thread != null && thread.isAlive()){
            thread.interrupt();
        }
        System.out.println(String.format("当前线程：%s,执行方法stop成功",Thread.currentThread().getName()));
    }
}

class NettyClient{

    private Thread thread;

    private Channel channel;

    private static final String IP = "127.0.0.1";

    private static final int PORT = 8888;

    public void start(){
        thread = new Thread(()->{
            System.out.println(String.format("%s 线程启动成功",Thread.currentThread().getName()));
            NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(new IdleStateHandler(0,0,30,TimeUnit.SECONDS))
                                        .addLast(new NettyEncoder(new Hessian2Serializer()))
                                        .addLast(new NettyDecoder(new Hessian2Serializer()))
                                        .addLast(new NettyClientHandler());
                            }
                        })
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

                this.channel = bootstrap.connect(IP,PORT).sync().channel();

                //如果是无效连接，则关闭
                if(!isValidate()){
                    close();
                    return;
                }
                System.out.println(String.format("netty client start success ,ip:%s ,port:%d",IP,PORT));
            } catch (InterruptedException e) {
                if(e instanceof InterruptedException){
                    System.out.println("netty client stop.");
                } else{
                    System.out.println("netty client error.,errorMsg:"+e.getMessage());
                }
            }
        });
        thread.setName("netty-client-thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(){
        if(thread != null && thread.isAlive()){
            thread.isInterrupted();
        }
        System.out.println(String.format("当前线程：%s,执行方法stop成功",Thread.currentThread().getName()));
    }

    public boolean isValidate() {
        if(this.channel != null){
            return this.channel.isActive();
        }
        return false;
    }

    public void close(){
        if(this.channel != null && this.channel.isActive()){
            this.channel.close();
        }
        System.out.println("netty client close.");
    }

    public void send(RpcRequest rpcRequest) throws Exception {
        this.channel.writeAndFlush(rpcRequest).sync();
        System.out.println("client send request...");
    }
}

class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        if(Beat.BEAT_ID.equalsIgnoreCase(msg.getRequestId())){
            System.out.println("netty server read beat-ping. 当前时间：" + System.currentTimeMillis());
            return;
        }
        try {
            System.out.println("server 接收到请求，请求信息" + msg);
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(msg.getRequestId());
            rpcResponse.setResult("响应成功");
            ctx.writeAndFlush(rpcResponse);
            System.out.println("server 响应 send response");
        } catch (Exception e){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(msg.getRequestId());
            rpcResponse.setErrorMsg(e.getMessage());
            ctx.writeAndFlush(rpcResponse);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            ctx.channel().close();      // beat 3N, close if idle
            System.out.println("netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(String.format("当前线程 %s,netty server caught exception %s",Thread.currentThread().getName(),cause.getStackTrace()));
        ctx.close();
    }
}

class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        System.out.println("client 接收到响应，响应信息" + msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            ctx.writeAndFlush(Beat.BEAT_PING);
            System.out.println("netty client send beat-ping. 当前时间：" + System.currentTimeMillis());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(String.format("当前线程 %s,netty client caught exception %s",Thread.currentThread().getName(),cause.getStackTrace()));
        ctx.close();
    }
}
