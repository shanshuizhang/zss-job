package com.zss.rpc.core.test.serialize;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.remoting.transport.model.RpcFutureResponse;
import com.zss.rpc.core.remoting.transport.model.RpcRequest;
import com.zss.rpc.core.remoting.transport.model.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestRpcFutureResponse {
    public static void main(String[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123");
        RpcFutureResponse rpcFutureResponse = new RpcFutureResponse(rpcRequest);
        long start = System.currentTimeMillis();

        Thread thread1 = new Thread(()->{
            try {
                RpcResponse rpcResponse = rpcFutureResponse.get(3*1000L,TimeUnit.MILLISECONDS);
                System.out.println(String.format("获取耗时：%d，response:%s",(System.currentTimeMillis() - start),rpcResponse));
            } catch (InterruptedException e) {
                System.out.println("中断异常");
            } catch (ExecutionException e) {
                System.out.println("ExecutionException 异常");
            } catch (TimeoutException e){
                System.out.println("超时异常,e:"+e.getStackTrace());
            } catch (RpcException e){
                System.out.println(e.getMessage());
            }
        });

        Thread thread2 = new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(rpcRequest.getRequestId());
            rpcFutureResponse.setRpcResponse(rpcResponse);
        });

        thread1.start();
        thread2.start();

        try {
            TimeUnit.SECONDS.sleep(2L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //thread1.interrupt();
        System.out.println("主线程结束,耗时："+ (System.currentTimeMillis() - start));
    }
}
