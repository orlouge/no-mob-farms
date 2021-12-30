package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.item.ItemStack;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.entity.raid.RaiderEntity.class)
public abstract class RaiderEntityMixin {
    private boolean wasRaider = false;

    @ModifyVariable(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V",
                    at = @At("STORE"),
                    ordinal = 0)
    public ItemStack modifyBannerSlotForBadOmen(ItemStack stack) {
        if (wasRaider && NoMobFarmMod.RAID_NO_BAD_OMEN_LOOP) {
            return ItemStack.EMPTY;
        } else {
            return stack;
        }
    }

    @Inject(method = "setRaid(Lnet/minecraft/village/raid/Raid;)V",
            at = @At("HEAD"))
    public void onSetRaid(Raid raid, CallbackInfo ci) {
        if (raid != null) {
            wasRaider = true;
        }
    }
}
