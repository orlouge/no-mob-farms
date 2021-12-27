package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.HasTrackedOrigin;
import io.github.orlouge.nomobfarm.TrackedMobOrigin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.entity.LivingEntity.class)
public class LivingEntityMixin implements HasTrackedOrigin {
    @Override
    public void setOrigin(TrackedMobOrigin origin) {
        this.origin = origin;
    }

    private TrackedMobOrigin origin = null;

    @Inject(
            at=@At("HEAD"),
            method= "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"
    )
    protected void onDeathCalled(CallbackInfo callbackInfo) {
        if (origin != null) {
            origin.getMobDeathScoreAlgorithm().signalDeath();
        }
    }
}
