package com.facecam.network;

import com.facecam.MCFacecam;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StartCameraPayload(int x, int y, int z, int pixelX, int pixelY, int framerate) implements CustomPacketPayload {
    public static final Identifier ID = MCFacecam.id("start_camera");
    public static final CustomPacketPayload.Type<StartCameraPayload> TYPE = new CustomPacketPayload.Type<>(ID);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, StartCameraPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, StartCameraPayload::x,
        ByteBufCodecs.VAR_INT, StartCameraPayload::y,
        ByteBufCodecs.VAR_INT, StartCameraPayload::z,
        ByteBufCodecs.VAR_INT, StartCameraPayload::pixelX,
        ByteBufCodecs.VAR_INT, StartCameraPayload::pixelY,
        ByteBufCodecs.VAR_INT, StartCameraPayload::framerate,
        StartCameraPayload::new
    );

    @Override 
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
