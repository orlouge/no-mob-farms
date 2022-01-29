package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.HasTrackedOrigin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(
            method = "convertTo(Lnet/minecraft/entity/EntityType;Z)Lnet/minecraft/entity/mob/MobEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;copyPositionAndRotation(Lnet/minecraft/entity/Entity;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public <T extends MobEntity> void modifyConvertedMobEntity(EntityType<T> entityType, boolean keepEquipment, CallbackInfoReturnable<T> cir, T entity) {
        if (entity != null && this instanceof HasTrackedOrigin && entity instanceof HasTrackedOrigin) {
            ((HasTrackedOrigin) entity).setOrigin(((HasTrackedOrigin) this).getOrigin());
        }
    }
}
