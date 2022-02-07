package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import io.github.orlouge.nomobfarm.TrackedMobOrigin;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(net.minecraft.server.world.ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            at = @At("HEAD"))
    private void tickMobDeathScore(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ((TrackedMobOrigin) chunk).getMobDeathScoreAlgorithm().tick();
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At("TAIL"))
    private void tickCropDrops(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        NoMobFarmMod.allowedCropDrops = Math.max(0, NoMobFarmMod.allowedCropDrops - 1);
    }
}
