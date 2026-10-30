package com.facecam.client;

import com.facecam.client.client.ClientStreamManager;
import com.facecam.network.StartCameraPayload;
import com.facecam.network.StopCameraPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MCFacecamClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientStreamManager.registerHandlers();

        ClientPlayNetworking.registerGlobalReceiver(StartCameraPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientStreamManager.startStreaming(payload.x(), payload.y(), payload.z());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(StopCameraPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientStreamManager.stopStreaming();
            });
        });
    }
}