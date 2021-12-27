package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.HasBirthChunk;
import io.github.orlouge.nomobfarm.MobDeathScoreTracker;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.entity.LivingEntity.class)
public class LivingEntityMixin implements HasBirthChunk {
    @Override
    public void setBirthChunk(Chunk birthChunk) {
        this.birthChunk = birthChunk;
    }

    private Chunk birthChunk = null;

    @Inject(
            at=@At("HEAD"),
            method= "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"
    )
    protected void onDeathCalled(CallbackInfo callbackInfo) {
        if (birthChunk != null) {
            ((MobDeathScoreTracker) birthChunk).increaseMobDeathScore();
        }
    }
}
