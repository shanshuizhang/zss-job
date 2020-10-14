package com.zss.rpc.core.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.zss.rpc.core.exception.RpcException;
import com.zss.rpc.core.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian2序列化
 */
public class Hessian2Serializer implements Serializer {

    @Override
    public <T> byte[] serialize(T object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        try {
            hessian2Output.writeObject(object);
            hessian2Output.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            try {
                hessian2Output.close();
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
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        try {
            return (T)hessian2Input.readObject();
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            try {
                hessian2Input.close();
            } catch (IOException e) {
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
