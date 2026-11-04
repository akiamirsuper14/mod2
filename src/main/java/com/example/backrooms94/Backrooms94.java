package com.example.backrooms94;

import com.example.backrooms94.worldgen.ModFeatures;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;

@Mod(Backrooms94.MOD_ID)
public class Backrooms94 {
    public static final String MOD_ID = "backrooms94";
    public static final ResourceKey<Level> LEVEL_94 = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation(MOD_ID, "level_94"));

    public Backrooms94() {
        ModFeatures.FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("level94")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("enter").executes(context -> enter(context.getSource())))
                .then(Commands.literal("exit").executes(context -> exit(context.getSource())))
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal("Используй /level94 enter или /level94 exit"), false);
                    return 1;
                }));
    }

    private int enter(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel destination = source.getServer().getLevel(LEVEL_94);
        if (destination == null) {
            source.sendFailure(Component.literal("Измерение Level 94 не загружено. Создай/перезапусти мир с установленным модом."));
            return 0;
        }
        teleportToSafeSurface(player, destination, 0, 0);
        source.sendSuccess(() -> Component.literal("Ты вошёл в Level 94 — Motion."), false);
        return 1;
    }

    private int exit(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MinecraftServer server = source.getServer();
        ServerLevel overworld = server.overworld();
        if (player.level().dimension() == Level.OVERWORLD) {
            source.sendFailure(Component.literal("Ты уже в обычном мире."));
            return 0;
        }
        var spawn = overworld.getSharedSpawnPos();
        teleportToSafeSurface(player, overworld, spawn.getX(), spawn.getZ());
        source.sendSuccess(() -> Component.literal("Ты вернулся в обычный мир."), false);
        return 1;
    }

    private void teleportToSafeSurface(ServerPlayer player, ServerLevel destination, int x, int z) {
        int y = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        player.teleportTo(destination, x + 0.5, y + 1.0, z + 0.5,
                Set.of(), player.getYRot(), player.getXRot());
    }
}
