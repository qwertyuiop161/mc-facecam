package com.facecam.client.client;

import com.facecam.network.RecordPayload;
import com.github.sarxos.webcam.Webcam;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ClientStreamManager {
    private static boolean isStreaming = false;
    private static Webcam webcam = null;
    private static int targetX, targetY, targetZ;
    private static int streamWidth = 128;
    private static int streamHeight = 128;
    private static int tickInterval = 5;
    private static int tickCounter = 0;

    public static void startStreaming(int x, int y, int z, int pixelX, int pixelY, int framerate) {
        targetX = x;
        targetY = y;
        targetZ = z;
        streamWidth = pixelX;
        streamHeight = pixelY;
        tickInterval = Math.max(1, 20 / Math.max(1, framerate));
        tickCounter = 0;
        
        try {
            if (webcam == null) {
                webcam = Webcam.getDefault();
            }
            if (webcam != null && !webcam.isOpen()) {
                webcam.open();
            }
            isStreaming = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopStreaming() {
        isStreaming = false;
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    public static void registerHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isStreaming || webcam == null) return;

            tickCounter++;
            if (tickCounter < tickInterval) return;
            tickCounter = 0;

            try {
                BufferedImage rawFrame = webcam.getImage();
                if (rawFrame == null) return;

                BufferedImage scaledImage = new BufferedImage(streamWidth, streamHeight, BufferedImage.TYPE_INT_RGB);
                var g2d = scaledImage.createGraphics();
                g2d.drawImage(rawFrame, 0, 0, streamWidth, streamHeight, null);
                g2d.dispose();

                int totalPixels = streamWidth * streamHeight;
                List<Integer> pixels = new ArrayList<>(totalPixels);
                for (int py = 0; py < streamHeight; py++) {
                    for (int px = 0; px < streamWidth; px++) {
                        pixels.add(scaledImage.getRGB(px, py));
                    }
                }

                ClientPlayNetworking.send(new RecordPayload(pixels, streamWidth, streamHeight, targetX, targetY, targetZ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
