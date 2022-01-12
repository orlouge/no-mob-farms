package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(net.minecraft.world.MobSpawnerLogic.class)
public class MobSpawnerLogicMixin implements TrackedMobOrigin, BasicMobDeathScoreAlgorithm.BasicMobDeathScoreAlgorithmNotify {
    private Box detectionBox = null;
    protected MobDeathScoreAlgorithm deathScore = new BasicMobDeathScoreAlgorithm(slowdownFunction(NoMobFarmMod.SPAWNER_SLOWDOWN_NEAR_RATE,
                                                                                                   NoMobFarmMod.SPAWNER_SLOWDOWN_FAR_RATE),
                                                                                  NoMobFarmMod.SPAWNER_MAX_WAIT,
                                                                                  NoMobFarmMod.SPAWNER_RECOVERY_RATE,
                                                                                  NoMobFarmMod.SPAWNER_MIN_DEATHS,
                                                                                  NoMobFarmMod.SPAWNER_MAX_DEATHS,
                                                                                  NoMobFarmMod.SPAWNER_OFFLINE_PERSISTENCE,
                                                                                  this);

    @Shadow
    private int spawnRange;

    @Override
    public MobDeathScoreAlgorithm getMobDeathScoreAlgorithm() {
        return deathScore;
    }

    private boolean setDirty = false;



    @Inject(method= "serverTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("HEAD"),
            cancellable = true)
    public void onTick(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        deathScore.tick();
        if (!deathScore.acceptableScore()) {
            ci.cancel();
        }
        if (setDirty) {
            world.getBlockEntity(pos).markDirty();
            setDirty = false;
        }
    }

    @ModifyVariable(method= "serverTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V",
                    at = @At("STORE"),
                    ordinal = 0)
    public Entity onMobsAboutToSpawn(Entity entity, ServerWorld world, BlockPos pos) {
        detectionBox = new Box(
                (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(),
                (double)(pos.getX() + 1), (double) (pos.getY() + 1), (double) (pos.getZ() + 1)
        ).expand((double) this.spawnRange);

        if (entity != null && entity instanceof HasTrackedOrigin) {
            ((HasTrackedOrigin) entity).setOrigin(this);
        }
        return entity;
    }

    @Inject(method = "readNbt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("HEAD"))
    public void onReadNbt(World world, BlockPos pos, NbtCompound nbt, CallbackInfo ci) {
        deathScore.readNbt(nbt);
    }

    @Inject(method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;",
            at = @At("HEAD"))
    public void onWriteNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        deathScore.writeNbt(nbt);
    }

    @Override
    public void notifyLargeScoreChange() {
        setDirty = true;
    }

    private Function<LivingEntity, Integer> slowdownFunction(int nearRate, int farRate) {
        return (entity) -> {
            if (detectionBox == null || entity.getBoundingBox().intersects(detectionBox)) {
                return nearRate;
            } else {
                return farRate;
            }
        };
    }
}
