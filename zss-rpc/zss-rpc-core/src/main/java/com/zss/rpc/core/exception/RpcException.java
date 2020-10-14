package com.zss.rpc.core.exception;

public class RpcException extends RuntimeException{

    private static final long serialVersionUID = 100L;

    public RpcException(String message){
        super(message);
    }

    public RpcException(Throwable cause){
        super(cause);
    }

    public RpcException(String message,Throwable cause){
        super(message,cause);
    }
}
