package com.facecam.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.facecam.MCFacecam;

public record StopCameraPayload() implements CustomPacketPayload {
    public static final Identifier ID = MCFacecam.id("stop_camera");
    public static final CustomPacketPayload.Type<StopCameraPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, StopCameraPayload> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {},
        buf -> new StopCameraPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}