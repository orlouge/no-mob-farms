package io.github.orlouge.nomobfarm.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.orlouge.nomobfarm.TrackedMobOrigin;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.SerializedChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.chunk.SerializedChunk.class)
public class ChunkSerializerMixin {
    @Unique
    private NbtCompound nomobfarm$serializedMobDeaths = null;

    @Inject(method = "fromNbt", at = @At("RETURN"))
    private static void onFromNbt(HeightLimitView world, DynamicRegistryManager registryManager, NbtCompound nbt, CallbackInfoReturnable<SerializedChunk> cir) {
        ((ChunkSerializerMixin) (Object) cir.getReturnValue()).nomobfarm$serializedMobDeaths = nbt.getCompound("NoMobFarmsData").orElse(null);
    }

    @Inject(method = "fromChunk", at = @At("RETURN"))
    private static void onFromChunk(ServerWorld world, Chunk chunk, CallbackInfoReturnable<SerializedChunk> cir) {
        if (chunk instanceof TrackedMobOrigin tracked) {
            ((ChunkSerializerMixin) (Object) cir.getReturnValue()).nomobfarm$serializedMobDeaths = tracked.getMobDeathScoreAlgorithm().toNbt();
        }
    }

    @Inject(method = "convert", at = @At("RETURN"))
    private void onConvert(ServerWorld world, PointOfInterestStorage poiStorage, StorageKey key, ChunkPos expectedPos, CallbackInfoReturnable<ProtoChunk> cir) {
        if (nomobfarm$serializedMobDeaths == null) return;
        ProtoChunk ret = cir.getReturnValue();
        Chunk chunk = ret instanceof WrapperProtoChunk ? ((WrapperProtoChunk) ret).getWrappedChunk() : ret;
        if (chunk instanceof TrackedMobOrigin tracked) {
            tracked.getMobDeathScoreAlgorithm().readNbt(nomobfarm$serializedMobDeaths);
        }
    }

    @ModifyReturnValue(method = "serialize", at = @At("RETURN"))
    private NbtCompound onSerialize(NbtCompound original) {
        if (nomobfarm$serializedMobDeaths != null) original.put("NoMobFarmsData", nomobfarm$serializedMobDeaths);
        return original;
    }
}
