package com.facecam;

import com.facecam.network.RecordPayload;
import com.facecam.network.StartCameraPayload;
import com.facecam.network.StopCameraPayload;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.DustParticleOptions;

import net.minecraft.network.chat.Component;
import java.util.List;

public class MCFacecam implements ModInitializer {
	public static final String MOD_ID = "mc-facecam";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(StopCameraPayload.TYPE, StopCameraPayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(StartCameraPayload.TYPE, StartCameraPayload.CODEC);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("livestream")
					.then(Commands.literal("stop").executes(context -> {
						ServerPlayer player = context.getSource().getPlayer();
						if (player != null) {
							ServerPlayNetworking.send(player, new StopCameraPayload());
						}
						context.getSource().sendSuccess(() -> Component.literal("Livestream stopped."), false);
						return 1;
					}))
					.then(Commands.argument("x", IntegerArgumentType.integer())
							.then(Commands.argument("y", IntegerArgumentType.integer())
									.then(Commands.argument("z", IntegerArgumentType.integer())
											.executes(context -> {
												int x = IntegerArgumentType.getInteger(context, "x");
												int y = IntegerArgumentType.getInteger(context, "y");
												int z = IntegerArgumentType.getInteger(context, "z");

												ServerPlayer player = context.getSource().getPlayer();
												if (player != null) {
													ServerPlayNetworking.send(player, new StartCameraPayload(x, y, z));
												}
												context.getSource().sendSuccess(() -> Component.literal(
														"Starting livestream at [" + x + ", " + y + ", " + z + "]"),
														false);
												return 1;
											})))));
		});
		PayloadTypeRegistry.serverboundPlay().register(RecordPayload.TYPE, RecordPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(RecordPayload.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				ServerLevel world = (ServerLevel) context.player().level();

				int startX = payload.x();
				int startY = payload.y();
				int startZ = payload.z();
				int width = payload.width();
				int height = payload.height();
				List<Integer> pixels = payload.pixels();

				for (int py = 0; py < height; py++) {
					for (int px = 0; px < width; px++) {
						int argb = pixels.get(py * width + px);
						int r = (argb >> 16) & 0xFF;
						int g = (argb >> 8) & 0xFF;
						int b = argb & 0xFF;

						if (r < 10 && g < 10 && b < 10)
							continue;

						int colorInt = (r << 16) | (g << 8) | b;
						DustParticleOptions dustOptions = new DustParticleOptions(colorInt, 1.5f);

						world.sendParticles(
								dustOptions,
								startX + (px * 0.04),
								startY + ((height - py) * 0.04),
								startZ,
								1,
								0.0, 0.0, 0.0,
								1.0);
					}
				}
			});
		});
	}
}