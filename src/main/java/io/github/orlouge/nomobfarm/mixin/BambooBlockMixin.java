package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(BambooBlock.class)
public class BambooBlockMixin extends Block {
    public BambooBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        if (NoMobFarmMod.BAMBOO_DROP_REQUIRES_PLAYER && builder.getNullable(LootContextParameters.THIS_ENTITY) == null && NoMobFarmMod.allowedCropDrops < 1) {
            return Collections.emptyList();
        } else {
            NoMobFarmMod.allowedCropDrops += 3;
            return super.getDroppedStacks(state, builder);
        }
    }
}
