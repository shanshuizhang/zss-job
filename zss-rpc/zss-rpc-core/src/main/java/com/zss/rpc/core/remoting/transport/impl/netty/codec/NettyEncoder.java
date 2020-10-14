package com.zss.rpc.core.remoting.transport.impl.netty.codec;

import com.zss.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;

    public NettyEncoder(Serializer serializer){
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        byte[] data = serializer.serialize(in);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
