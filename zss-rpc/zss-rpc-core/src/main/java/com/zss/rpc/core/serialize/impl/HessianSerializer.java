package com.zss.rpc.core.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化
 */
public class HessianSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(outputStream);
        try {
            hessianOutput.writeObject(object);
            hessianOutput.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            try {
                hessianOutput.close();
            } catch (IOException e) {
                throw new RpcException(e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RpcException(e);
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput(inputStream);
        try {
            return (T)hessianInput.readObject();
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            try {
                hessianInput.close();
            } catch (Exception e) {
                throw new RpcException(e);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RpcException(e);
            }
        }
    }
}
