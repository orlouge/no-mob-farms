package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;<init>(Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V"),
            index = 1,
            expect = 2
    )
    public double modifyReinforcementCharge(double charge) {
        return charge - NoMobFarmMod.REINFORCEMENT_PENALTY;
    }

    @Inject(
            method = "convertTo(Lnet/minecraft/entity/EntityType;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;applyAttributeModifiers(F)V"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void applyPenaltyAfterConversion(EntityType<? extends ZombieEntity> entityType, CallbackInfo ci, ZombieEntity convertedZombie) {
        if (NoMobFarmMod.REINFORCEMENT_PENALTY_CONVERSION) {
            convertedZombie.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setFrom(
                    this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
            );
        }
    }
}
