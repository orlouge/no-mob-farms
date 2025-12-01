package io.github.orlouge.nomobfarm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
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
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;<init>(Lnet/minecraft/util/Identifier;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V"),
            index = 1
    )
    public double modifyCallerReinforcementCharge(double charge) {
        return charge - NoMobFarmMod.REINFORCEMENT_PENALTY;
    }

    @ModifyArg(
        method = "damage",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;addPersistentModifier(Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", ordinal = 1)
    )

    public EntityAttributeModifier modifyCalleeReinforcementCharge(EntityAttributeModifier modifier) {
        return new EntityAttributeModifier(Identifier.ofVanilla("reinforcement_callee_charge"), -0.05 - NoMobFarmMod.REINFORCEMENT_PENALTY, EntityAttributeModifier.Operation.ADD_VALUE);
    }
}
