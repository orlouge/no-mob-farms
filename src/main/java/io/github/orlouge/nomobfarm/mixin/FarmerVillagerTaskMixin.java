package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.NoMobFarmMod;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmerVillagerTask.class)
public class FarmerVillagerTaskMixin {
    @Redirect(
            method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z")
    )
    public boolean onVillagerBreakBlock(ServerWorld instance, BlockPos target, boolean drop, Entity breakingEntity, ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (!NoMobFarmMod.FARMER_INSTANT_PICKUP) {
            return serverWorld.breakBlock(target, drop, breakingEntity);
        }

        BlockState blockState = serverWorld.getBlockState(target);
        FluidState fluidState = serverWorld.getFluidState(target);

        if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
            serverWorld.syncWorldEvent(2001, target, Block.getRawIdFromState(blockState));
        }

        if (drop) {
            BlockEntity blockEntity = blockState.hasBlockEntity() ? serverWorld.getBlockEntity(target) : null;
            Block.getDroppedStacks(blockState, serverWorld, target, blockEntity, breakingEntity, ItemStack.EMPTY).forEach((stack) -> {
                if (villagerEntity.canGather(stack)) {
                    villagerEntity.getInventory().addStack(stack);
                }
            });
        }

        boolean blockEntity = serverWorld.setBlockState(target, fluidState.getBlockState(), 3);
        if (blockEntity) {
            serverWorld.emitGameEvent(breakingEntity, GameEvent.BLOCK_DESTROY, target);
        }

        return blockEntity;
    }
}
