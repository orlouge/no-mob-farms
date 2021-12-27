package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.HasBirthChunk;
import io.github.orlouge.nomobfarm.MobDeathScoreTracker;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
	@ModifyVariable(
			at = @At("STORE"),
			ordinal = 0,
			method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V"
	)
	private static PlayerEntity preventSpawn(PlayerEntity entity, SpawnGroup group, ServerWorld world, Chunk chunk) {
		if (chunk instanceof  MobDeathScoreTracker && !((MobDeathScoreTracker) chunk).acceptableMobDeathScore()) {
			return null;
		}
		return entity;
	}

	@ModifyVariable(
			at = @At("STORE"),
			ordinal = 0,
			method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V"
	)
	private static MobEntity setBirthChunk(MobEntity entity, SpawnGroup group, ServerWorld world, Chunk chunk) {
		if (entity != null && entity instanceof HasBirthChunk) {
			((HasBirthChunk) entity).setBirthChunk(chunk);
		}
		return entity;
	}
}
