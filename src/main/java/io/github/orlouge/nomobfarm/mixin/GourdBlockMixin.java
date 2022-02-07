package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(GourdBlock.class)
public abstract class GourdBlockMixin extends Block {
    public GourdBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        if (NoMobFarmMod.PISTON_MOVES_GOURD) {
            return PistonBehavior.NORMAL;
        } else {
            return super.getPistonBehavior(state);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        if (NoMobFarmMod.GOURD_DROP_REQUIRES_PLAYER && builder.getNullable(LootContextParameters.THIS_ENTITY) == null) {
            return Collections.emptyList();
        } else {
            return super.getDroppedStacks(state, builder);
        }
    }
}
