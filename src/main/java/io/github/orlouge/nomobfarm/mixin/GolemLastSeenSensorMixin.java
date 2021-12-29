package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor.class)
public class GolemLastSeenSensorMixin {
    @ModifyArg(method = "rememberIronGolem(Lnet/minecraft/entity/LivingEntity;)V",
               at = @At(value = "INVOKE", target="Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;J)V"),
               index = 2)
    private static long modifyGolemDetectedExpiry(long expiry) {
        return expiry + NoMobFarmMod.GOLEM_DETECTION_MEMORY;
    }
}
