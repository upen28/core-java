
package com.payment.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class CompositeByteBufDemo {
    public static void main(String... args) {

        ByteBuf buf1 = Unpooled.buffer(1);
        buf1.writeByte(1);

        ByteBuf buf2 = Unpooled.buffer(1);
        buf2.writeByte(2);

        ByteBuf buf3 = Unpooled.buffer(1);
        buf3.writeByte(3);

        ByteBuf buf4 = Unpooled.buffer(1);
        buf4.writeByte(4);

        ByteBuf buf5 = Unpooled.buffer(1);
        buf5.writeByte(5);

        CompositeByteBuf compBuffer = Unpooled.compositeBuffer();
        compBuffer.addComponent(buf1);
        compBuffer.addComponent(buf2);
        compBuffer.addComponent(buf3);
        compBuffer.addComponent(buf4);
        compBuffer.addComponent(buf5);

        System.out.println(compBuffer.numComponents());

        ByteBuf bb = compBuffer.internalComponent(3);

        System.out.println(bb.getByte(0));

        for (ByteBuf buff : compBuffer) {
            System.out.println(buff.readByte());
        }

        List<ByteBuf> groupList = new ArrayList<>();

        ByteBuf buf6 = Unpooled.buffer();
        buf6.writeBytes("81AA".getBytes());

        groupList.add(buf6);

        ByteBuf buf7 = Unpooled.buffer();
        buf7.writeBytes("82CAD413042".getBytes());
        groupList.add(buf7);

        ByteBuf buf8 = Unpooled.buffer();
        buf8.writeBytes("82EUR589781".getBytes());
        groupList.add(buf8);

        ByteBuf buf9 = Unpooled.buffer();
        buf9.writeBytes("82JPY511554613".getBytes());
        groupList.add(buf9);

        ByteBuf buf10 = Unpooled.buffer();
        buf10.writeBytes("82GBP579445".getBytes());
        groupList.add(buf10);

        Map<String, List<ByteBuf>> map = groupList.stream().map(item -> {
            CompositeByteBuf compositeBuffer = null;
            try {
                ByteBuf groupIdByteBuf = Unpooled.buffer();
                groupIdByteBuf.writeBytes(item, 2);

                int remaining = item.readableBytes();
                ByteBuf groupIdValueByteBuf = Unpooled.buffer(remaining);
                groupIdValueByteBuf.writeBytes(item, remaining);

                compositeBuffer = Unpooled.compositeBuffer();
                // compositeBuffer.addComponent(true, 0, groupIdByteBuf);
                // compositeBuffer.addComponent(true, 1, groupIdValueByteBuf);
            } finally {
                item.release();
            }
            return compositeBuffer;

        }).collect(Collectors.groupingBy(buffer -> {
            byte[] gId = new byte[2];
            buffer.getBytes(0, gId, 0, 2);
            return new String(gId);
        }));

        System.out.println(map);
        System.out.println(map.size());

        map.values().stream().forEach(ls -> {
            ls.stream().forEach(bf -> {
                byte[] dst = new byte[bf.readableBytes()];
                bf.readBytes(dst);
                System.out.println(new String(dst));
            });
        });

    }
}
