package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.MobDeathScoreTracker;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.world.ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            at = @At("HEAD"))
    private void tickMobDeathScore(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ((MobDeathScoreTracker) chunk).tickMobDeathScore();
    }
}
