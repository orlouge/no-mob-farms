package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.*;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.MobSpawnerLogic.class)
public class MobSpawnerLogicMixin implements TrackedMobOrigin, BasicMobDeathScoreAlgorithm.BasicMobDeathScoreAlgorithmNotify {
    protected MobDeathScoreAlgorithm deathScore = new BasicMobDeathScoreAlgorithm(NoMobFarmMod.SPAWNER_SLOWDOWN_RATE,
                                                                                  NoMobFarmMod.SPAWNER_MAX_WAIT,
                                                                                  NoMobFarmMod.SPAWNER_RECOVERY_RATE,
                                                                                  NoMobFarmMod.SPAWNER_MIN_DEATHS,
                                                                                  this);

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
    public Entity onMobsAboutToSpawn(Entity entity) {
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
}
