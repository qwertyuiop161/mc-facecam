package com.facecam.network;

import com.facecam.MCFacecam;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record RecordPayload(List<Integer> pixels, int width, int height, int x, int y, int z) implements CustomPacketPayload {
    public static final Identifier ID = MCFacecam.id("record_payload");
    public static final CustomPacketPayload.Type<RecordPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RecordPayload> CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.width());
            buf.writeVarInt(payload.height());
            buf.writeVarInt(payload.x());
            buf.writeVarInt(payload.y());
            buf.writeVarInt(payload.z());

            buf.writeVarInt(payload.pixels().size());
            for (int pixel : payload.pixels()) {
                buf.writeVarInt(pixel);
            }
        },
        buf -> {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            int x = buf.readVarInt();
            int y = buf.readVarInt();
            int z = buf.readVarInt();

            int maxAllowedSize = width * height * 2;
            int count = buf.readVarInt();

            if (count < 0 || count > maxAllowedSize) {
                throw new DecoderException("Pixel count " + count + " exceeds max allowed size of " + maxAllowedSize);
            }

            List<Integer> pixels = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                pixels.add(buf.readVarInt());
            }

            return new RecordPayload(pixels, width, height, x, y, z);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
