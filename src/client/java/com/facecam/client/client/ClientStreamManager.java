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
    private static int tickCounter = 0;

    public static void startStreaming(int x, int y, int z) {
        targetX = x;
        targetY = y;
        targetZ = z;
        
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
            if (tickCounter < 5) return;
            tickCounter = 0;

            try {
                BufferedImage rawFrame = webcam.getImage();
                if (rawFrame == null) return;

                BufferedImage scaledImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
                var g2d = scaledImage.createGraphics();
                g2d.drawImage(rawFrame, 0, 0, 512, 512, null);
                g2d.dispose();

                List<Integer> pixels = new ArrayList<>(1000000);
                for (int py = 0; py < 128; py++) {
                    for (int px = 0; px < 128; px++) {
                        pixels.add(scaledImage.getRGB(px, py));
                    }
                }

                ClientPlayNetworking.send(new RecordPayload(pixels, 512, 512, targetX, targetY, targetZ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}