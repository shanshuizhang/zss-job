package com.zss.rpc.core.remoting.transport.model;

import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.invoker.call.RpcInvokeCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcFutureResponse implements Future<RpcResponse> {

    private RpcInvoker rpcInvoker;

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    private RpcInvokeCallback rpcInvokeCallback;

    private boolean done = false;

    private Object lock = new Object();

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public RpcInvokeCallback getRpcInvokeCallback() {
        return rpcInvokeCallback;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
        synchronized (lock){
            done = true;
            lock.notifyAll();
        }
    }
    @Deprecated
    public RpcFutureResponse(RpcRequest rpcRequest){
        this.rpcRequest = rpcRequest;
    }
    public RpcFutureResponse(final RpcInvoker rpcInvoker, RpcRequest rpcRequest, RpcInvokeCallback rpcInvokeCallback) {
        this.rpcInvoker = rpcInvoker;
        this.rpcRequest = rpcRequest;
        this.rpcInvokeCallback = rpcInvokeCallback;

        setInvokerFuture();
    }

    public void setInvokerFuture(){
        this.rpcInvoker.setInvokerFuture(rpcRequest.getRequestId(),this);
    }

    public void removeInvokerFuture(){
        this.rpcInvoker.removeInvokerFuture(rpcRequest.getRequestId());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        try {
            return this.get(-1,TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        //response 没有完成，当前线程则等待
        if(!done){
            synchronized (lock){
                if(timeout < 0){
                    lock.wait();
                } else {
                    long timeoutMillis = (TimeUnit.MILLISECONDS == unit) ? timeout : TimeUnit.MILLISECONDS.convert(timeout , unit);
                    lock.wait(timeoutMillis);
                }

            }
        }
        //等待超时，抛出超时异常
        if(!done){
            throw new RpcException(String.format("zss-rpc, request timeout at:[%d],request:[%s]",System.currentTimeMillis(),rpcRequest.toString()));
        }
        //已完成，则返回响应
        return rpcResponse;
    }
}
