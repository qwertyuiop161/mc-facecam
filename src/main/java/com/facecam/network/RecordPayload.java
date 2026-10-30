package com.facecam.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.facecam.MCFacecam;

import java.util.ArrayList;
import java.util.List;

public record RecordPayload(List<Integer> pixels, int width, int height, int x, int y, int z) implements CustomPacketPayload {
    public static final Identifier ID = MCFacecam.id("summon_camera");
    public static final CustomPacketPayload.Type<RecordPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RecordPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT, 1000000),
        RecordPayload::pixels,
        ByteBufCodecs.VAR_INT, RecordPayload::width,
        ByteBufCodecs.VAR_INT, RecordPayload::height,
        ByteBufCodecs.VAR_INT, RecordPayload::x,
        ByteBufCodecs.VAR_INT, RecordPayload::y,
        ByteBufCodecs.VAR_INT, RecordPayload::z,
        RecordPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}