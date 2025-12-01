package io.github.orlouge.nomobfarm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.orlouge.nomobfarm.HasTrackedOrigin;
import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @ModifyExpressionValue(
        method = "convertTo(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/conversion/EntityConversionContext;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/conversion/EntityConversionContext$Finalizer;)Lnet/minecraft/entity/mob/MobEntity;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;Lnet/minecraft/entity/SpawnReason;)Lnet/minecraft/entity/Entity;")
    )
    public <T extends Entity> T applyZombiePenaltyAfterConversion(T converted) {
        if (NoMobFarmMod.REINFORCEMENT_PENALTY_CONVERSION && (Object) this instanceof ZombieEntity zombie && converted instanceof ZombieEntity convertedZombie) {
            convertedZombie.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setFrom(
                zombie.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS)
            );
        }
        if (this instanceof HasTrackedOrigin self && converted instanceof HasTrackedOrigin other) {
            other.setOrigin(self.getOrigin());
        }
        return converted;
    }
}
